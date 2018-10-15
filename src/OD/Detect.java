package OD;

import java.util.ArrayList;

import Data.Cmp;
import Data.DataStruct;
import Test.*;

public class Detect {	
	ArrayList<Integer> preList=new ArrayList<Integer>(),nextList=new ArrayList<Integer>(),
			curList=new ArrayList<Integer>(),increList=new ArrayList<Integer>();
	//ArrayList<DataStruct> objectList = new ArrayList<DataStruct>();
	public Detect() {

	}
	
	public Detect(ArrayList<Integer> pre, ArrayList<Integer> next,
			ArrayList<Integer> cur, ArrayList<Integer> incre) {
		preList = pre;
		nextList = next;
		curList = cur;
		increList = incre;
		//objectList = objList;
	}

	// 这里需要一个循环，来应对AB->CDEF这种右边有多条od的情况
	public String detectSingleOD(OrderDependency od) {
		DataStruct preData=preList.isEmpty()?null:ReadandCheck.objectList.get(preList.get(0));
		DataStruct nextData=nextList.isEmpty()?null:ReadandCheck.objectList.get(nextList.get(0));
		DataStruct curData=curList.isEmpty()?null:ReadandCheck.objectList.get(curList.get(0));
		DataStruct increData=ReadandCheck.objectList.get(increList.get(0));
		
		System.out.print("\nchecking od: ");
		od.printOD();
		if (ReadandCheck.debug) {
			System.out.print("ATTR_NAME: ");
			DataStruct.printAttrName();
			System.out.print("increData: ");
			increData.printSingleData();
			
			if (curData != null) {
				System.out.print("curData:   ");
				curData.printSingleData();
			}
				
		}
		boolean split=false;
		//TODO::swap 和split的检验还是一个问题
		for (String it : od.getRHS()) {
			
			String pv = preData == null ? "" : preData.getByName(it);
			String cv = curData == null ? "" : curData.getByName(it);
			String iv = increData.getByName(it);
			String nv = nextData == null ? "" : nextData.getByName(it);

			if(ReadandCheck.debug) {
				System.out.println("检查右边属性: "+it);
				System.out.println("pre: " + pv + "  cur: " + cv + "  next: " + nv + "  incre: " + iv);
			}
			
			
			if(Cmp.equals(cv,iv)==false) {
				if (Cmp.compare(pv, iv) >0||Cmp.compare(nv, iv) < 0)
					return "swap";
				else if (Cmp.equals(cv,"")==false&&Cmp.compare(pv, iv) <=0 &&  Cmp.compare(nv, iv) >= 0) {
					split=true;
					
					//这里是考虑到右边多元素的情况
					//比如右边pre：472 cur：521  next:562  incre:561 头一遭已经确定pre比后面的都小了，后面的循环就不能用pre的后序比了。
					if(Cmp.compare(pv, iv)<0) preData=null;
					else if(Cmp.compare(nv, iv)>0) nextData=null;
				}else if(Cmp.equals(cv,"")&&(Cmp.equals(pv,"")||Cmp.compare(pv, iv) <0) &&  (Cmp.equals(nv,"")||Cmp.compare(nv, iv) > 0))
					return "valid";
			}
		}
		if(split) return "split";
		return "valid";
	}
}
