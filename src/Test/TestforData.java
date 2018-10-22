package Test;


import java.util.ArrayList;
import BplusTree.InstanceKey;
import Data.CSVtoDataObject;
import Data.DataStruct;
import EquivalenceClass.Index;
import OD.ODs;
import OD.OrderDependency;

public class TestforData {
	public static boolean debug=true;
	public final static int order = 5;
	public final static int tid =15;//513:0;1610:99
	private final static String dataFileName=new String("data2k.csv");
	private final static String increFileName=new String("incrementalData.csv");
	private final static String odFileName=new String("od2.txt");
	public static Index indexes=new Index();
	public static CSVtoDataObject cdo = new CSVtoDataObject();
	private static CSVtoDataObject ind=new CSVtoDataObject();
	private static ODs od=new ODs();
	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),iObjectList=new ArrayList<DataStruct>();;
	
	
	public static void main(String[] args) {
		dataInitial();
		System.out.println("共有 "+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量");
		
		DataStruct.printAttrName();
		for(DataStruct d:iObjectList) {
			d.printSingleData();
		}
		
		
		OrderDependency nod=od.ods.get(0);
		nod.printOD();
		
		//objectList.add(iObjectList.get(0));
		
		
		ArrayList<String> listforKey=new ArrayList<>();
	
		listforKey.add("A");
		listforKey.add("E");
		InstanceKey key=new InstanceKey(listforKey,objectList.get(tid));
		
		
		
		System.out.println("pre is");
		ArrayList<Integer> pre=indexes.getPre(key);
		if(pre!=null)
		for(int i:pre) {
			objectList.get(i).printSingleData();
		}
		
		System.out.println("cur is");
		ArrayList<Integer> cur=indexes.getCur(key);
		if(cur!=null)
		for(int i:cur) {
			objectList.get(i).printSingleData();
		}
		
		
		System.out.println("next is");
		ArrayList<Integer> next=indexes.getNext(key);
		if(next!=null)
		for(int i:next) {
			objectList.get(i).printSingleData();
		}
		
		System.out.println("test over");
	}
	
	public static void dataInitial() {
		try{
			od.storeOD(odFileName);
			cdo.readCSVData(dataFileName);
			ind.readCSVData(increFileName);
		}catch(Exception e) {
			System.out.println("read fail!");
		}
		objectList = cdo.datatoObject();
		
		iObjectList = ind.datatoObject();
		indexes.buildIndexes(od.ods);
	}
}
