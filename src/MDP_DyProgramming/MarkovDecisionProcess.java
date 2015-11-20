package MDP_DyProgramming;

/**
 * 本程序是使用马尔科夫决策过程来解决多轮分区的问题
 * 
 * Description: (1) read data from a file
 * 				(2) MDP
 * 				(3) write output in a file 
 **/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MarkovDecisionProcess {
	public int MicrParNum = 200;
	public int ReduceNum = 4;
	
	public Map<Integer, Integer> micrParMes = new HashMap<Integer, Integer>();   //用于存放每个Micro的信息
	// 三行采样数组，第一行为Mic的编号，第二行为负载量，第三行标识是否被分配完
	public int[][] Sampletable = new int[3][MicrParNum];

	public int TOTALNUM = 1000000;
	public int AssPnum = 0;      //已分配分区的个数
	public int unAssPnum = 0;    //未分配分区的个数
	
	public int AssProweight = 0;   //已采用且分配的负载量   
	public int unAssProweight = 0;  //已采用且未分配的负载量


	public int curSampleNum = 0;     //当前分配时分区的统计量
	public int lastSampleNum = 0;    //上轮分配时分区的统计量

	boolean oncetime = true;         // 一次分配完成的实验

	public List<Integer> AssignedPar = new LinkedList<Integer>();      //已分配分区的链表
	public List<Integer> unAssignedPar = new LinkedList<Integer>();    //未分配分区的链表

	/**
	 * Read and write a file
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void readFileByLines(String fileName) throws IOException {
		File file = new File(fileName);
		BufferedReader reader = null;

		// String tempString =null;
		int input = 0;

		/** init MDP.state and MDP.action */
		for (int i = 0; i < Sampletable[2].length; i++) {
			Sampletable[2][i] = -1;
		}
		for (int i = 0; i < MicrParNum; i++) {
			unAssignedPar.add(i);
			unAssPnum++;
		}
		//System.out.println("unAssignedPar = " + unAssignedPar);
		
		try {
			String br;
			reader = new BufferedReader(new FileReader(file));
			while ((br = reader.readLine()) != null) {

				input = Integer.parseInt(br);

				int microHashCode = input % MicrParNum;

				if (micrParMes.containsKey(microHashCode)) {
					int tempvalue = micrParMes.get(microHashCode);
					micrParMes.put(microHashCode, tempvalue + 1);
					
					for(int i=0; i<Sampletable[1].length; i++){
						if(Sampletable[0][i] == microHashCode){
							Sampletable[1][i] = tempvalue + 1;
						}
					}
					
				} else {
					micrParMes.put(microHashCode, 1);
					Sampletable[0][microHashCode] = microHashCode;
					Sampletable[1][microHashCode] = 1;
				}

				curSampleNum++;

				/**
				 * the moment to decide to mdp samplenum = 10%TOTALNUM, begin
				 * MDP
				 */
				
				if ((curSampleNum > Math.round((double) (TOTALNUM * 0.2)))
						&& ((curSampleNum - lastSampleNum) > Math.round((double) (TOTALNUM * 0.3)))
						&& unAssignedPar.size() > 0 && (curSampleNum != TOTALNUM)) {
					// System.out.println("Samplenum = " + Samplenum);
					lastSampleNum = curSampleNum;
					oncetime = false;
					mdp();
				} else {
//					if ((unAssignedPar.size() > 0) && (curSampleNum == TOTALNUM)) {
//						mdp();
//					}

				}
			}

			//System.out.println("curSampleNum=" + curSampleNum);
			
			
			/**
			 * 将结果写入文件夹中
			 */			
			// 按照hash值写入文件
			String outstring = String.valueOf(micrParMes);

			outstring = outstring.substring(1, outstring.length() - 1);

			String[] outar = outstring.split(",");
			//System.out.println("outstring = " + outstring);

			// FileWriter writer = new FileWriter("/home/wzhuo/example/mdp/out.txt");
			FileWriter writer = new FileWriter("D:/workspace/out.txt");
			BufferedWriter bw = new BufferedWriter(writer);

			for (int i = 0; i < outar.length; i++) {
				if (i == 0) {
					// System.out.println("outar[ "+i+"]=" + outar[i]);
					bw.write(outar[i] + '\r');
				} else {
					// System.out.println("outar["+i+"]=" + outar[i]);
					outar[i] = outar[i].substring(1, outar[i].length());
					bw.write(outar[i] + '\r');
				}
			}
			
			bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	/**
	 * MDP()
	 */
	private void mdp() {
		/** 按照负载量从大到小进行排序  */
		for (int i = 0; i < Sampletable[1].length; i++) {
			int id = Sampletable[0][i];
			int idval = Sampletable[1][i];
			
			int maxpid = id;
			int maxnum = idval;

			for (int j = i ; j < Sampletable[1].length; j++) {
				if (maxnum < Sampletable[1][j]) {
					maxpid = j;
					maxnum = Sampletable[1][j];
				}
			}
			
			if (idval < maxnum) {
				
				int tempid =  Sampletable[0][maxpid];
				int tempval =  Sampletable[1][maxpid];
				
				Sampletable[0][maxpid] = id;
				Sampletable[1][maxpid] = idval;

				Sampletable[0][i] = tempid;
				Sampletable[1][i] = tempval;

			} 
		}
		// output matrix[3][]
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < Sampletable[1].length; j++) {
				System.out.print("  "+ Sampletable[i][j] );
			}
			System.out.println("");
		}
		
		
		/**
		 * 动态规划算法
		 */
		int eachAssiNum = 0;
		int puweight = 0;
		
		for(int i=0; i<Sampletable[1].length; i++){
			if(Sampletable[2][i] == -1){
				puweight +=Sampletable[1][i];
			}
		}
		
		
		//System.out.println("unAssPnum="+unAssPnum);
		double[] weight  = new double[unAssPnum];
		int assweight = 0;
		for(int i=0; i<Sampletable[1].length; i++){
			if(Sampletable[2][i] == -1){
			eachAssiNum++;
			assweight += Sampletable[1][i];
			System.out.print("   eachAssiNum=" +eachAssiNum + "   assweight=" + assweight);
			weight[i]=(double)((assweight/puweight) - (eachAssiNum/unAssPnum));
			System.out.print("  assweight/puweight= " +assweight/puweight);
			System.out.print("  eachAssiNum/unAssPnum= " +eachAssiNum/unAssPnum);
			System.out.print("  weight[" +i+ "]"+weight[i]);
			}
		}
		
		System.out.println("");
		int maxnum = 0;
		for(int i=0; i<weight.length-1;i++){
			if(weight[i] < weight[i+1]){
				maxnum++;
				unAssPnum--;
			}
		}
		System.out.println("maxnum="+ maxnum);
		System.out.println("unAssPnum="+ unAssPnum);
		System.out.println("!!!!!!!!!!");
		

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		MarkovDecisionProcess mpd = new MarkovDecisionProcess();
		// mpd.readFileByLines("/home/wzhuo/example/mdp/zipf7.txt");
		mpd.readFileByLines("D:/workspace/zipf7.txt");

	}

}
