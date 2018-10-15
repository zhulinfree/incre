package OD;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ODs {
	public static final String ENCODE = "UTF-8";
	public ArrayList<OrderDependency> ods;
	public ODs() {
		ods = new ArrayList<OrderDependency>();
	}
	//OD形式：year,month name,age空格分隔左右，逗号分隔属性顺序
	public void storeOD(String fileName) throws Exception {

		FileInputStream fis = new FileInputStream(fileName);
		InputStreamReader isw = new InputStreamReader(fis, ENCODE);
		BufferedReader   br = new BufferedReader(isw);
		//boolean bReadNext = true;
		String line=new String();
		line=br.readLine();
		if(line==null) System.out.println("fail to read");
		while(line!=null) {
			line.trim();
			String[] adder=line.split(OrderDependency.lr_separator);
			String[] left=adder[0].split(OrderDependency.attr_separator);
			String[] right=adder[1].split(OrderDependency.attr_separator);
			OrderDependency in=new OrderDependency();
			in.addArray2LHS(left);
			in.addArray2RHS(right);
			ods.add(in);
			line=br.readLine();
		}     
	}
	
	
	public void print() {
		for(int i=0;i<ods.size();i++) {
			ods.get(i).printOD();
		}
		System.out.println("\n");
	}
	
}
