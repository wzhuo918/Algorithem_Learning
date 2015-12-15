package ProceedData;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ProduceRandomData {
	public int TOTALNUM = 1000000;
	
	public ProduceRandomData() throws IOException {
		// TODO Auto-generated constructor stub

		
		// FileWriter writer = new FileWriter("/home/wzhuo/example/mdp/out.txt");
		FileWriter writer = new FileWriter("D:/workspace/out1.txt");
		BufferedWriter bw = new BufferedWriter(writer);
		java.util.Random random=new java.util.Random();
		
		
		for(int i=0;i<TOTALNUM;i++){
			int result=random.nextInt(1000);
			bw.write(String.valueOf(result) +"\n");
			//System.out.println(result);
		}
	

		bw.close();
		
	}

	
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
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		ProduceRandomData pd = new ProduceRandomData();
	}
	
	
	

}


