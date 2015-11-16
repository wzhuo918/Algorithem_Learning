package BinPacking;

public class binpacking {  
	   
    public static void main(String[] args) {  
        // TODO Auto-generated method stub                
        final int packageWheight=10;//包的重量  
        Package[] pg={  
           new Package(6,2,"a"),  
           new Package(3,2,"b"),  
           new Package(5,6,"c"),  
           new Package(4,5,"d"),  
           new Package(6,4,"e")          
        };  
          
        int[][] bestValues = new int[pg.length+1][packageWheight+1];  
      
        for(int i=0;i<=pg.length;i++){  
            for(int j=0;j<=packageWheight;j++){  
                if(i==0||j==0){  
                    bestValues[i][j]=0;//临界情况  
                }  
                else{  
                    if(j<pg[i-1].getWheight()){  
                        bestValues[i][j] = bestValues[i-1][j];//当第n件物品重量大于包的重量时，最佳值取前n-1件的  
                    }  
                    else{  
                           int iweight = pg[i-1].getWheight(); //当第n件物品重量小于包的重量时，分两种情况，分别是装第n件或不装，比较取最大  
                            int ivalue = pg[i-1].getValue();      
                            bestValues[i][j] =       
                                Math.max(bestValues[i-1][j], ivalue + bestValues[i-1][j-iweight]);            
                    }  
                }  
            }  
        }  
          
        System.out.print(""+bestValues[pg.length][packageWheight]);  
        }  
    }  
  
  
class Package {  
  
    int value;  
    int wheight;  
    String name;  
    Package(int value,int wheight,String name){  
        this.value=value;  
        this.wheight=wheight;  
        this.name=name;  
    }  
    public int getWheight(){  
        return wheight;  
    }  
    public int getValue(){  
        return value;  
    }  
    public String getName(){  
        return name;  
    }  
} 
