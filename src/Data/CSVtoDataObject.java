package Data;

import java.util.ArrayList;
import java.util.Iterator;


public class CSVtoDataObject {
	public String tempString = new String();
	public ArrayList<String> tempList = new ArrayList<>();
	public ArrayList<ArrayList> tempListTwoDem = new ArrayList<>() ;
	public  int listCount = 0;
	
	public  void readCSVData(String fileName) throws Exception {

		CSVFileUtil cfu = new CSVFileUtil(fileName);
		while((tempString=cfu.readLine())!=null) {
			tempList = cfu.fromCSVLinetoArray(tempString);
			tempList.add("id");
			tempListTwoDem.add(tempList);
			listCount++;
		}
		
	}
	public  ArrayList<DataStruct> datatoObject() {
		ArrayList<DataStruct> objectList = new ArrayList<>();
//		Iterator iter = tempListTwoDem.iterator();
		ArrayList<String> nameContrroller = (ArrayList<String>) tempListTwoDem.get(0);
		
		ArrayList<String> dataList =new ArrayList<String>();
		if(tempListTwoDem.size()<2) {
			System.out.println("tuple is less than 1");
			return null;
		}
		for (int j=1;j<tempListTwoDem.size();j++) {
//			DataStruct.buildAttrName(nameContrroller);
			
//			for(int tempCount = 2;tempCount<listCount;tempCount++) {
//			for(;iter.hasNext();) {
				dataList = (ArrayList<String>)tempListTwoDem.get(j);
				
//				String[] array = (String[])dataList.toArray(new String[nameContrroller.size()]); 
				String[] array = (String[])dataList.toArray(new String[39]); 
				
				DataStruct tempObject = new DataStruct();
				tempObject.buildAttrName(nameContrroller);
				//id最后放入所以是number-1；
				for(int i=0;i<tempObject.attrNumber-1;i++) {
					tempObject.add(array[i]);
				}
				String str = Integer.toString(j-1);
				tempObject.add(str);	
				objectList.add(tempObject);
//			}
		}
		
		return objectList;
	}
	
	
	public void printData(ArrayList<DataStruct> objectList) {
		for(int i=0;i<DataStruct.attrNumber;i++) {
			System.out.print(objectList.get(i)+"  ");
		}
		System.out.println();
		
	}
	
	
	
	
}
