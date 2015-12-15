/**
 * 
 * 使用动态规划算法实现收益函数为：{Wc/W * Nu/N}
 * 其中Wc本轮选取的大小，W为总数据量的大小；
 * Nu为选取后剩余的，N为总的个数
 * 
 */
package NewRF_DP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DP_ReFunction {

	public int DecisionTime = 10;
	public static int TotalWeight;
	public static int TotalParNum = 100;
	public int[][] PartitionMes = new int[3][TotalParNum];

	@SuppressWarnings("resource")
	public void readline() throws IOException{

		// 随机产生指定范围的内不重复的一列数
		//PartitionMes[1] = randomNum(1000, 50000, TotalParNum);
		
		
		
		//使用zipf分布的数据集
		String fileName="D:/workspace/zipf7.txt";
		//String fileName="D:/workspace/out1.txt";
		
		File file = new File(fileName);
		BufferedReader reader = null;
		String br;
		reader = new BufferedReader(new FileReader(file));
		int input =0 ;
		int microHashCode=0;
		int counter =0;
		
		while (((br = reader.readLine()) != null) && (counter<100000)) {

			input = Integer.parseInt(br);

			microHashCode = input % TotalParNum;
			
			PartitionMes[1][microHashCode]++;
			counter++;
		}
				
		//初始化分区的信息队列
		for (int i = 0; i < TotalParNum; i++) {
			PartitionMes[0][i] = i;
			PartitionMes[2][i] = -1;
			//PartitionMes[1][i] = i;
			//			System.out.print(PartitionMes[0][i] + " ");
						//System.out.println(PartitionMes[1][i] + " ");
			//			System.out.print(PartitionMes[2][i]);
			//
			//			System.out.println();
		}

		// 对现在分区的数据量从大到小进行排序
		for (int i = 0; i < PartitionMes[1].length; i++) {
			int id = PartitionMes[0][i];
			int idval = PartitionMes[1][i];

			int maxpid = id;
			int maxnum = idval;

			for (int j = i; j < PartitionMes[1].length; j++) {
				if (maxnum < PartitionMes[1][j]) {
					maxpid = j;
					maxnum = PartitionMes[1][j];
				}
			}

			if (idval < maxnum) {

				int tempid = PartitionMes[0][(int) maxpid];
				int tempval = PartitionMes[1][(int) maxpid];

				PartitionMes[0][(int) maxpid] = id;
				PartitionMes[1][(int) maxpid] = idval;

				PartitionMes[0][i] = tempid;
				PartitionMes[1][i] = tempval;
			}
		}

		// for (int i = 0; i < 3; i++) {
		for (int j = 0; j < PartitionMes[1].length; j++) {
			// System.out.print("  " + PartitionMes[i][j]);
			TotalWeight += PartitionMes[1][j];
		}
		//System.out.println("TotalWeight" + TotalWeight);

		/**
		 * 动态规划算法实现MDP的分配
		 */
		dp_mdp();
		
		for (int j = 0; j < PartitionMes[1].length; j++) {
			System.out.print("  " + PartitionMes[0][j]);
			System.out.print("  " + PartitionMes[1][j]);
			System.out.print("  " + PartitionMes[2][j]);
			System.out.println();
		}
		

	}

	// 随机产生不重复的数字放到初始的3元数组中
	public int[] randomNum(int min, int max, int n) {
		if (n > (max - min + 1) || max < min) {
			return null;
		}
		int[] result = new int[n];
		int count = 0;
		while (count < n) {
			int num = (int) (Math.random() * (max - min)) + min;
			boolean flag = true;
			for (int j = 0; j < n; j++) {
				if (num == result[j]) {
					flag = false;
					break;
				}
			}
			if (flag) {
				result[count] = num;
				count++;
			}
		}
		return result;
	}

	
	// 动态规划实现mdp
	public void dp_mdp() {

		// dp决策表格
		double[][] dp_weight = new double[DecisionTime][TotalParNum];

		for (int t = 0; t < DecisionTime; t++) {

			// 统计所有未分配的分区个数
			int noallocatednum = 0;
			for (int no = 0; no < PartitionMes[0].length; no++) {
				if (PartitionMes[2][no] == -1) {
					noallocatednum++;
				}
			}

			// 已分配的个数
			int allocatednum = TotalParNum - noallocatednum;
			//System.out.println("allocatednum=" + allocatednum);
			if (t > 0) {
				for (int k = 0; k < allocatednum; k++) {
					dp_weight[t][k] = 0;
				}
			}

			//DecimalFormat df = new DecimalFormat("#.#####");
			double tempweight = 0;
			int allocateweight = 0;
			for (int j = 0; j < noallocatednum; j++) {
				allocateweight += PartitionMes[1][allocatednum + j];

				tempweight = ((((double) allocateweight / (double) TotalWeight)
					* (((double) TotalParNum - (double) allocatednum - (double) j) / (double) TotalParNum)));

				dp_weight[t][j + allocatednum] = tempweight;
				
				
				//System.out.println("dp_weight[" + t + "][" + (j + allocatednum) + "]=" + tempweight);
			}

			
			double maxnum = Double.MIN_VALUE;
			int maxid = 0;
			for (int i = 0; i < dp_weight[0].length; i++) {
				if (maxnum < dp_weight[t][i]) {
					maxnum = dp_weight[t][i];
					maxid = i;
				}
			}
			//System.out.println("maxid=" + maxid);
			int assignum = maxid - allocatednum;
			System.out.println("assignnum=" + assignum);
			
			if( (assignum == 0) && (maxid < TotalParNum)){
				assignum = (int) (Math.ceil(noallocatednum/t));
			}

			for (int i = 0; i < PartitionMes[2].length; i++) {
				if (assignum > 0 && PartitionMes[2][i] == -1) {
					PartitionMes[2][i] = 1;
					assignum--;
					System.out.print(" " + PartitionMes[0][i]);
				}
			}
			System.out.println();
		}
	}

	
	public static void main(String[] args) throws Throwable, IOException {
		// TODO Auto-generated method stub

		
	DP_ReFunction dp = new DP_ReFunction();
	dp.readline();
	}
	

}
