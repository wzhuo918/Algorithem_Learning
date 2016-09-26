package MDP_New_Greedy;

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
	public int MicrParNum = 100;
	public double Gam = 0.5;

	public Map<Integer, Integer> micrParMes = new HashMap<Integer, Integer>();   //用于存放每个Micro的信息
	// 三行采样数组，第一行为Mic的编号，第二行为负载量，第三行标识是否被分配完
	public int[][] Sampletable = new int[3][MicrParNum];

	public int TOTALNUM = 1000000;

	public int curSampleNum = 0;     //当前分配时的统计量
	public int lastSampleNum = 0;    //上轮分配时的统计量

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

					for (int i = 0; i < Sampletable[1].length; i++) {
						if (Sampletable[0][i] == microHashCode) {
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

				if ((curSampleNum > Math.round((double) (TOTALNUM * 0.1)))
					&& (curSampleNum < Math.round((double) (TOTALNUM * 0.95)))
					&& ((curSampleNum - lastSampleNum) > Math.round((double) (TOTALNUM * 0.1)))
					&& unAssignedParnum > 0 && (curSampleNum != TOTALNUM)) {

					DecimalFormat df = new DecimalFormat("#.####");

					System.out.println("rate = " + Double.parseDouble(df.format((double)curSampleNum/(double)TOTALNUM)));
					lastSampleNum = curSampleNum;
					mdp();

					for (int i = 0; i < Sampletable[1].length; i++) {
						if (Sampletable[2][i] == 1) {
							//System.out.print(" !! "+Sampletable[0][i]);
						}
					}

					//System.out.println();

				} else {

					if ((unAssignedParnum > 0) && (curSampleNum >= Math.round((double) (TOTALNUM * 0.95)))) {

						//System.out.println("last unAssignedParnum=" + unAssignedParnum);

						for (int i = 0; i < Sampletable[1].length; i++) {
							if (Sampletable[2][i] == -1) {
								unAssignedParnum--;
								Sampletable[2][i] = 1;
								System.out.println("  " + Sampletable[0][i] + "  " + Sampletable[0][i]);
							}
						}

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

		/** 按照负载量从大到小进行排序 */
		for (int i = 0; i < Sampletable[1].length; i++) {

			if (Sampletable[2][i] == -1) {

				int id = Sampletable[0][i];
				int idval = Sampletable[1][i];

				int maxpid = id;
				int maxnum = idval;

				for (int j = i; j < Sampletable[1].length; j++) {
					if (maxnum < Sampletable[1][j]) {
						maxpid = j;
						maxnum = Sampletable[1][j];
					}
				}

				if (idval < maxnum) {

					int tempid = Sampletable[0][maxpid];
					int tempval = Sampletable[1][maxpid];

					Sampletable[0][maxpid] = id;
					Sampletable[1][maxpid] = idval;

					Sampletable[0][i] = tempid;
					Sampletable[1][i] = tempval;
				}
			}
		}

		//		for (int i = 0; i < 3; i++) {
		//			for (int j = 0; j < Sampletable[1].length; j++) {
		//				System.out.print("  " + Sampletable[i][j]);
		//			}
		//			System.out.println("");
		//		}

		int eachAssiNum = 0;
		int totalUnaWeight = 0;
		int unAssiNum = 0;

		//统计所有未分配分区的总负载量
		for (int i = 0; i < Sampletable[1].length; i++) {
			if (Sampletable[2][i] == -1) {
				totalUnaWeight += Sampletable[1][i];
				unAssiNum++;
			}
		}

		//存放所有未分配分区的数组
		int[][] UnAssinSample = new int[3][unAssiNum];

		int uncounter = 0;
		for (int i = 0; i < Sampletable[1].length; i++) {
			if (Sampletable[2][i] == -1) {
				UnAssinSample[0][uncounter] = Sampletable[0][i];
				UnAssinSample[1][uncounter] = Sampletable[1][i];
				UnAssinSample[2][uncounter] = Sampletable[2][i];
				uncounter++;
			}
		}

		double[] TotalWeight = new double[unAssiNum];

		//System.out.println("unAssiNum=" + unAssiNum);
		
//		for(int i=0; i<UnAssinSample[2].length;i++){
//			System.out.println("UnAssinSample=" + UnAssinSample[1][i]);
//		}
		
		int candWeight = 0;
		int assignNum = 0;
		for (int i = 0; i < UnAssinSample[1].length; i++) {
			assignNum++;
			candWeight += UnAssinSample[1][i];

			DecimalFormat df = new DecimalFormat("#.####");
			double ap = (double) candWeight / (double) TOTALNUM;
			double eu = (double) (unAssiNum - assignNum) / (double) (MicrParNum);
			TotalWeight[i] = Double.parseDouble(df.format(ap * eu));
			
			//System.out.println("assignNum=" + assignNum);

			double[] RestWeight = new double[unAssiNum - assignNum];
			int restweight = 0;
			int restassignNum = 0;
			for (int j = 0; j < RestWeight.length; j++) {
				restassignNum++;
				restweight += UnAssinSample[1][assignNum+j];
				//System.out.println("assignNum=" + assignNum);

				DecimalFormat restdf = new DecimalFormat("#.####");
				double restap = (double) restweight / (double) TOTALNUM;
				double resteu = (double) (unAssiNum - assignNum - restassignNum) / (double) (MicrParNum);
				RestWeight[j] = Double.parseDouble(restdf.format(restap * resteu));
				
				if(i==0){
					//System.out.println("RestWeight=" + RestWeight[j]);
				}
			}

			double restmaxnum = 0;
			int remaxid = 0;
			for (int rei = 0; rei < RestWeight.length; rei++) {
				if (restmaxnum < RestWeight[rei]) {
					restmaxnum = RestWeight[rei];
					remaxid = rei;
				}
			}
			//System.out.println("restmaxnum=" + restmaxnum);
			//System.out.println("111TotalWeight=" + TotalWeight[i]);
			TotalWeight[i] += Gam * restmaxnum;
			//System.out.println("TotalWeight=" + TotalWeight[i]);
		}

		double maxnum = 0;
		int maxid = 0;
		for (int i = 0; i < TotalWeight.length; i++) {
			//System.out.println("TotalWeight=" + TotalWeight[i]);
			if (maxnum < TotalWeight[i]) {
				maxnum = TotalWeight[i];
				maxid = i;
			}
		}

		System.out.println("maxnum=" + maxid);
		for (int i = 0; i < Sampletable[1].length; i++) {
			if ((maxid > 0) && (Sampletable[2][i] == -1)) {
				//System.out.print(" " + Sampletable[0][i]);
				unAssignedParnum--;
				Sampletable[2][i] = 1;
				maxid--;
			}
		}
		System.out.println();

		//		for (int i = 0; i < 3; i++) {
		//			for (int j = 0; j < UnAssinSample[1].length; j++) {
		//				System.out.print("  " + UnAssinSample[i][j]);
		//			}
		//			System.out.println(" ");
		//		}

		//System.out.println("unAssPnum="+unAssPnum);

		//System.out.println("");

		//		System.out.println("maxnum=" + maxid);
		//				for (int i = 0; i < Sampletable[1].length; i++) {
		//					if ((maxid > 0) && (Sampletable[2][i] == -1)) {
		//						System.out.print(" " + Sampletable[0][i]);
		//						unAssignedParnum--;
		//						Sampletable[2][i] = 1;
		//						maxid--;
		//					}
		//				}
		//		System.out.println();
		//System.out.println("unAssignedParnum="+unAssignedParnum);
		//System.out.println("unAssPnum="+ unAssPnum);
		//System.out.println("!!!!!!!!!!");
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		MarkovDecisionProcess mpd = new MarkovDecisionProcess();
		// mpd.readFileByLines("/home/wzhuo/example/mdp/zipf7.txt");
		mpd.readFileByLines("E:/博士/论文/数据集/zipf/zipf8.txt");
		//mpd.readFileByLines("D:/workspace/out1.txt");

	}

}
