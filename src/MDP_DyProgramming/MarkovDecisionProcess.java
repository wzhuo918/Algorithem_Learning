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
	public int PNUM = 200;
	public Map<Integer, Integer> counter = new HashMap<Integer, Integer>();
	// 二维数组，第一行
	public int[][] Sampletable = new int[3][PNUM];
	public int[] ReduceLoad = new int[4];
	public int[] ReduceLoad_Hash = new int[4];

	public int TOTALNUM = 100000;
	public int Samplenum = 0;
	public int AssPnum = 0;
	public int unAssPnum = 0;
	public int npAssPnum = 0;
	public int npunAssPnum = 0;
	public int npro = 0;

	public int UNAssiPNUM = -1;

	public int bbeta = 0;
	public int abeta = 0;

	boolean oncetime = true; // 一次分配完成的实验

	public List<Integer> AssignedPar = new LinkedList<Integer>();
	public List<Integer> unAssignedPar = new LinkedList<Integer>();

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
		for (int i = 0; i < PNUM; i++) {
			unAssignedPar.add(i);

		}
		System.out.println("unAssignedPar = " + unAssignedPar);

		try {
			String br;
			reader = new BufferedReader(new FileReader(file));
			while ((br = reader.readLine()) != null) {

				input = Integer.parseInt(br);

				int hashcodenum = input % PNUM;

				int hadoophashnum = input % 4;

				if (hadoophashnum == 0) {
					ReduceLoad_Hash[0]++;
				}

				if (hadoophashnum == 1) {
					ReduceLoad_Hash[1]++;
				}

				if (hadoophashnum == 2) {
					ReduceLoad_Hash[2]++;
				}

				if (hadoophashnum == 3) {
					ReduceLoad_Hash[3]++;
				}

				if (counter.containsKey(hashcodenum)) {
					int tempvalue = counter.get(hashcodenum);
					counter.put(hashcodenum, tempvalue + 1);
					Sampletable[1][hashcodenum] = tempvalue + 1;
				} else {
					counter.put(hashcodenum, 1);
					Sampletable[0][hashcodenum] = hashcodenum;
					Sampletable[1][hashcodenum] = 1;
				}

				Samplenum++;

				/**
				 * the moment to decide to mdp samplenum = 10%TOTALNUM, begin
				 * MDP
				 */
				bbeta = Samplenum;

				if ((Samplenum > Math.round((double) (TOTALNUM * 0.3)))
						&& ((bbeta - abeta) > Math
								.round((double) (TOTALNUM * 0.1)))
						&& unAssignedPar.size() > 0) {
					// System.out.println("Samplenum = " + Samplenum);
					abeta = Samplenum;
					oncetime = false;
					mdp();
				} else {
					if ((unAssignedPar.size() > 0) && (bbeta == TOTALNUM)) {
						mdp();
					}

				}
			}

			System.out.println("Samplenum = " + Samplenum);

			// 按照hash值写入文件
			String outstring = String.valueOf(counter);

			outstring = outstring.substring(1, outstring.length() - 1);

			String[] outar = outstring.split(",");
			System.out.println("outstring = " + outstring);

			// FileWriter writer = new
			// FileWriter("/home/wzhuo/example/mdp/out.txt");
			FileWriter writer = new FileWriter("D:/out.txt");
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
		// TODO Auto-generated method stub
		/** sort samplingTable descending */
		for (int i = 0; i < Sampletable[1].length; i++) {
			int maxid = Sampletable[0][i];
			int maxnum = Sampletable[1][i];
			int maxdid = 0;

			for (int j = i + 1; j < Sampletable[1].length; j++) {
				if (maxnum < Sampletable[1][j]) {
					maxid = Sampletable[0][j];
					maxnum = Sampletable[1][j];
					maxdid = j;
				}
			}

			int tempval = 0;

			if (maxid != Sampletable[0][i]) {
				tempval = Sampletable[1][i];
				Sampletable[0][maxdid] = Sampletable[0][i];
				Sampletable[1][maxdid] = tempval;

				Sampletable[0][i] = maxid;
				Sampletable[1][i] = maxnum;

			} else {
				continue;
			}
		}
		// output matrix[3][]
		for (int i = 0; i < Sampletable.length; i++) {
			for (int j = 0; j < Sampletable[1].length; j++) {
				// System.out.println("Sampletable[" + i + "][" + j + "]=" +
				// Sampletable[i][j]);
				System.out.print(Sampletable[i][j]);
				System.out.print(' ');
			}
			System.out.println(' ');
		}

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		MarkovDecisionProcess mpd = new MarkovDecisionProcess();
		// mpd.readFileByLines("/home/wzhuo/example/mdp/zipf7.txt");
		mpd.readFileByLines("D:/zipf7.txt");

	}

}
