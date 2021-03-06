package OD;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import Test.*;

public class OrderDependency {
	private ArrayList<String> LHS,RHS;
	static final String lr_separator="->";
	static final String attr_separator=","; 
	
	public OrderDependency() {
		LHS= new ArrayList<String>();
		RHS = new ArrayList<String>();
	}
	
	public OrderDependency(OrderDependency cp) {
		LHS= new ArrayList<String>();
		RHS = new ArrayList<String>();
		for(String lhs:cp.LHS) {
			LHS.add(lhs);
		}
		for(String rhs:cp.RHS) {
			RHS.add(rhs);
		}
	}
	public void copy(OrderDependency d) {
		this.LHS.clear();
		this.RHS.clear();
		for(String it:d.LHS) LHS.add(it);
		for(String it:d.RHS) RHS.add(it);
	}
	public void addLHS(String s) {
		LHS.add(s);
	}
	public void addArray2LHS(String[] as) {
		for(int i=0;i<as.length;i++) {
			LHS.add(as[i]);
		}
	}
	public void addRHS(String s) {
		RHS.add(s);
	}
	public void addArray2RHS(String[] as) {
		for(int i=0;i<as.length;i++) {
			RHS.add(as[i]);
		}
	}
	
	public void addLHS(int it,String s) {
		LHS.add(it,s);
	}
	
	
	public void deleteRHSTail() {
		RHS.remove(RHS.size()-1);
	}
	
	public ArrayList<String> getLHS(){
		return this.LHS;
	}
	
	public ArrayList<String> getRHS(){
		return this.RHS;
	}
	//检查当前od是否包含cod
	public int isContain(OrderDependency cod) {
		int cit=0,it=0;

		//找到this中与cod第一个相同的属性
		while(it<this.LHS.size()&&this.LHS.get(it).equals(cod.getLHS().get(0))==false) {
			it++;
		}
		
		while(it<this.LHS.size()&&cit<cod.getLHS().size()&&this.LHS.get(it).equals(cod.getLHS().get(cit))) {
			it++;
			cit++;
		}
		
		if(cit==cod.getLHS().size()) return it;
		return -1;
	}
	public boolean isEqual(OrderDependency cod) {
		if(this.LHS.size()!=cod.getLHS().size()) return false;
		for(int i=0;i<this.LHS.size();i++) {
			if(this.LHS.get(i).compareTo(cod.getLHS().get(i))!=0) return false;
		}
		return true;
	}
	
	public void printOD() {
		System.out.print(LHS.get(0));
		for(int i=1;i<LHS.size();i++) {
			System.out.print(attr_separator+LHS.get(i));
		}
		
		System.out.print(lr_separator+RHS.get(0));
		for(int i=1;i<RHS.size();i++) {
			System.out.print(attr_separator+RHS.get(i));
		}
		System.out.print("\n");
	}
	
	
//	public void filePrintOD() {
//		
//		
//		try {   
//			BufferedWriter bw= ReadandCheck.bw;
//			bw.write(LHS.get(0));
//			for(int i=1;i<LHS.size();i++) {
//				bw.write(attr_separator+LHS.get(i));
//			}
//			
//			bw.write(lr_separator+RHS.get(0));
//			for(int i=1;i<RHS.size();i++) {
//				bw.write(attr_separator+RHS.get(i));
//			}
//			//bw.write("\n");
//			bw.newLine();
//			
//			
//		    
//		    
//		} catch (IOException e) { 
//			e.printStackTrace();   
//		}  
//		
//	}
	
}
