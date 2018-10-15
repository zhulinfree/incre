package Test;
import java.util.Map.Entry;

import BplusTree.*;
import Data.*;
import OD.*;

import java.util.ArrayList;
import java.util.HashMap;


public class ReadandCheck {
	public static boolean debug=true;
	public final static int order = 10;
	public static int tn=0;//tn表示当前建立索引树的数目
	private final static String dataFileName=new String("data100k.csv");
	private final static String increFileName=new String("batchIncrementalData.csv");
	private final static String odFileName=new String("od2.txt");
	public static HashMap<ArrayList<String>,Integer> treeMap=new HashMap<>(); 
	public static ArrayList<BplusTree<InstanceKey, ArrayList<Integer>> > bptree=new ArrayList<>(); 
	public static CSVtoDataObject cdo = new CSVtoDataObject();
	private static CSVtoDataObject ind=new CSVtoDataObject();
	private static ODs od=new ODs();
	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),
			iObjectList=new ArrayList<DataStruct>();
	private static ArrayList<OrderDependency> originalODList=new ArrayList<OrderDependency>(),
			incorrectODList=new ArrayList<OrderDependency>(),
			enrichODList=new ArrayList<OrderDependency>();
	
	
public static void main(String[] args) {
		
   	    dataInitial();
		
		
		/*将读入的od输出*/
		System.out.println("The original od is:");
		od.print();
		System.out.println("共有"+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量数据");
	
	//iObjectList.size()
		for(int i=2;i<4;i++) {
			System.out.println("第"+i+"次插入..");
			checkAllOD(originalODList,i);
			
			printList(incorrectODList,"incorrrect list:");
			
			enrichment();
			
			printList(enrichODList,"\n\nNot checked enrichment ods is:");
			
			checkAllOD(enrichODList,i);
			
			printList(enrichODList,"\n\nThe enrichment ods is:");
			
			for(OrderDependency o:enrichODList) {
				od.ods.add(o);
			}
			listClear();
			
			if(od.ods.isEmpty()) {
				System.out.println("There is no od left in this data");
				return;
			}
			//将增量数据插入到原始数据中，为下一个增量数据做准备
			iObjectList.get(i).setId(Integer.toString(objectList.size()));
			objectList.add(iObjectList.get(i));
			Index.updateTrees(objectList.get(objectList.size()-1));
			
			System.out.println("第"+i+"次插入，od剩余"+od.ods.size()+"条");
			od.print();
			
		}
		
		System.out.println("\n\n\nThe latest ods is:");
		od.print();
		
	}
	
	
	private static void checkAllOD(ArrayList<OrderDependency> checkedOD,int increTuple) {
		//对每一条od进行验证
		for(OrderDependency nod:checkedOD) {
			
			int treeNumber=treeMap.getOrDefault(nod.getLHS(),-1);
			if(treeNumber==-1) {
				Index.buildTree(nod.getLHS());
				treeNumber=treeMap.getOrDefault(nod.getLHS(),-1);
			}
			if(debug) System.out.println("tree number= "+treeNumber);
			//将增量的数据放到原始数据集的最后一行
			iObjectList.get(increTuple).setId(Integer.toString(objectList.size()));
			objectList.add(iObjectList.get(increTuple));
			
			ArrayList<Integer> preList,nextList,curList,increList=new ArrayList<Integer>();
			
			//最后一行是增量数据
			increList.add(objectList.size()-1);
			
			InstanceKey key=new InstanceKey(nod.getLHS(),iObjectList.get(increTuple));
			curList=bptree.get(treeNumber).get(key);
			curList=curList==null?new ArrayList<Integer>():curList;
			
			
			Entry<InstanceKey,ArrayList<Integer>> pre=bptree.get(treeNumber).getPre(key,objectList.size()-1);
			preList=pre==null?new ArrayList<Integer>():pre.getValue();
			
			Entry<InstanceKey,ArrayList<Integer>> next=bptree.get(treeNumber).getNext(key,objectList.size()-1);
			nextList=next==null?new ArrayList<Integer>():next.getValue();
			
			if(debug) {
				System.out.print("\n\nThe current data is tuple: ");
				for(Integer i:curList) {
					System.out.print(i+" ");
				}
			}
				
		
			Detect d=new Detect(preList,nextList,curList,increList);
			String detectRes=d.detectSingleOD(nod);
			
			System.out.println(detectRes);
			
			if(detectRes.equals("valid")==false) {
				od.ods.remove(nod);
				incorrectODList.add(nod);
				//扩展od
				Extend et=new Extend(preList ,nextList,curList,increList);
				ArrayList<OrderDependency> newOdList=new ArrayList<OrderDependency>();
				newOdList=et.extend(nod,detectRes);
				
				if(!newOdList.isEmpty()) {
					
					System.out.print("modify od: ");
					nod.printOD();
				
					int count=0;
					for(OrderDependency no:newOdList) {
					
						System.out.print((count++)+". ");
						no.printOD();
						
						od.ods.add(no);
					}
				}
			}
			objectList.remove(objectList.size()-1);
		}
	}
	public static void enrichment() {
		if(incorrectODList.isEmpty()) return;
		for(OrderDependency iod:incorrectODList) {
			for(OrderDependency ood:originalODList) {
				if(iod.getLHS().size()<ood.getLHS().size()&&ood.isEqual(iod)==false&&ood.isContain(iod)!=-1) {
					enrichSingleOD(ood,iod,ood.isContain(iod));
				}
			}
		}
	}
	
	//od:需要被扩展的od，iod：错误的od，it：需要插入iod右边的起始index.最后都放到enrichODList中
	public static void enrichSingleOD(OrderDependency od,OrderDependency iod,int it) {
		OrderDependency tmp;
		if(it==od.getLHS().size()) return;//如果iod正好在od的尾巴上，没必要扩展
		while(it<od.getLHS().size()) {
			tmp=new OrderDependency(od);
			int iter=it;
			for(String r:iod.getRHS()) {
				tmp.addLHS(iter++,r);
			}
			enrichODList.add(tmp);
			
			it++;
		}
		
	}
	
	
	private static void listClear() {
		incorrectODList.clear();
		enrichODList.clear();
		originalODList.clear();
		//存储所有原有的od
		for(OrderDependency o:od.ods) {
			originalODList.add(o);
		}
	}
	private static void dataInitial() {
		try{
			od.storeOD(odFileName);
			cdo.readCSVData(dataFileName);
			ind.readCSVData(increFileName);
		}catch(Exception e) {
			System.out.println("read fail!");
		}

		listClear();
		objectList = cdo.datatoObject();
		iObjectList=ind.datatoObject();
		
		Index.buildTrees(originalODList);
		
		
	}
	
	public static void printList(ArrayList<OrderDependency> list,String sentence) {
		if(list.isEmpty()==false) System.out.println(sentence);
		for(OrderDependency od:list) {
			od.printOD();
		}
	}
	
}
