package Data;
import java.util.ArrayList;
import java.util.HashMap;

public class DataStruct2{
	private ArrayList<String> attr=new ArrayList<String>();
	static private HashMap<Integer,String> colNumber_to_attrName=new HashMap<Integer,String>();
	static private HashMap<String,Integer> attrName_to_colNumber=new HashMap<String,Integer>();
	static int attrNumber=0;
	
	//将属性名和属性所在列数统一起来
	public static void buildAttrName(ArrayList<String> line) {
		attrNumber=line.size();
		for(int i=0;i<line.size();i++) {
			colNumber_to_attrName.put(i,line.get(i));
			attrName_to_colNumber.put(line.get(i), i);
		}
	}
		
	public static ArrayList<String> getAllAttributeName() {
		
		ArrayList<String> res=new ArrayList<String>();
		for(int i=0;i<attrNumber;i++) {
			res.add(colNumber_to_attrName.get(i));
		}
		return res;
	}
	
	public void add(String adder) {
		attr.add(adder);
	}
	public void add(DataStruct nds) {
		
	}
	public String getIndex(int i) {
		return attr.get(i);
	}
	
	public String getByName(String name) {
		return attr.get(attrName_to_colNumber.get(name));
	}
	
	public void printSingleData() {
		for(int i=0;i<attrNumber;i++) {
			System.out.printf("%-7s",attr.get(i)+" ");
		}
		System.out.println();
	}
	public static  int comparator(DataStruct d1,DataStruct d2,ArrayList<String> attrNameOfKey ) {
		for(int i =0;i<attrNameOfKey.size();i++) {
			String attrName = attrNameOfKey.get(i);
			int result = Cmp.compare(d1.getByName(attrName),d2.getByName(attrName));
			if(result!=0) {
				return result;
			}
		}
		return 0;
	}
	static public void printAttrName() {
		for(int i=0;i<attrNumber;i++) {
			System.out.printf("%-7s",colNumber_to_attrName.get(i)+" ");
		}
		System.out.println();
	}
	
}




/*package Data;
import java.util.ArrayList;
import java.util.HashMap;

public class DataStruct{
	private ArrayList<String> attr=new ArrayList<String>();
	static private HashMap<Integer,String> colNumber_to_attrName=new HashMap<Integer,String>();
	static private HashMap<String,Integer> attrName_to_colNumber=new HashMap<String,Integer>();
	static int attrNumber=0;
	
	//将属性名和属性所在列数统一起来
	public static void buildAttrName(ArrayList<String> line) {
		attrNumber=line.size();
		for(int i=0;i<line.size();i++) {
			colNumber_to_attrName.put(i,line.get(i));
			attrName_to_colNumber.put(line.get(i), i);
		}
	}
		
	public static ArrayList<String> getAllAttributeName() {
		
		ArrayList<String> res=new ArrayList<String>();
		for(int i=0;i<attrNumber;i++) {
			res.add(colNumber_to_attrName.get(i));
		}
		return res;
	}
	
	public void add(String adder) {
		attr.add(adder);
	}
	
	public String getIndex(int i) {
		return attr.get(i);
	}
	
	public String getByName(String name) {
		return attr.get(attrName_to_colNumber.get(name));
	}
	
	public void printSingleData() {
		for(int i=0;i<attrNumber;i++) {
			System.out.printf("%-7s",attr.get(i)+" ");
		}
		System.out.println();
	}
	static public void printAttrName() {
		for(int i=0;i<attrNumber;i++) {
			System.out.printf("%-7s",colNumber_to_attrName.get(i)+" ");
		}
		System.out.println();
	}
	
}
*/