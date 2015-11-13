public class DynamicProgramming {
	/**
	 * 硬币找零：动态规划算法
	 * 
	 * @param values
	 *            :保存每一种硬币的币值的数组
	 * @param valueKinds
	 *            :币值不同的硬币种类数量，即coinValue[]数组的大小
	 * @param money
	 *            :需要找零的面值
	 * @param coinsUsed
	 *            :保存面值为i的纸币找零所需的最小硬币数
	 */
	public static void makeChange1(int[] values, int valueKinds, int money,
			int[][] coinsUsed) {

		coinsUsed[0][0] = 0;
		// 对每一分钱都找零，即保存子问题的解以备用，即填表
		for (int cents = 1; cents <= money; cents++) {

			// 当用最小币值的硬币找零时，所需硬币数量最多
			int minCoins = cents;
			int temp = 0;

			// 遍历每一种面值的硬币，看是否可作为找零的其中之一
			for (int kind = 0; kind < valueKinds; kind++) {
				// 若当前面值的硬币小于当前的cents则分解问题并查表
				if (values[kind] <= cents) {
					if (temp == 0) {
						temp = coinsUsed[cents - values[kind]][0] + 1;
						// 为每个币值添加组成自己的链表
						if (temp == 1) {
							coinsUsed[cents][0] = temp;
							coinsUsed[cents][1] = values[kind];
							// System.out.println("10coinsUsed["+cents+"][0]="+coinsUsed[cents][0]);
							// System.out.println("11coinsUsed["+cents+"][1]="+coinsUsed[cents][1]);
							break;
						} else {
							coinsUsed[cents][0] = temp;
							// System.out.println("2coinsUsed["+cents+"][0]="+coinsUsed[cents][0]);
							int i = 1;
							int total = 0;
							for (i = 1; i <= temp - 1; i++) {
								coinsUsed[cents][i] = coinsUsed[cents - 1][i];
								total += coinsUsed[cents][i];
								// System.out.println("3coinsUsed["+cents+"]["+i+"]="+coinsUsed[cents][i]);
							}

							for (int j = 0; j < valueKinds; j++) {

								if (total + values[j] > cents) {
									continue;
								} else {
									coinsUsed[cents][temp] = values[j];
									break;
								}
							}

							// System.out.println("4coinsUsed["+cents+"]["+temp+"]="+coinsUsed[cents][temp]);
							break;
						}
					}
				}
			}
		}
	}

	public static void main(String[] args) {

		// 硬币面值预先已经按降序排列
		int[] coinValue = new int[] { 25, 21, 10, 5, 1 };
		// 需要找零的面值
		int money = 63;
		// 求存放每个币值所需要的最大数组数目
		int maxnumber = 0;
		for (int i = 0; i < coinValue.length - 1; i++) {
			if (maxnumber < coinValue[i] / coinValue[i + 1] + 3) {
				maxnumber = coinValue[i] / coinValue[i + 1] + 3;
			}
		}
		//System.out.println("maxnumber" + maxnumber);
		// 保存每一个面值找零所需的最小硬币数，0号单元舍弃不用，所以要多加1
		int[][] coinsUsed = new int[money + 1][maxnumber];

		makeChange1(coinValue, coinValue.length, money, coinsUsed);
		
		for(int i=1; i<money+1;i++){
			System.out.print("零钱为["+i+"]的最小找零个数为：" + coinsUsed[i][0]  + "    币值分别为{");
			for(int j=1; j<maxnumber;j++){
				if(coinsUsed[i][j]!=0){
					System.out.print( coinsUsed[i][j] +" ");
				}
			}
			System.out.println("}");
		}
	}
}