package MDP_Greedy;

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
	public int MicrParNum = 1000;
	public int ReduceNum = 4;
	
	public Map<Integer, Integer> micrParMes = new HashMap<Integer, Integer>();   //用于存放每个Micro的信息
	// 三行采样数组，第一行为Mic的编号，第二行为负载量，第三行标识是否被分配完
	public int[][] Sampletable = new int[3][MicrParNum];

	public int TOTALNUM = 1000000;
	
	public int AssProweight = 0;   //已采用且分配的负载量   
	public int unAssProweight = 0;  //已采用且未分配的负载量


	public int curSampleNum = 0;     //当前分配时分区的统计量
	public int lastSampleNum = 0;    //上轮分配时分区的统计量

    int unAssignedParnum = 0;
	
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
			unAssignedParnum++;
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
				
				if ((curSampleNum > Math.round((double) (TOTALNUM * 0.3)))&&
						(curSampleNum < Math.round((double) (TOTALNUM * 0.95)))
						&& ((curSampleNum - lastSampleNum) > Math.round((double) (TOTALNUM * 0.1)))
						&& unAssignedParnum > 0 && (curSampleNum != TOTALNUM)) {
					
					DecimalFormat df = new DecimalFormat("#.####");

					System.out.println("rate = " + Double.parseDouble(df.format((double)curSampleNum/(double)TOTALNUM)));
					lastSampleNum = curSampleNum;
					mdp();
				} else {
					
					if ((unAssignedParnum > 0) && (curSampleNum >= Math.round((double) (TOTALNUM * 0.95)))) {
						
						System.out.println("last unAssignedParnum=" + unAssignedParnum);
						
						for(int i=0; i<Sampletable[1].length; i++){
							if(Sampletable[2][i] == -1){
								unAssignedParnum--;
								Sampletable[2][i]=1;
							}
						}
						System.out.println("All has done!!");
					}

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
		 * 贪心算法
		 */
		int eachAssiNum = 0;
		int puweight = 0;
		int unAssiNum = 0;
		
		//统计所有未分配分区的总负载量
		for(int i=0; i<Sampletable[1].length; i++){
			if(Sampletable[2][i] == -1){
				puweight +=Sampletable[1][i];
				unAssiNum++;
			}
		}
		
		//System.out.println("unAssPnum="+unAssPnum);
		double[] weight  = new double[unAssiNum];
		int assweight = 0;

		for(int i=0; i<Sampletable[1].length; i++){
			if(Sampletable[2][i] == -1){
			eachAssiNum++;
			assweight += Sampletable[1][i];
			DecimalFormat df = new DecimalFormat("#.####");

			double ap = (double)assweight/(double)puweight;
			double eu = (double)eachAssiNum/(double)(unAssiNum); 
			weight[eachAssiNum-1] = Double.parseDouble(df.format(ap -eu));
			//System.out.print("  assweight/puweight= " + ap);
			//System.out.print("  eachAssiNum/unAssPnum= " + eu);
			System.out.print("  weight[" + (eachAssiNum-1)+ "]="+ weight[eachAssiNum-1]);
			}
		}
		
		System.out.println("");
		double maxnum = 0;
		int maxid = 0;
		for(int i=0; i<weight.length;i++){
			if(maxnum<weight[i]){
				maxnum = weight[i];
				maxid = i;
			}
		}
		System.out.println("maxnum="+ maxid);
		for(int i=0; i<Sampletable[1].length; i++){
			if(maxid >0 && Sampletable[2][i]==-1){
				unAssignedParnum--;
				Sampletable[2][i]=1;
				maxid--;
			}
		}
		
		//System.out.println("unAssPnum="+ unAssPnum);
		System.out.println("!!!!!!!!!!");
	}

	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		MarkovDecisionProcess mpd = new MarkovDecisionProcess();
		// mpd.readFileByLines("/home/wzhuo/example/mdp/zipf7.txt");
		mpd.readFileByLines("D:/workspace/zipf7.txt");

	}

}
