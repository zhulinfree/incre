package Test;

import Data.*;
import EquivalenceClass.*;
import OD.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import BplusTree.InstanceKey;


public class ReadandCheck {
	public static boolean debug;
	public final static int order = 5;
	public static Index indexes=new Index();
	private static ArrayList<OrderDependency> odList;
	private static ArrayList<DataStruct> objectList,iObjectList;
	private static ArrayList<OrderDependency> originalODList=new ArrayList<OrderDependency>(),
			incorrectODList=new ArrayList<OrderDependency>(),
			enrichODList=new ArrayList<OrderDependency>();
	private static ArrayList<ArrayList<String>> ind_list=new ArrayList<>();
	
	public static BufferedWriter bw;
	
	
	public static void main(String[] args) {
		
		initial();
	
		
		/*将读入的od输出*/
		System.out.println("The original od is:");
		for(OrderDependency od:odList) od.printOD();
		System.out.println("共有"+objectList.size()+"条数据\n共有"+iObjectList.size()+"条增量数据");
	
		
		
		
		
		long start = System.currentTimeMillis( );
		 for(int i=0;i<iObjectList.size();i++) {
			 if(i%100==0) 
				 System.out.println("已处理 "+i+" 条");
			 checkAllOD(originalODList,i);
			 enrichment();
			 checkAllOD(enrichODList,i);
			 objectList.add(iObjectList.get(i));
			 incorrectODList.clear();
			enrichODList.clear();
			// indexes.updateIndexes(iObjectList.get(i));
			// listClear();
			 
		 }
		
		long end = System.currentTimeMillis( );
        long diff = end - start;
		System.out.println(objectList.size()-iObjectList.size()+"条数据"+iObjectList.size()+"条增量数据 共耗时"+diff+"毫秒");
		System.out.println("\n\n\nThe latest ods is:");
		if(!odList.isEmpty()) for(OrderDependency od:odList) od.printOD();
		
		
//		if(Debug.fileout) {
//			try {   
//				FileWriter fw =new FileWriter("result1.txt");
//				bw= new BufferedWriter(fw);
//			    bw.write(objectList.size()-iObjectList.size()+"条数据"+iObjectList.size()+"条增量数据 共耗时"+diff+"毫秒");			   
//			    bw.newLine();
//			    bw.write("The latest ods is:");
//			    if(!odList.isEmpty()) for(OrderDependency od:odList) od.filePrintOD();
//			    bw.flush();   
//			    bw.close();    
//			} catch (IOException e) {   
//				e.printStackTrace();   
//			}    
//		}
	}
	

	private static void checkAllOD(ArrayList<OrderDependency> checkedOD,int incre_tid) {
		//对每一条od进行验证
		for(OrderDependency nod:checkedOD) {
			
			
			ArrayList<Integer> preList,nextList,curList,increList=new ArrayList<Integer>();
			
			objectList.add(iObjectList.get(incre_tid));
			//最后一行是增量数据
			increList.add(objectList.size()-1);
			
			
			InstanceKey key=new InstanceKey(nod.getLHS(),iObjectList.get(incre_tid));
			
			int indid=getIndexId(key.getAttrName());
			if(debug) System.out.println("index id is "+indid);

			//TODO::主要是找不到匹配索引的时候怎么办；最蠢就是重新建立一个索引这样
			if(indid==-1) continue;
			
			preList=indexes.getPre(key,indid);
			curList=indexes.getCur(key,indid);
			nextList=indexes.getNext(key,indid);
			

					
			if(debug) {
				System.out.print("\n\nThe current data is tuple: ");
				if(curList!=null)
				for(Integer i:curList) {
					System.out.print(i+" ");
				}
			}
				
		
			Detect d=new Detect(preList,nextList,curList,increList);
			String detectRes=d.detectSingleOD(nod);
			
			if(debug) System.out.println(detectRes);
			
			if(detectRes.equals("valid")==false) {
				odList.remove(nod);
				incorrectODList.add(nod);
				//扩展od
				Extend et=new Extend(preList ,nextList,curList,increList);
				ArrayList<OrderDependency> newOdList=new ArrayList<OrderDependency>();
				newOdList=et.extend(nod,detectRes);
				
				if(!newOdList.isEmpty()) {
					
					System.out.print("modify od: ");
					nod.printOD();
				
					int count=0;
					for(OrderDependency no:newOdList) {
					
						System.out.print((count++)+". ");
						no.printOD();
						
						odList.add(no);
					}
				}
			}
			objectList.remove(objectList.size()-1);
		}
	}

	public static void enrichment() {
		if(incorrectODList.isEmpty()) return;
		for(OrderDependency iod:incorrectODList) {
			for(OrderDependency ood:originalODList) {
				if(iod.getLHS().size()<ood.getLHS().size()&&ood.isEqual(iod)==false&&ood.isContain(iod)!=-1) {
					enrichSingleOD(ood,iod,ood.isContain(iod));
				}
			}
		}
	}
	
	//od:需要被扩展的od，iod：错误的od，it：需要插入iod右边的起始index.最后都放到enrichODList中
	public static void enrichSingleOD(OrderDependency od,OrderDependency iod,int it) {
		OrderDependency tmp;
		if(it==od.getLHS().size()) return;//如果iod正好在od的尾巴上，没必要扩展
		while(it<od.getLHS().size()) {
			tmp=new OrderDependency(od);
			int iter=it;
			for(String r:iod.getRHS()) {
				tmp.addLHS(iter++,r);
			}
			enrichODList.add(tmp);
			
			it++;
		}
		
	}

	public static void initial() {
		debug=Debug.debug;
		DataInitial.readData();
		objectList=DataInitial.objectList;
		iObjectList=DataInitial.iObjectList;
		odList=DataInitial.odList;
		listClear();
		calInd_list();
		indexes.buildIndexes(ind_list);
	}
	
	
	
	private static void calInd_list() {
		
		int divider=100;//抽样倍数。100就是1%
		double a=0.0001;//wi等于0的时候使用的代替因子
		double error=0.00001;//误差
		
		ArrayList<Vertex> u=new ArrayList<Vertex>();
		
		//hashMap记录属性序列在list中的位置如A,B在下标为2的地方
		HashMap<ArrayList<String>,Integer> set=new HashMap<>();
		
		for(OrderDependency od:originalODList) {
			ArrayList<String> indList=new ArrayList<String>();
			
			int D=objectList.size()/divider;//抽样
			D=D>0?D:objectList.size();//防止数据集小于100
		
			//对于OD中每个前缀
			for(String attr:od.getLHS()) {
				indList.add(attr);
				
				int flag=set.getOrDefault(indList,-1);
				if(flag!=-1) continue;
				Vertex inp_v=new Vertex(indList);
				
				//计算weight，抽样，w=(1-dis(x1)/D)*(1-dis(x2)/D)*...
				
				//对于Vertex中每个属性
				double wi=1.0;
				for(String at:indList) {
					HashSet<Integer> s=new HashSet<>();
					s.clear();
					//每隔20条抽一条
					for(int i=1;i<D;i++) {
						int tmp=objectList.get(i).getByName_int(at);
						s.add(tmp);
					}
					

					wi=(1-(s.size()*1.0)/D)>error?(1-(s.size()*1.0)/D)*wi:a*wi;

					//System.out.println("wi="+wi);
				}
				inp_v.weight=wi;
				ArrayList<String> adder=new ArrayList<>();
				adder.addAll(indList);
				set.put(adder, u.size());
				u.add(inp_v);
			}
			
			
		}
		
	
		//超图建立后进行计算顶点覆盖
		for(OrderDependency od:originalODList) {
			
			ArrayList<String> indList=new ArrayList<String>();
			
			double min=1;
			//对于每条超边，找到他的所有顶点，计算w-p的最小值
			for(String at:od.getLHS()) {
				indList.add(at);
				int x=set.getOrDefault(indList,-1);
				Vertex v=u.get(x);
				min=min<v.weight-v.price?min:v.weight-v.price;
			}
			indList.clear();
			for(String at:od.getLHS()) {
				indList.add(at);
				Vertex v=u.get(set.get(indList));
				v.price+=min;
			}
		}
		
		
		
		//price==weight 的放到输出序列中
		for(Vertex v:u) {
			//將每个顶点的price和weight输出
//			for(String s:v.attrList) {
//				System.out.print(s+" ");
//			}
//			System.out.println("\nprice="+v.price+" weight="+v.weight);
//			
//			
			
			if(Math.abs(v.price-v.weight)<error) {
				ArrayList<String> adder=new ArrayList<>();
				adder.addAll(v.attrList);
				ind_list.add(adder);
			}
		}
		

//		System.out.println("生成的IndList");
//		for(ArrayList<String> al:ind_list) {
//			for(String s:al) {
//				System.out.print(s+",");
//			}
//			System.out.println();
//		}
	
	}
	
	
	
	
	
	//将几个List进行初始化
	private static void listClear() {
		incorrectODList.clear();
		enrichODList.clear();
		originalODList.clear();
		//存储所有原有的od
		for(OrderDependency o:odList) {
			originalODList.add(new OrderDependency(o));
		}
	}
	
	public static void printList(ArrayList<OrderDependency> list,String sentence) {
		if(list.isEmpty()==false) System.out.println(sentence);
		for(OrderDependency od:list) {
			od.printOD();
		}
	}
	
	
	public static int getIndexId(ArrayList<String> todo) {
		
		int x=indexes.indexMap.getOrDefault(todo,-1);
		if(x!=-1) return x;
		
		//没有正好的就用范围大的，如AB索引
		ArrayList<String> tmp=new ArrayList<String>();
		tmp.addAll(todo);
		tmp.remove(tmp.size()-1);
		while(tmp.isEmpty()==false) {
			int r=indexes.indexMap.getOrDefault(tmp,-1);
			x=r==-1?x:r;
			tmp.remove(tmp.size()-1);
		}
		if(x!=-1) return x;
		
		//没有范围大的就用范围小的，在ABCD上查abc
		//return 0;
        
        for(Entry<ArrayList<String>, Integer> entry: indexes.indexMap.entrySet())
        {
         //System.out.println("Key: "+ entry.getKey()+ " Value: "+entry.getValue());
        	if(contain(entry.getKey(),todo)) return entry.getValue();
        }
		
        return -1;
	}
	
	//查看l1是否完全包括l2
	public static boolean contain(ArrayList<String> list1,ArrayList<String> list2) {
		if(list1.size()==0||list2.size()==0) return false;
		int count=0;
		for(String s2:list2) {
			if(s2.equals(list1.get(count))==false) return false;
			count++;
		}
		return true;
	}
	
}

class Vertex{
	public ArrayList<String> attrList=new ArrayList<>();
	public double price;
	public double weight;
	Vertex(ArrayList<String> attr){
		attrList.clear();
		if(!attr.isEmpty()) attrList.addAll(attr);
		price=0;
		weight=0;
	}
	
	
	@Override
    public boolean equals(Object o)
    {
        if(this == o)
        {
            return true;
        }
        if(!(o instanceof InstanceKey))
        {
            return false;
        }
        Vertex pn = (Vertex)o;
        if(this.attrList.size()!=pn.attrList.size()) return false;
		for(int i =0;i<this.attrList.size();i++) {
			if(!this.attrList.get(i).equals(pn.attrList.get(i))) return false;		
		}
		
		return true;
    }
}
