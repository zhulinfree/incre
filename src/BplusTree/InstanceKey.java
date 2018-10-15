package BplusTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Data.Cmp;
import Data.DataStruct;

public class InstanceKey implements Comparable<InstanceKey>{
	public ArrayList<String> multiAtr = new ArrayList<String>();
	HashMap<String,Integer> attrName=new HashMap<String,Integer>();
	
	
	

	public InstanceKey(List<String> LHS,DataStruct d) {
		for(String temp:LHS) {
			multiAtr.add(d.getByName(temp));
		}
	}
	


	public InstanceKey() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public int compareTo(InstanceKey k2) {
		int size = this.multiAtr.size()<k2.multiAtr.size()?this.multiAtr.size():k2.multiAtr.size();
		for(int i =0;i<size;i++) {
			
			int result = Cmp.compare(this.multiAtr.get(i), k2.multiAtr.get(i));
			if(result!=0)
				return result;
			
		}
		return 0;
	}
}
	
