//package Test;
//
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import BplusTree.InstanceKey;
//import Data.DataInitial;
//import Data.DataStruct;
//import EquivalenceClass.Index;
//import OD.ODs;
//import OD.OrderDependency;
//
//public class TestforData {
//	public static boolean debug=true;
//	public final static int order = 5;
//	public final static int tid =25;//513:0;1610:99
//	public static Index indexes=new Index();
//	private static ODs od=new ODs();
//	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),iObjectList=new ArrayList<DataStruct>();;
//	
//	
//	public static void main(String[] args) {
//		
//		initial();
//		
//		System.out.println("共有 "+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量");
//		
//		DataStruct.printAttrName();
//		for(DataStruct d:iObjectList) {
//			d.printSingleData();
//		}
//		
//		
//		OrderDependency nod=od.ods.get(0);
//		nod.printOD();
//		
//		//objectList.add(iObjectList.get(0));
//		
//		
//		ArrayList<String> listforKey=new ArrayList<>();
//	
//		listforKey.add("A");
//		listforKey.add("E");
//		listforKey.add("F");
//		InstanceKey key=new InstanceKey(listforKey,objectList.get(tid));
//		
//		int indid=getIndexId(key.getAttrName());
//		System.out.println("index id is "+indid);
//		if(indid==-1) return;
//		
//		System.out.println("pre is");
//		ArrayList<Integer> pre=indexes.getPre(key,indid);
//		if(pre!=null)
//		for(int i:pre) {
//			objectList.get(i).printSingleData();
//		}
//		
//		System.out.println("cur is");
//		ArrayList<Integer> cur=indexes.getCur(key,indid);
//		if(cur!=null)
//		for(int i:cur) {
//			objectList.get(i).printSingleData();
//		}
//		
//		
//		System.out.println("next is");
//		ArrayList<Integer> next=indexes.getNext(key,indid);
//		if(next!=null)
//		for(int i:next) {
//			objectList.get(i).printSingleData();
//		}
//		
//		System.out.println("test over");
//	}
//	
//	
//	
//	public static void initial() {
//		debug=Debug.debug;
//		DataInitial.readData();
//		objectList=DataInitial.objectList;
//		iObjectList=DataInitial.iObjectList;
//		od=DataInitial.od;
//		indexes.buildIndexes(od.ods);
//	}
//	
//	
//	
//	
//	
//	
//	
//	//在ABC上查，有正好的就用正好的，没正好的就用范围大的（AB）,再没有就用ABCD
//	public static int getIndexId(ArrayList<String> todo) {
//		
//		int x=indexes.indexMap.getOrDefault(todo,-1);
//		if(x!=-1) return x;
//		
//		//没有正好的就用范围大的，如AB索引
//		ArrayList<String> tmp=new ArrayList<String>();
//		tmp.addAll(todo);
//		tmp.remove(tmp.size()-1);
//		while(tmp.isEmpty()==false) {
//			int r=indexes.indexMap.getOrDefault(tmp,-1);
//			x=r==-1?x:r;
//			tmp.remove(tmp.size()-1);
//		}
//		if(x!=-1) return x;
//		
//		//没有范围大的就用范围小的，在ABCD上查abc
//		//return 0;
//        System.out.println("\n通过Map.entrySet遍历key和value");  
//        for(Entry<ArrayList<String>, Integer> entry: indexes.indexMap.entrySet())
//        {
//         //System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
//        	if(contain(entry.getKey(),todo)) return entry.getValue();
//        }
//		
//        return -1;
//		
//		
//		
//	}
//	
//	//查看l1是否完全包括l2
//	public static boolean contain(ArrayList<String> list1,ArrayList<String> list2) {
//		if(list1.size()==0||list2.size()==0) return false;
//		int count=0;
//		for(String s2:list2) {
//			if(s2.equals(list1.get(count))==false) return false;
//			count++;
//		}
//		
//		return true;
//	}
//	
//	
//	
//		
//}
