//package TestForBplusTree;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map.Entry;
//
//import org.apache.log4j.Logger;
//
//import BplusTree.BplusTree;
//import BplusTree.InstanceKey;
//import Data.CSVtoDataObject;
//import Data.DataStruct;
//import OD.Detect;
//import OD.Extend;
//import OD.ODs;
//import OD.OrderDependency;
//
///**
// * 用于测试如下函数
// * 
// * public Entry<K,V> getPrefixAtrributeTupleID_Pre(K key,int tid, BplusTree<K,V>
// * tree) public Entry<K,V> getPrefixAtrributeTupleID_Next(K key,int tid,
// * BplusTree<K,V> tree) public Entry<K,V> getPrefixAtrributeTupleID(K key)
// * 
// * public Entry<K,V> getMoreDetailedAtrributeTupleID_Next public Entry<K,V>
// * getMoreDetailedAtrributeTupleID_Pre public Entry<K,V>
// * getMoreDetailedAtrributeTupleID(K key,ArrayList<String> attrNameOfKey,
// * ArrayList<DataStruct> objectList)
// * 
// * @author Sun
// * @param <K>
// * @param <V>
// *
// */
//public class TestUsingBySunxu<K, V> {
//
//	public static ArrayList<DataStruct> objectList = null;
//	public static ArrayList<DataStruct> iObjectList = null;
//	public static BplusTree<InstanceKey, ArrayList<Integer>> tree = null;
//
//	public static void main(String[] args) {
//		long t1 = System.currentTimeMillis();
//		final int order = 50;
//		CSVtoDataObject cdo = new CSVtoDataObject();
//		CSVtoDataObject ind = new CSVtoDataObject();
//		try {
//			cdo.readCSVData("src/TestForBplusTree/flights_20_500k.csv");
////			ind.readCSVData("src/TestForBplusTree/incrementalData.csv");
//		} catch (Exception e) {
//			System.out.println("read fail!");
//		}
//		// 接受读入的D数据和增量数据,放入链表中
//		String A = "FlightNum";
//		String B = "OriginAirportID";
//		String C = "OriginCityMarketID";
//		objectList = cdo.datatoObject();
//		Logger logger = Logger.getLogger(TestUsingBySunxu.class);
////		//D盘输出日志文件,查看读入数据是否正确(正确,已验证)
////		for(int i = 0;i<objectList.size();i++) {
////			DataStruct dataKey = objectList.get(i);
////			logger.info((dataKey.getByName("id"))+"|"+"i:"+(i)+"|"+dataKey.getByName(A)+"|"+dataKey.getByName(B)+"|"+dataKey.getByName(C));
////		}
////		
//		long t2 = System.currentTimeMillis();
//		System.out.println("读取耗时："+ (t2-t1)+"ms");
//		// 建 B+树
//		tree = new BplusTree<InstanceKey, ArrayList<Integer>>(order);
//		// keyAttr为B+树的精细度,AB为year和month
//		ArrayList<String> keyAttr = new ArrayList<>();
//		keyAttr.add(A);
//		keyAttr.add(B);
//		for (int i = 0; i < objectList.size(); i++) {
//			DataStruct temp = objectList.get(i);
//			InstanceKey instanceKey = new InstanceKey(keyAttr, temp);
//			tree.insertOrUpdate(instanceKey, Integer.parseInt(temp.getByName("id")));
//		}
//		long t3 = System.currentTimeMillis();
//		System.out.println("建树耗时："+ (t3-t2)+"ms");
//		// 制造查询key的精细度
//		//20为excel中的位置
////		int loc = 83228-2;
//		int loc = 29999-2;
//		//打印要查找的key的信息
//		DataStruct dataKey = objectList.get(loc);
//		dataKey.printSingleData(A);
//		dataKey.printSingleData(B);
//		dataKey.printSingleData(C);
//		System.out.println();
//		
//		ArrayList<String> prefixAtrrName = new ArrayList<>();
//		prefixAtrrName.add(A);
//
//		ArrayList<String> appropiateAttrName = new ArrayList<>();
//		appropiateAttrName.add(A);
//		appropiateAttrName.add(B);
//
//		ArrayList<String> moreDetailedAttrName = new ArrayList<>();
//		moreDetailedAttrName.add(A);
//		moreDetailedAttrName.add(B);
//		moreDetailedAttrName.add(C);
//		//创造接收答案的容器
//		Entry<InstanceKey, ArrayList<Integer>> ansEntry = null;
//		
//		long t4 = System.currentTimeMillis();
//		InstanceKey key = new InstanceKey(appropiateAttrName, objectList.get(loc));
//		int id = Integer.parseInt(objectList.get(loc).getByName("id"));
//		
//		printTupleListIfo( "getKey",tree.getKey(key), A,B);
//		printTupleListIfo("getPre",tree.getPre(key,id), A,B);
//		printTupleListIfo("getNext",tree.getNext(key, id), A,B);
//
//		key = new InstanceKey(prefixAtrrName, objectList.get(loc));
//		id = Integer.parseInt(objectList.get(loc).getByName("id"));
//		
//		printTupleListIfo( "getPrefixAtrributeTupleID",tree.getPrefixAtrributeTupleID(key), A);
//		printTupleListIfo("getPrefixAtrributeTupleID_Pre",tree.getPrefixAtrributeTupleID_Pre(key,id,tree), A);
//		printTupleListIfo("getPrefixAtrributeTupleID_Next",tree.getPrefixAtrributeTupleID_Next(key,id,tree), A);
//
//
//		key = new InstanceKey(moreDetailedAttrName, objectList.get(loc));
//		id = Integer.parseInt(objectList.get(loc).getByName("id"));
//		printTupleListIfo( "getMoreDetailedAtrributeTupleID",tree.getMoreDetailedAtrributeTupleID(key, moreDetailedAttrName, objectList), A,B,C);
//		printTupleListIfo("getMoreDetailedAtrributeTupleID_Pre",tree.getMoreDetailedAtrributeTupleID_Pre(key, id, moreDetailedAttrName, objectList, tree), A,B,C);
//		printTupleListIfo("getMoreDetailedAtrributeTupleID_Next",tree.getMoreDetailedAtrributeTupleID_Next(key, id, moreDetailedAttrName, objectList, tree), A,B,C);
//		long t5 = System.currentTimeMillis();
//		System.out.println("查询耗时："+ (t5-t4)+"ms");
//	}
//
//	public static void printTupleListIfo(String mod, Entry<InstanceKey, ArrayList<Integer>> ansEntry,
//			String... strings) {
//		ArrayList<Integer> ansTupleList = null;
//		DataStruct ansData = null;
//		System.out.println(mod+"|");
//		if (ansEntry != null) {
//			ansTupleList = ansEntry.getValue();
//			for (int i = 0; i < ansTupleList.size(); i++) {
//				ansData = objectList.get(ansTupleList.get(i));
//				System.out.print("ID:" + ansData.getByName("id") + "|");
//				for (int j = 0; j < strings.length; j++) {
//					System.out.print(strings[j] + ":" + ansData.getByName(strings[j]) + "|");
//				}
//				System.out.println();
//			}
//		}
//		else {
//			System.out.println("null");
//		}
//	}
//
//}
