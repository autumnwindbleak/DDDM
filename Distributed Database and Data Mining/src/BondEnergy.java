import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;



public class BondEnergy {
	
	public static int[][] useMatrix;  //store useMatrix[label][query]
	public static int[][] accMatrix;  //store accMatrix[query][site]
	public static int[] order;        //store the order of column after calculate the contribution
	
	/*
	 * This method is use to read data from files
	 * 
	 * @param name: the file name
	 * @return  return a string array contain line n in array[n]
	 * 
	 */
	public static String[] read(String name) throws IOException{        // 
		ArrayList<String> stock = new ArrayList<String>();        //store the new line in a string list
	  try{
		File f=new File(name);                              
		InputStreamReader reader = new InputStreamReader( new FileInputStream(f)); 
		BufferedReader br = new BufferedReader(reader);
		String line;
	    line= br.readLine();        //read by line and store in string list
	    while (line != null) {       
	      stock.add(line);
	      line=br.readLine();
	    }
	    br.close();
	  }catch (FileNotFoundException e) {
		    e.printStackTrace();
		}catch (Exception e) {  
          e.printStackTrace();  
          }	  
	  String[] recieve = new String[stock.size()];	  //change string list into string array
	  stock.toArray(recieve);
	  return recieve;
	}
	/*
	 * this method is use to get the useMatrix in integer form
	 * 
	 * @param Attributes_name: the name of attributes file
	 * @param Queries_name: the name of queries file
	 * @return return the useMatrix in integer  (useMatrix[label][query])
	 */
	
	public static int[][] getUseMatrix(String Attributes_name, String Queries_name) throws IOException{  
		String[] att_s,que_s;
		att_s=read(Attributes_name);    //get the string array of attributes file
		que_s=read(Queries_name);       //get the string array of queries file
		
		//get the name column from attribute file
		String[] att_e = new String[att_s.length];
		int start,end;
		for(int i=0;i<att_s.length;i++){ 		   		   //by line order scan the name of the label
			start=att_s[i].indexOf('\t')+1;		  		   //find the tab before the name column(first tab of the line)
			end=att_s[i].indexOf('\t',start);	 		   //find the tab after the name column(second tab of the line)
			if(end==-1)att_e[i]=att_s[i].substring(start); //store the name of each label in a string array if there is a description column
			else att_e[i]=att_s[i].substring(start,end);   //if the name column is the last column
		}
		
		//get the useMatrix
		int[][] useMatrix= new int[att_s.length][que_s.length];
		for(int i=0;i<att_e.length;i++)                             //scan the each name column
			for(int j=0;j<que_s.length;j++)							//scan the queries
				if(que_s[j].contains(att_e[i]))useMatrix[i][j]=1;	//if name of the label appear in the queries
				else useMatrix[i][j]=0;
		
		return useMatrix;
	}
	
	/*
	 * This method is use to get the accMatrix in integer form
	 * 
	 * @param filename: the accMatrix file name
	 * @return the accMatrix in integer form (accMatrix[query][site])
	 */
	
	public static int[][] getAccMatrix(String filename) throws IOException{    
		String[] tempMatrix;
		tempMatrix=read(filename);          //get the string array from accMatrix file
		int count=0,start=0,end=0;
		for(int i=0;i<tempMatrix[0].length();i++)      //scan one line to see how many site in the accMatrix
			if(tempMatrix[0].charAt(i)=='\t')count++; 
		
		//get the accMatrix
		int[][] accMatrix=new int[tempMatrix.length-1][count];
		for(int i=1;i<tempMatrix.length;i++){						//skip the first line and scan each line in accMatric file
			start=tempMatrix[i].indexOf('\t')+1;					//find the first tab before the data
			for(int j=0;j<count;j++){								//store each data in different site form accMatrix
				end=tempMatrix[i].indexOf('\t',start);				//find the tab after the data from site j
				if(end!=-1)accMatrix[i-1][j]=Integer.valueOf(tempMatrix[i].substring(start, end));  //store the data if it is not the data in the last site
				else accMatrix[i-1][j]=Integer.valueOf(tempMatrix[i].substring(start)); //if it is the last site's data
				start=end+1;			                            //get ready to get next data;
			}
		}
		return accMatrix;
		
	}
	
	/*
	 * This method is use to calculate the affinity value between Ai and Ak
	 * 
	 * @param i: index of the label (Ai)
	 * @param j: index of the label (Aj)
	 * @return the calculation of affinity between Ai and Aj
	 */
	public static double affinity(int i,int j){
		double numerator=0,denominator1=0,denominator2=0,result;
		for(int k=0;k<accMatrix.length;k++){					//calculate each query(Aik & Ajk)
			numerator+=calculateAik(i,k)*calculateAik(j,k);
			denominator1+=calculateAik(i,k)*calculateAik(i,k);
			denominator2+=calculateAik(j,k)*calculateAik(j,k);
		}
		result=numerator/(Math.sqrt(denominator1)*Math.sqrt(denominator2));
		return result;		
	}
	
	/*
	 * This method is use to calculate the Aik use in the affinity method
	 * 
	 * @param i: index of the label 
	 * @param k: index of the queries
	 * @return the calculation of Aik  
	 */
	public static int calculateAik(int i,int k){
		int sum=0;
		for(int j=0;j<accMatrix[k].length;j++)sum+=accMatrix[k][j]; //calculate each query in accMatrix
		sum=sum*useMatrix[i][k];                                    
		return sum;
	}
	
	/*
	 * This method is use to get the AAMatrix
	 * 
	 * @return AAMatrix
	 */
	public static double[][] calculateAA(){
		double[][] result = new double[useMatrix.length][useMatrix.length];
		for(int i=0;i<result.length;i++)			//index of label(Ai)
			for(int j=0;j<result[i].length;j++)		//index of label(Aj)
				result[i][j]=affinity(i,j);         //calculate the value at AiAj
		return result;
	}
	/*
	 * This method is use to calculate the bond between Ax and Ay
	 * 
	 * @param x: index of label
	 * @param y: index of label
	 * @return the calculation of bond
	 */
	public static double bond(int x,int y){
		double result=0;
		if(x==-1||y==-1)return 0;
		for(int i=0;i<useMatrix.length;i++) result+=affinity(i,x)*affinity(i,y); //calculate each label
		return result;
	}
	
	/*
	 * This method is use to calculate the contribution between when Ak is between Ai and Aj
	 * 
	 * @param i: the label at the left
	 * @param k: the calculate label
	 * @param j: the label at the right
	 * @return the calculation of contribution
	 */
	public static double con(int i,int k,int j){
		double result=0;
		result= 2*bond(i,k)+2*bond(k,j)-2*bond(i,j);
		return result;
	}
	/*
	 * insert each column in the right place according to the contribution
	 * 
	 * @param n: the column which is about to insert in order array
	 */
	public static void insert(int n){
		int location=n;
		double max=0,temp;
		for(int i=-1;i<n;i++)   //try each possible place from the left to the right
			if(i==-1){			//if it was at the leftmost
				temp=con(-1,n,order[i+1]);   
				if(max<=temp){			//if contribution is bigger then record the max contribution and location
					max=temp;		
					location=i;
				}
			}else
				if(i==n-1){		//if it was at the rightmost
					temp=con(order[i],n,-1);
					if(max<=temp){		//if contribution is bigger then record the max contribution and location
						max=temp;
						location=i;
					}
				}else
				{
					temp=con(order[i],n,order[i+1]);// if it was between leftmost and rightmost(not inclusive)
					if(max<=temp){		//if contribution is bigger then record the max contribution and location
						max=temp;
						location=i;
					}
				}
		for(int i=n-1;i>location;i--)order[i+1]=order[i]; //move the data at right of the location one index-long 
		order[location+1]=n; //insert the nth column in the right place
	}
	
	/*
	 * This method is use to change the AAMatrix into CAMatrix according to the order 
	 */
	public static double[][] calculateCA(double[][] AA){
		order=new int[useMatrix.length];
		order[0]=0;						//put two columns in order at first
		order[1]=1;
		for(int i=2;i<useMatrix.length;i++)insert(i); //insert other columns into right order
		//transfer AAMatrix into CAMatrix by order 
		double[][] result=new double[useMatrix.length][useMatrix.length];
		for(int i=0;i<order.length;i++)			// ith row
			for(int j=0;j<order.length;j++)		// jth column
				result[i][j]=AA[order[i]][order[j]]; //change according to the order
		return result;
	}
	/*
	 * This method is use to print the Matrix on the screen
	 * 
	 * @param Matrix: the Matrix about to print
	 */
	
	public static void output(double[][] Matrix){
		DecimalFormat df = new DecimalFormat("0.0000");
		df.setRoundingMode(RoundingMode.HALF_UP);
			System.out.print(" \t");		//print the space in the first line 
			for(int i=0;i<Matrix.length;i++)System.out.print("A"+(order[i]+1)+"\t"); //print the column name 
			System.out.println(); //change line
			for(int i=0;i<Matrix.length;i++){           	//print in line
				System.out.print("A"+(order[i]+1)+"\t"); 	//print the row name first
				for(int j=0;j<Matrix.length;j++)			//print the value of the current line
					System.out.print(df.format(Matrix[i][j])+"\t"); 
				System.out.println(); //change line
			}
	}
	
	public static void main(String[] args)throws IOException {
		useMatrix=getUseMatrix(args[1],args[2]); 	//get useMatrix store in the useMatrix array
		accMatrix=getAccMatrix(args[0]);			//get accMatrix store in the accMatrix array
		double[][] AA,CA;							
		AA=calculateAA();							//get the AAMatrix in AA array
		CA=calculateCA(AA);							//get the CAMatrix in CA array
		output(CA);									//print the CA matrix on the screen
	}

}
