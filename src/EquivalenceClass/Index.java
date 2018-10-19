package EquivalenceClass;

import java.util.ArrayList;

import BplusTree.InstanceKey;
import Data.DataStruct;
import OD.OrderDependency;
import Test.ReadandCheck;

public class Index<K extends Comparable<K> > {
	

	
//	//现在有索引AB，查ABCD,key指的是对于当前等价类的key，并不是在ABCD上的Key
//	public static ArrayList<Integer> getCur_narrow(K key,ArrayList<String> attrName,ArrayList<Integer> adder){
//		ArrayList<Integer> cur=ec.get(key);
//		if(cur==null) return null;
//		
//		
//	}
	
	public static void buildIndex(ArrayList<String> indexList,int order) {	
		if(ReadandCheck.debug) {
			System.out.print("building Index in ");
			for(String s:indexList) System.out.print(s+" ");
			System.out.println();
		}
		ArrayList<DataStruct> objList=ReadandCheck.objectList;
		EquiClass<InstanceKey> index=new EquiClass<InstanceKey>(indexList,order);
		for (int i=0;i< objList.size();i++) {
			DataStruct temp= objList.get(i);
			index.addTuple(new InstanceKey(indexList,temp),i);
		}
		
		ReadandCheck.indexMap.put(indexList,ReadandCheck.tn++);
		ReadandCheck.ECIndexList.add(index);	
	}
	public static void buildIndexes(ArrayList<OrderDependency> ods,int order) {
		for(OrderDependency nod:ods) {
			buildIndex(nod.getLHS(),order);
		}
	}
	//增量数据插入，更新tree的信息
	public static void updateIndexes(DataStruct data) {
		for(int i=0;i<ReadandCheck.tn;i++) {
			EquiClass<InstanceKey> tmp_ind=ReadandCheck.ECIndexList.get(i);
			tmp_ind.addTuple(new InstanceKey(tmp_ind.getAttrName(),data), ReadandCheck.objectList.size()-1);
		}
	}
	
	
}
