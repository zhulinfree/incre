package Test;

import Data.*;
import EquivalenceClass.*;
import OD.*;

import java.util.ArrayList;
import java.util.Map.Entry;

import BplusTree.InstanceKey;


public class ReadandCheck {
	public static boolean debug;
	public final static int order = 5;
	public static Index indexes=new Index();
	private static ArrayList<OrderDependency> odList;
	private static ArrayList<DataStruct> objectList,iObjectList;
	private static ArrayList<OrderDependency> originalODList=new ArrayList<OrderDependency>(),
			incorrectODList=new ArrayList<OrderDependency>(),
			enrichODList=new ArrayList<OrderDependency>();
	
	
public static void main(String[] args) {
		
		initial();
		listClear();
		
		/*将读入的od输出*/
		System.out.println("The original od is:");
		for(OrderDependency od:odList) od.printOD();
		System.out.println("共有"+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量数据");
	
		long start = System.currentTimeMillis( );
		 for(int i=0;i<iObjectList.size();i++) {
			 checkAllOD(originalODList,i);
			  
			 
		 }
		
		long end = System.currentTimeMillis( );
        long diff = end - start;
		System.out.println(objectList.size()-iObjectList.size()+"条数据"+iObjectList.size()+"条增量数据 共耗时"+diff+"毫秒");
		System.out.println("\n\n\nThe latest ods is:");
		if(!odList.isEmpty()) for(OrderDependency od:odList) od.printOD();
		
	}
	

	private static void checkAllOD(ArrayList<OrderDependency> checkedOD,int incre_tid) {
		//对每一条od进行验证
		for(OrderDependency nod:checkedOD) {
			
			
			ArrayList<Integer> preList,nextList,curList,increList=new ArrayList<Integer>();
			
			objectList.add(iObjectList.get(incre_tid));
			//最后一行是增量数据
			increList.add(objectList.size()-1);
			
			
			InstanceKey key=new InstanceKey(nod.getLHS(),iObjectList.get(incre_tid));
			
			int indid=getIndexId(key.getAttrName());
			System.out.println("index id is "+indid);
			if(indid==-1) return;
			
			preList=indexes.getPre(key,indid);
			curList=indexes.getCur(key,indid);
			nextList=indexes.getNext(key,indid);
			
			
			
//			System.out.println("pre is");
//			if(pre!=null)
//			for(int i:pre) {
//				objectList.get(i).printSingleData();
//			}
//			System.out.println("cur is");
//			if(cur!=null)
//			for(int i:cur) {
//				objectList.get(i).printSingleData();
//			}
//			System.out.println("next is");
//			if(next!=null)
//			for(int i:next) {
//				objectList.get(i).printSingleData();
//			}

					
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
				odList.remove(nod);
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
						
						odList.add(no);
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

	public static void initial() {
		debug=Debug.debug;
		DataInitial.readData();
		objectList=DataInitial.objectList;
		iObjectList=DataInitial.iObjectList;
		odList=DataInitial.odList;
		indexes.buildIndexes(odList);
	}
	
	
	private static void listClear() {
		incorrectODList.clear();
		enrichODList.clear();
		originalODList.clear();
		//存储所有原有的od
		for(OrderDependency o:odList) {
			originalODList.add(o);
		}
	}
	
	public static void printList(ArrayList<OrderDependency> list,String sentence) {
		if(list.isEmpty()==false) System.out.println(sentence);
		for(OrderDependency od:list) {
			od.printOD();
		}
	}
	
	
	public static int getIndexId(ArrayList<String> todo) {
		
		int x=indexes.indexMap.getOrDefault(todo,-1);
		if(x!=-1) return x;
		
		//没有正好的就用范围大的，如AB索引
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.addAll(todo);
		tmp.remove(tmp.size()-1);
		while(tmp.isEmpty()==false) {
			int r=indexes.indexMap.getOrDefault(tmp,-1);
			x=r==-1?x:r;
			tmp.remove(tmp.size()-1);
		}
		if(x!=-1) return x;
		
		//没有范围大的就用范围小的，在ABCD上查abc
		//return 0;
        
        for(Entry<ArrayList<String>, Integer> entry: indexes.indexMap.entrySet())
        {
         //System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
        	if(contain(entry.getKey(),todo)) return entry.getValue();
        }
		
        return -1;
	}
	
	//查看l1是否完全包括l2
	public static boolean contain(ArrayList<String> list1,ArrayList<String> list2) {
		if(list1.size()==0||list2.size()==0) return false;
		int count=0;
		for(String s2:list2) {
			if(s2.equals(list1.get(count))==false) return false;
			count++;
		}
		return true;
	}
	
}
