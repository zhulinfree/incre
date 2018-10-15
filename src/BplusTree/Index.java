package BplusTree;

import java.util.ArrayList;

import Data.DataStruct;
import OD.OrderDependency;
import Test.ReadandCheck;

public class Index {
	public static void buildTree(ArrayList<String> indexList) {
		
		
		if(ReadandCheck.debug) {
			System.out.print("building trees in ");
			for(String s:indexList) System.out.print(s+" ");
			System.out.println();
		}
		
		//build B+ tree
		BplusTree<InstanceKey, ArrayList<Integer>> tree= new BplusTree<InstanceKey, ArrayList<Integer>>(ReadandCheck.order);		
		tree.attrName=indexList;
		
		for (int i=0;i<ReadandCheck.objectList.size();i++) {
			DataStruct temp=ReadandCheck.objectList.get(i);
			tree.insertOrUpdate(new InstanceKey(indexList,temp),i);
		}
		ReadandCheck.treeMap.put(indexList,ReadandCheck.tn++);
		ReadandCheck.bptree.add(tree);	
	}
	public static void buildTrees(ArrayList<OrderDependency> ods) {
		for(OrderDependency nod:ods) {
			buildTree(nod.getLHS());
		}
	}
	//增量数据插入，更新tree的信息
	public static void updateTrees(DataStruct data) {
		data.setId(Integer.toString(ReadandCheck.objectList.size()-1));
		for(int i=0;i<ReadandCheck.tn;i++) {
			BplusTree<InstanceKey, ArrayList<Integer>> tempTree=ReadandCheck.bptree.get(i);
			tempTree.insertOrUpdate(new InstanceKey(tempTree.attrName,data),ReadandCheck.objectList.size()-1);
		}
	}
}
