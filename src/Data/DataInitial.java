package Data;

import java.util.ArrayList;

import OD.ODs;

public class DataInitial {
	
	private final static String dataFileName=new String("data2k.csv");
	private final static String increFileName=new String("incrementalData.csv");
	private final static String odFileName=new String("od2.txt");
	public static CSVtoDataObject cdo = new CSVtoDataObject();
	private static CSVtoDataObject ind=new CSVtoDataObject();
	public static ODs od=new ODs();
	public static ArrayList<DataStruct> objectList=new ArrayList<DataStruct>(),
			iObjectList=new ArrayList<DataStruct>();
	
	public static void readData() {
		try{
			od.storeOD(odFileName);
			cdo.readCSVData(dataFileName);
			ind.readCSVData(increFileName);
		}catch(Exception e) {
			System.out.println("read fail!");
		}
		objectList = cdo.datatoObject();
		iObjectList=ind.datatoObject();
	}
	
}
