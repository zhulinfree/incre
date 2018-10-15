package Data;
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
		//-1是因为 id本身不是数据中的属性
		for(int i=0;i<attrNumber-1;i++) {
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
	public void setId(String id) {
		attr.remove(attrNumber-1);
		attr.add(id);
	}
	public void printSingleData() {
		for(int i=0;i<attrNumber;i++) {
			System.out.printf("%-7s",attr.get(i)+" ");
		}
		System.out.println();
	}
	
	public void printSingleData(String name) {
		
		System.out.print(name+": "+attr.get(attrName_to_colNumber.get(name))+"|");
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
