package Test;
import java.util.Map.Entry;

import BplusTree.InstanceKey;
import Data.*;
import EquivalenceClass.*;
import OD.*;

import java.util.ArrayList;
import java.util.HashMap;


public class ReadandCheck {
	public static boolean debug=false;
	public final static int order = 5;
	public static int tn=0;//tn表示当前建立索引树的数目
	private final static String dataFileName=new String("data100k.csv");
	private final static String increFileName=new String("batchIncrementalData.csv");
	private final static String odFileName=new String("od2.txt");
	public static HashMap<ArrayList<String>,Integer> indexMap=new HashMap<>(); 
	
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
	
		long start = System.currentTimeMillis( );
		 
		
		for(int i=0;i<iObjectList.size();i++) {
			//System.out.println("第"+i+"次插入..");
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
			objectList.add(iObjectList.get(i));
			Index.updateIndexes(objectList.get(objectList.size()-1));
			
			//System.out.println("第"+i+"次插入，od剩余"+od.ods.size()+"条");
			//od.print();
			
		}
		long end = System.currentTimeMillis( );
        long diff = end - start;
		System.out.println(objectList.size()-iObjectList.size()+"条数据"+iObjectList.size()+"条增量数据 共耗时"+diff+"毫秒");
		System.out.println("\n\n\nThe latest ods is:");
		od.print();
		
	}
	

	private static void checkAllOD(ArrayList<OrderDependency> checkedOD,int increTuple) {
		//对每一条od进行验证
		for(OrderDependency nod:checkedOD) {
			
			int indNumber=indexMap.getOrDefault(nod.getLHS(),-1);
			if(indNumber==-1) {
				Index.buildIndex(nod.getLHS(),order);
				indNumber=indexMap.getOrDefault(nod.getLHS(),-1);
			}
			if(debug) System.out.println("index number= "+indNumber);
			//将增量的数据放到原始数据集的最后一行
			objectList.add(iObjectList.get(increTuple));
			
			ArrayList<Integer> preList,nextList,curList,increList=new ArrayList<Integer>();
			
			//最后一行是增量数据
			increList.add(objectList.size()-1);
			
			InstanceKey key=new InstanceKey(nod.getLHS(),iObjectList.get(increTuple));
			curList=ECIndexList.get(indNumber).getCur(key);
			
			preList=ECIndexList.get(indNumber).getPre(key,objectList.size()-1);
			
			nextList=ECIndexList.get(indNumber).getNext(key,objectList.size()-1);
			
			if(debug) {
				System.out.println("\n\nThe current data is tuple: ");
				if(curList!=null)
					for(Integer i:curList) {
						objectList.get(i).printSingleData();
					}
				System.out.println("\n\nThe pre data is tuple: ");
				if(preList!=null)
					for(Integer i:preList) {
						objectList.get(i).printSingleData();
					}
				
				System.out.print("\n\nThe next data is tuple: ");
				if(nextList!=null)
					for(Integer i:nextList) {
						objectList.get(i).printSingleData();
					}
			}
			
			
		
			Detect d=new Detect(preList,nextList,curList,increList);
			String detectRes=d.detectSingleOD(nod);
			
			if(debug) System.out.println(detectRes);
			
			if(detectRes.equals("valid")==false) {
				od.ods.remove(nod);
				incorrectODList.add(nod);
				//扩展od
				Extend et=new Extend(preList ,nextList,curList,increList);
				ArrayList<OrderDependency> newOdList=new ArrayList<OrderDependency>();
				newOdList=et.extend(nod,detectRes);
				
				if(!newOdList.isEmpty()) {
					
					if(debug) System.out.print("modify od: ");
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
	public static void dataInitial() {
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
		
		Index.buildIndexes(originalODList,order);
		
		
	}
	
	public static void printList(ArrayList<OrderDependency> list,String sentence) {
		if(list.isEmpty()==false) System.out.println(sentence);
		for(OrderDependency od:list) {
			od.printOD();
		}
	}
	
}
