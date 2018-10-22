package EquivalenceClass;

import java.util.ArrayList;
import java.util.HashMap;

import BplusTree.InstanceKey;
import Data.*;
import OD.OrderDependency;
import Test.*;

public class Index {
	
	public ArrayList<EquiClass<InstanceKey>> ECIndexList=new ArrayList<>();
//	public ArrayList<DataStruct> objList=new ArrayList<>();
	public HashMap<ArrayList<String>,Integer> indexMap=new HashMap<>(); 
	public HashMap<Integer,ArrayList<String>> recforIndex=new HashMap<>();
	public int tn=0;//tn表示当前建立索引树的数目
	public int order;
	public boolean debug;
	public Index() {
//		debug=TestforData.debug;
//		order=TestforData.order;
		debug=Debug.debug;
		order=Debug.order;
	}
	
	
	public EquiClass<InstanceKey> buildIndex(ArrayList<String> indexList) {	
		if(debug) {
			System.out.print("building Index in ");
			for(String s:indexList) System.out.print(s+" ");
			System.out.println();
		}
		ArrayList<DataStruct> objList=DataInitial.objectList;
		EquiClass<InstanceKey> index=new EquiClass<InstanceKey>(indexList,order);
		for (int i=0;i< objList.size();i++) {
			DataStruct temp= objList.get(i);
			index.addTuple(new InstanceKey(indexList,temp),i);
		}
		
		indexMap.put(indexList,tn);
		recforIndex.put(tn++,indexList);
		return index;
	}
	public void buildIndexes(ArrayList<OrderDependency> ods) {
		for(OrderDependency nod:ods) {
			ECIndexList.add(buildIndex(nod.getLHS()));
		}
		//return ECIndexList;
	}
	
	//在ABC上查，有正好的就用正好的，没正好的就用范围大的（AB）,再没有就用ABCD
	public int getIndexId(ArrayList<String> todo) {
		
		int x=indexMap.getOrDefault(todo,-1);
		if(x!=-1) return x;
		
		//没有正好的就用范围大的，如AB索引
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.addAll(todo);
		tmp.remove(tmp.size()-1);
		while(tmp.isEmpty()==false) {
			int r=indexMap.getOrDefault(tmp,-1);
			x=r==-1?x:r;
			tmp.remove(tmp.size()-1);
		}
		if(x!=-1) return x;
		
		//没有范围大的就用范围小，如ABCD
		return 0;
		
		
		
		
	}
	
	
	
	
	public ArrayList<Integer> getCur(InstanceKey key){
		
		int indexId=getIndexId(key.getAttrName());
		//TODO::if(indexId==-1) how to do重新建立索引
		ArrayList<String> indexAttrName=new ArrayList<>();
		indexAttrName=recforIndex.get(indexId);
		int comp=key.getAttrName().size()-indexAttrName.size();

		//查询的属性和索引属性相等
		if(comp==0) {
			return getCur(key,indexId);
		}
		
		if(comp>0) {
			//索引比较短
			return getCur_narrow(key,indexAttrName,indexId);
		}
		
		return	getCur_extension(key,indexAttrName,indexId);
		//TODO::索引比较长
		//return null;
		
	}
	
	public ArrayList<Integer> getPre(InstanceKey key){
		
		int indexId=getIndexId(key.getAttrName());
		ArrayList<String> indexAttrName=recforIndex.get(indexId);
		int comp=key.getAttrName().size()-indexAttrName.size();

		//查询的属性和索引属性相等
		if(comp==0) {
			return getPre(key,indexId);
		}
		
		if(comp>0) {
			//索引比较短
			return getPre_narrow(key,indexAttrName,indexId);
		}
		
		return	getPre_extension(key,indexAttrName,indexId);
		//TODO::索引比较长
		//return null;
		
	}

	public ArrayList<Integer> getNext(InstanceKey key){
		
		int indexId=getIndexId(key.getAttrName());
		ArrayList<String> indexAttrName=recforIndex.get(indexId);
		int comp=key.getAttrName().size()-indexAttrName.size();
	
		//查询的属性和索引属性相等
		if(comp==0) {
			return getNext(key,indexId);
		}
		
		if(comp>0) {
			//索引比较短
			return getNext_narrow(key,indexAttrName,indexId);
		}
		
		
		//TODO::索引比较长
		return	getNext_extension(key,indexAttrName,indexId);
		
	}

	//getCur 
	private ArrayList<Integer> getCur(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getCur(key);
	}
	
	private ArrayList<Integer> getPre(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getPre(key);
	}
	
	private ArrayList<Integer> getNext(InstanceKey key,int indexId){
		return ECIndexList.get(indexId).getNext(key);
	}
	

	//现在有索引AB，查ABCD,key里面有ABCD的值和属性名字
	private ArrayList<Integer> getCur_narrow(InstanceKey key,ArrayList<String> indexAttrName,int indexId){
		//if(debug) System.out.println("narrowforCur");
		InstanceKey queryKey=new InstanceKey(indexAttrName,key.getFullData());
		ArrayList<Integer> curList=getCur(queryKey,indexId);
		if(curList==null) return null;
		
		ArrayList<Integer> preList=getPre(queryKey,indexId);
		ArrayList<Integer> nextList=getNext(queryKey,indexId);
		ArrayList<Integer> union=new ArrayList<Integer>();
		if(curList!=null) union.addAll(curList);
		if(preList!=null) union.addAll(preList);
		if(nextList!=null) union.addAll(nextList);
		
		EquiClass<InstanceKey> newEC=buildEC(key,union);
		return newEC.getCur(key);
	}
	private ArrayList<Integer> getPre_narrow(InstanceKey key,ArrayList<String> indexAttrName,int indexId){
		InstanceKey queryKey=new InstanceKey(indexAttrName,key.getFullData());
		ArrayList<Integer> curList=getCur(queryKey,indexId);
		
		
		ArrayList<Integer> preList=getPre(queryKey,indexId);
		ArrayList<Integer> nextList=getNext(queryKey,indexId);
		
		ArrayList<Integer> union=new ArrayList<Integer>();
		if(curList!=null) union.addAll(curList);
		if(preList!=null) union.addAll(preList);
		if(nextList!=null) union.addAll(nextList);
		
		EquiClass<InstanceKey> newEC=buildEC(key,union);
		return newEC.getPre(key);
		
	}
	private ArrayList<Integer> getNext_narrow(InstanceKey key,ArrayList<String> indexAttrName,int indexId){
		InstanceKey queryKey=new InstanceKey(indexAttrName,key.getFullData());
		ArrayList<Integer> curList=getCur(queryKey,indexId);
		
		ArrayList<Integer> preList=getPre(queryKey,indexId);
		ArrayList<Integer> nextList=getNext(queryKey,indexId);
		
		ArrayList<Integer> union=new ArrayList<Integer>();
		if(curList!=null) union.addAll(curList);
		if(preList!=null) union.addAll(preList);
		if(nextList!=null) union.addAll(nextList);
		
		EquiClass<InstanceKey> newEC=buildEC(key,union);
		return newEC.getNext(key);
		
	}
	
	//现在有索引ABCD，需要在AB上查
	private ArrayList<Integer> getCur_extension(InstanceKey key,ArrayList<String> indexAttrName,int indexId){
		DataStruct data=key.getFullData();
		ArrayList<String> cmp_attr=key.getAttrName();
		
		InstanceKey queryKey=new InstanceKey(indexAttrName,data);
		ArrayList<Integer> curUnionSet=new ArrayList<Integer>();
		ArrayList<Integer> curList=getCur(queryKey,indexId);
		
		if(curList!=null) curUnionSet.addAll(curList);
		
		
		//拿到ABCD中的前一个值，如果前一个值不为空且在AB上和cur在AB上的值相等，那么就继续
		ArrayList<Integer> pre=getPre(queryKey,indexId);
	
		ArrayList<DataStruct> objList=DataInitial.objectList;
		while(pre!=null&&compareTwoData(objList.get(pre.get(0)),data,cmp_attr)==0) {
			curUnionSet.addAll(pre);
			InstanceKey newQueryKey=new InstanceKey(indexAttrName,objList.get(pre.get(0)));
			pre=getPre(newQueryKey,indexId);
		}
		
		ArrayList<Integer> next=getNext(queryKey,indexId);
		
		while(next!=null&&compareTwoData(objList.get(next.get(0)),data,cmp_attr)==0) {
			curUnionSet.addAll(next);
			InstanceKey newQueryKey=new InstanceKey(indexAttrName,objList.get(next.get(0)));
			next=getNext(newQueryKey,indexId);
		}
		
		return curUnionSet;
	}
	
	private ArrayList<Integer> getPre_extension(InstanceKey key,ArrayList<String> indexAttrName,int indexId){
		DataStruct data=key.getFullData();
		ArrayList<String> cmp_attr=key.getAttrName();
		
		
		InstanceKey queryKey=new InstanceKey(indexAttrName,data);
		
		//拿到ABCD中的前一个值，如果前一个值不为空且在AB上和cur在AB上的值相等，那么就继续
		ArrayList<Integer> pre=getPre(queryKey,indexId);
	
		ArrayList<DataStruct> objList=DataInitial.objectList;
		
		while(pre!=null&&compareTwoData(objList.get(pre.get(0)),data,cmp_attr)==0) {
			InstanceKey newQueryKey=new InstanceKey(indexAttrName,objList.get(pre.get(0)));
			pre=getPre(newQueryKey,indexId);
		}
		
		
		//拿到AB的前一个节点了，比如是查（5,7）的前节点，现在能得到(5,6,9,9)等
		ArrayList<Integer> preUnionSet=new ArrayList<Integer>();
		
		DataStruct newData=new DataStruct();
		if(pre!=null) newData.copy(objList.get(pre.get(0)));
		
		while(pre!=null&&compareTwoData(objList.get(pre.get(0)),newData,cmp_attr)==0) {
			preUnionSet.addAll(pre);
			InstanceKey qk=new InstanceKey(indexAttrName,objList.get(pre.get(0)));
			pre=getPre(qk,indexId);
		}
		
		return preUnionSet;
	}
	
	private ArrayList<Integer> getNext_extension(InstanceKey key,ArrayList<String> indexAttrName,int indexId){
		DataStruct data=key.getFullData();
		ArrayList<String> cmp_attr=key.getAttrName();
		
		
		InstanceKey queryKey=new InstanceKey(indexAttrName,data);
		
		//拿到ABCD中的前一个值，如果前一个值不为空且在AB上和cur在AB上的值相等，那么就继续
		ArrayList<Integer> next=getNext(queryKey,indexId);
	
		ArrayList<DataStruct> objList=DataInitial.objectList;
		
		while(next!=null&&compareTwoData(objList.get(next.get(0)),data,cmp_attr)==0) {
			InstanceKey newQueryKey=new InstanceKey(indexAttrName,objList.get(next.get(0)));
			next=getNext(newQueryKey,indexId);
		}
		
		
		//拿到AB的前一个节点了，比如是查（5,7）的前节点，现在能得到(5,6,9,9)等
		ArrayList<Integer> nextUnionSet=new ArrayList<Integer>();
		
		DataStruct newData=new DataStruct();
		if(next!=null) newData.copy(objList.get(next.get(0)));
		
		while(next!=null&&compareTwoData(objList.get(next.get(0)),newData,cmp_attr)==0) {
			nextUnionSet.addAll(next);
			InstanceKey qk=new InstanceKey(indexAttrName,objList.get(next.get(0)));
			next=getNext(qk,indexId);
		}
		
		return nextUnionSet;
		
	}
	
	
	private  int compareTwoData(DataStruct d1,DataStruct d2,ArrayList<String> cmp_attr) {
		for(String f:cmp_attr) {
			int r=Cmp.compare(d1.getByName(f),d2.getByName(f));
			if(r!=0) return r;
		}
		return 0;
	}
	
	
	private EquiClass<InstanceKey> buildEC(InstanceKey key,ArrayList<Integer> ecList) {
		ArrayList<DataStruct> objList=DataInitial.objectList;
		//建立新的等价类
		EquiClass<InstanceKey> newEC=new EquiClass<>(key.getAttrName(),order);
		for(int i:ecList) {	
			DataStruct temp= objList.get(i);
			newEC.addTuple(new InstanceKey(key.getAttrName(),temp),i);
		}
		return newEC;
	}
	
//	private InstanceKey setKey(InstanceKey key,ArrayList<String> indexAttrName) {
//		ArrayList<Integer> tailData=new ArrayList<>();
//		ArrayList<String> tailAttrName=new ArrayList<>();
//		for(int i=indexAttrName.size();i<key.getAttrName().size();i++) {
//			tailAttrName.add(key.getAttrName().get(i));
//			tailData.add(key.getKeyData().get(i));
//		}
//		
//		return new InstanceKey(tailAttrName,tailData);
//	}
//	
//	
	
	
	
//	//增量数据插入，更新tree的信息
//	public void updateIndexes(DataStruct data) {
//		for(int i=0;i<TestforData.tn;i++) {
//			EquiClass<InstanceKey> tmp_ind=TestforData.ECIndexList.get(i);
//			tmp_ind.addTuple(new InstanceKey(tmp_ind.getAttrName(),data), objList.size()-1);
//		}
//	}
	
	
}
