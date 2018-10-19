//package Test;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import BplusTree.InstanceKey;
//import Data.CSVtoDataObject;
//import Data.DataStruct;
//import EquivalenceClass.EquiClass;
//import OD.ODs;
//import OD.OrderDependency;
//
//public class TestforData {
//	public static boolean debug=false;
//	public final static int order = 5;
//	public final static int tid =0;//513:0;1610:99
//	private final static String dataFileName=new String("data2k.csv");
//	private final static String increFileName=new String("incrementalData.csv");
//	private final static String odFileName=new String("od2.txt");
//	public static HashMap<ArrayList<String>,Integer> treeMap=new HashMap<>(); 
//	 
//	public static CSVtoDataObject cdo = new CSVtoDataObject();
//	private static CSVtoDataObject ind=new CSVtoDataObject();
//	private static ODs od=new ODs();
//	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),iObjectList=new ArrayList<DataStruct>();;
//	
//	
//	public static void main(String[] args) {
//		dataInitial();
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
//		//建立索引
//		System.out.println("building..");
//		EquiClass<InstanceKey> index=new EquiClass<InstanceKey>(nod,order);
//		for (int i=0;i<objectList.size();i++) {
//			DataStruct temp=objectList.get(i);
//			index.addTuple(new InstanceKey(nod.getLHS(),temp),i);
//		}
//		System.out.println("build over");
//		System.out.println(index.ec.size());
//		
//		//将增量放到objectList中
//		objectList.add(iObjectList.get(0));
//		
//		InstanceKey key=new InstanceKey(nod.getLHS(),objectList.get(objectList.size()-1));
//		
//		System.out.println("cur is");
//		ArrayList<Integer> cur=index.getKey(key);
//		if(cur!=null)
//		for(int i:cur) {
//			objectList.get(i).printSingleData();
//		}
//		
//		System.out.println("pre is");
//		ArrayList<Integer> pre=index.getPre(key,tid);
//		if(pre!=null)
//		for(int i:pre) {
//			objectList.get(i).printSingleData();
//		}
//		System.out.println("next is");
//		
//		ArrayList<Integer> next=index.getNext(key,tid);
//		if(next!=null)
//		for(int i:next) {
//			objectList.get(i).printSingleData();
//		}
//		
//		System.out.println("test over");
//	}
//	
//	public static void dataInitial() {
//	try{
//		od.storeOD(odFileName);
//		cdo.readCSVData(dataFileName);
//		ind.readCSVData(increFileName);
//	}catch(Exception e) {
//		System.out.println("read fail!");
//	}
//	objectList = cdo.datatoObject();
//	
//	iObjectList = ind.datatoObject();
//}
//}
