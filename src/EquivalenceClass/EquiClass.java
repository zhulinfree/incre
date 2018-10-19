package EquivalenceClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import BplusTree.BplusTree;
import Data.DataStruct;
import OD.OrderDependency;



public class EquiClass<K extends Comparable<K>>{
	
	
	//保存当前等价类所需要的属性名字
	private ArrayList<String> attrName=new ArrayList<>();
	public BplusTree<K,ArrayList<Integer>> keyTree;
	//private ArrayList<K> keySortedList=new ArrayList<>();
	public HashMap<K,ArrayList<Integer>> ec=new HashMap<K,ArrayList<Integer>>();
	public EquiClass(ArrayList<String> indList,int treeOrder){
		this.setAttrName(indList);
		keyTree=new BplusTree<K,ArrayList<Integer>>(treeOrder);
		
	}
	
	//K key
	public void addTuple(K key,int tid) {
		
		//找到当前等价类
		ArrayList<Integer> findEC=ec.get(key);
		
		if(findEC==null) {
			//TODO::找不到等价类，更新树节点信息，
//			keySortedList.add(key);
//			Collections.sort(keySortedList);
			keyTree.insertOrUpdate(key, tid);
			ArrayList<Integer> in=new ArrayList<Integer>();
			in.add(tid);
			ec.put(key, in);
			return;
		}
		
		findEC.add(tid);
		
	}
	
	
	public ArrayList<Integer> getCur(K key){
		return ec.get(key);
	}
	
	
	public ArrayList<Integer> getPre(K key,int tid){
		K pre=getPreKey(key,tid);
		return pre==null?null:ec.get(pre);
	}
	
	public ArrayList<Integer> getNext(K key,int tid){
		K next=getNextKey(key,tid);
		return next==null?null:ec.get(next);
	}
	
	
	
	public void setAttrName(ArrayList<String> lhs) {
		for(String s:lhs) {
			attrName.add(s);
		}
	}
	
	public ArrayList<String> getAttrName() {
		return attrName;
	}
	

	public K getPreKey(K key,int tid) {
		return keyTree.getPre(key,tid);
	}
	public K getNextKey(K key,int tid) {
		return keyTree.getNext(key,tid);
	}
	
	/*
	//返回的是key中后一个数据的key值
	public K getPreKey(K key) {
		//TODO::return keyTree.getPre();
		int low=0,high=keySortedList.size()-1,mid;
		while(low<high) {
			mid=(low+high)/2;
			int cmp=key.compareTo(keySortedList.get(mid));
			if(cmp==0) {
				//放前一个key的值
				if(mid>0) return keySortedList.get(mid-1);
				else return null;
			}else if(cmp<0){
				high=mid-1;
			}else {
				low=mid+1;
			}
		}
		return null;
	}
	
	//返回的是key中后一个数据的key值
	public K getNextKey(K key) {
		//TODO::return keyTree.getNext();
		int low=0,high=keySortedList.size()-1,mid;
		while(low<high) {
			mid=(low+high)/2;
			int cmp=key.compareTo(keySortedList.get(mid));
			if(cmp==0) {
				//放后一个key的值
				if(mid+1<keySortedList.size()) return keySortedList.get(mid+1);
				else return null;
			}else if(cmp<0){
				high=mid-1;
			}else {
				low=mid+1;
			}
		}
		return null;
	}*/
	
	
}
