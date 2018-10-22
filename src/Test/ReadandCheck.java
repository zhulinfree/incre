//package Test;
//
//import Data.*;
//import EquivalenceClass.*;
//import OD.*;
//
//import java.util.ArrayList;
//
//
//public class ReadandCheck {
//	public static boolean debug;
//	public final static int order = 5;
//	public static Index indexes=new Index();
//	private static ODs od=new ODs();
//	private static ArrayList<DataStruct> objectList,iObjectList;
//	private static ArrayList<OrderDependency> originalODList=new ArrayList<OrderDependency>(),
//			incorrectODList=new ArrayList<OrderDependency>(),
//			enrichODList=new ArrayList<OrderDependency>();
//	
//	
//public static void main(String[] args) {
//		debug=Debug.debug;
//		
//		
//		DataInitial.readData();
//		
//		objectList=DataInitial.objectList;
//		iObjectList=DataInitial.iObjectList;
//		
//		listClear();
//		/*将读入的od输出*/
//		System.out.println("The original od is:");
//		od.print();
//		System.out.println("共有"+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量数据");
//	
//		long start = System.currentTimeMillis( );
//		 
//		
//		long end = System.currentTimeMillis( );
//        long diff = end - start;
//		System.out.println(objectList.size()-iObjectList.size()+"条数据"+iObjectList.size()+"条增量数据 共耗时"+diff+"毫秒");
//		System.out.println("\n\n\nThe latest ods is:");
//		od.print();
//		
//	}
//	
//
//	
//	
//	private static void listClear() {
//		incorrectODList.clear();
//		enrichODList.clear();
//		originalODList.clear();
//		//存储所有原有的od
//		for(OrderDependency o:od.ods) {
//			originalODList.add(o);
//		}
//	}
//	
//	public static void printList(ArrayList<OrderDependency> list,String sentence) {
//		if(list.isEmpty()==false) System.out.println(sentence);
//		for(OrderDependency od:list) {
//			od.printOD();
//		}
//	}
//	
//}
