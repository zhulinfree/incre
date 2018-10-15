package OD;

import java.util.ArrayList;
import java.util.HashMap;

import Data.Cmp;
import Data.DataStruct;
import Test.ReadandCheck;

public class Extend {
	ArrayList<Integer> preList = new ArrayList<Integer>(), nextList = new ArrayList<Integer>(),
			curList = new ArrayList<Integer>(), increList = new ArrayList<Integer>();
	//ArrayList<DataStruct> objectList = new ArrayList<DataStruct>();

	public Extend(ArrayList<Integer> pre, ArrayList<Integer> next,
			ArrayList<Integer> cur, ArrayList<Integer> incre) {
		preList = pre;
		nextList = next;
		curList = cur;
		increList = incre;
	}

	// 最终返回的是所有的符合条件的OD的一个list
	public ArrayList<OrderDependency> extend(OrderDependency od, String violationType) {
		// TODO::扩展当前od，左边加属性右边减属性；扩展成功的话可以加到原来的属ods里面

		ArrayList<OrderDependency> res = new ArrayList<OrderDependency>();

		// swap,try to reduce the attributes from right side
		if (violationType.equals("swap")) {

			return reduceRHSforSwap(od);

		} else {// split,add at left,reduce at right

			// try to add attr from left side
			ArrayList<OrderDependency> addLHS = increaseLHS(od, this.curList);
			for (OrderDependency tod : addLHS) {
				res.add(tod);
			}

			// try to reduce attr from right side
			ArrayList<OrderDependency> reRHS = reduceRHSforSplit(od);
			for (OrderDependency tod : reRHS) {
				res.add(tod);
			}
		}
		return res;
	}

	
	public ArrayList<OrderDependency> reduceRHSforSwap(OrderDependency od){
		if(ReadandCheck.debug)  System.out.println("尝试减少属性..");
		ArrayList<OrderDependency> res=new ArrayList<OrderDependency>();
		Detect d=new Detect(preList,nextList,curList,increList);
		String violationType="swap";
		while(violationType.equals("swap")&&od.getRHS().size()>1) {
			//od.getRHS().remove(od.getRHS().size()-1);
			od.deleteRHSTail();
			violationType=d.detectSingleOD(od);
			if(ReadandCheck.debug) System.out.println(violationType);
		}
		
		if(violationType.equals("valid")) {
			if(ReadandCheck.debug) {
				System.out.println("减少属性成功");
				od.printOD();
			}
			
			res.add(od);
		}else if(violationType.equals("split")) {
			return extend(od,"split");
		}
		return res;
	}
	// reduce right side attr
	public ArrayList<OrderDependency> reduceRHSforSplit(OrderDependency od) {
		ArrayList<OrderDependency> res = new ArrayList<OrderDependency>();

		// 尝试右边减属性，应该剪到cur和incre的相同的前半部分
		if(ReadandCheck.debug)  System.out.println("尝试减少属性..");
		if(od.getRHS().size()<2) return res;
		
		int prefixNum = 0;// 记录cur和增量数据在getRHS()中匹配的数据数量
		OrderDependency odReduce = new OrderDependency();
		odReduce.copy(od);
		for (String it : odReduce.getRHS()) {
			String cv = ReadandCheck.objectList.get(curList.get(0)).getByName(it);
			String iv = ReadandCheck.objectList.get(increList.get(0)).getByName(it);
			if (Cmp.equals(cv, iv))
				prefixNum++;
		}

		// 如果匹配的数目不为零，那么说明减属性有戏,将prefixNum后面的都删掉
		if (prefixNum != 0) {
			odReduce.getRHS().clear();
			for (int i = 0; i < prefixNum; i++)
				odReduce.getRHS().add(od.getRHS().get(i));
			res.add(odReduce);
		}
		return res;
	}

	// 左边加属性
	// TODO::左边加属性的深搜
	public ArrayList<OrderDependency> increaseLHS(OrderDependency od, ArrayList<Integer> curList) {
		if(ReadandCheck.debug) {
			System.out.print("increase od:");
			od.printOD();
		}
		
		ArrayList<OrderDependency> res = new ArrayList<OrderDependency>();

		// get the name of all attributes
		ArrayList<String> attrName = DataStruct.getAllAttributeName();
		HashMap<String, Integer> m = new HashMap<String, Integer>();
		
		// 使用哈希map来记录已经被使用过的属性
		for (String it : od.getLHS()) {
			m.put(it, 1);
		}
		for (String it : od.getRHS()) {
			m.put(it,1);
		}

		for (String adder : attrName) {

			OrderDependency odIncre = new OrderDependency(od);

			// 如果这个属性没有被使用
			if (m.get(adder) == null) {
				m.put(adder, 1);
				if(ReadandCheck.debug) System.out.println("尝试添加属性:  " + adder);
				int bigger = biggerThan(ReadandCheck.objectList.get(curList.get(0)), ReadandCheck.objectList.get(increList.get(0)), od);
				
				if (ReadandCheck.debug) {
					for (int i : curList) {
						System.out.print(i + " ");
					}
					System.out.println("/" + increList.get(0));
				}
					
				
				boolean flag = true;
				ArrayList<Integer> splitList = new ArrayList<Integer>();
				for (int li : curList) {
					int check = bigger * Cmp.compare(ReadandCheck.objectList.get(li).getByName(adder),
							ReadandCheck.objectList.get(increList.get(0)).getByName(adder));
					if (check < 0) {
						flag = false;
						break;
					} else if (check == 0) {
						if(ReadandCheck.debug) System.out.println("split id is: "+li);
						splitList.add(li);
						flag = false;
					}
				}
				if (flag) {
					if (ReadandCheck.debug) System.out.println("添加成功: "+adder);
					odIncre.getLHS().add(adder);
					res.add(new OrderDependency(odIncre));
					odIncre.copy(od);
				} else if (!splitList.isEmpty()&&splitList.size()<curList.size()) {
					if (ReadandCheck.debug) System.out.println("递归查找...");
					odIncre.getLHS().add(adder);
					ArrayList<OrderDependency> newOD = new ArrayList<OrderDependency>();
					newOD = increaseLHS(odIncre, splitList);
					for (OrderDependency tod : newOD)
						res.add(new OrderDependency(tod));
					odIncre.copy(od);
				}
			}
		}

		return res;
	}

	//若是cur>incre,返回ture
	public int biggerThan(DataStruct cur, DataStruct incre, OrderDependency od) {
		for (String rhs : od.getRHS()) {
			if (cur.getByName(rhs).equals(incre.getByName(rhs)) == false) {
				return Cmp.compare(cur.getByName(rhs),incre.getByName(rhs));
			}
		}
		return 0;
	}

}
