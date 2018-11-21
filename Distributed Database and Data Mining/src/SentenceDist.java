import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class SentenceDist {
	public String[] stock;						// stock store the file data by line
	public int[] length;						// the number of the words in each line
	public int firstindex;						//the first index of the first sentence

	/*
	 * This method is use for read data from the file by file name
	 * 
	 * @param fname: the name and address of the input file
	 * @return  return a String array, the ith element is the ith line in the file
	 * 
	 */
	
	private String[] fread(String fname){
		ArrayList<String> data = new ArrayList<String>();				//create a array list to store each line in the file
		try {
			File file = new File(fname);
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
			BufferedReader breader = new BufferedReader(reader);
			String newline;
			newline = breader.readLine();								//read line 
			while(newline != null){										//read until the next line
				data.add(newline);										//add to the array list
				newline = breader.readLine();							//read line
			}
			breader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] result = new String[data.size()];						
		data.toArray(result);											//change array list into String array
		return result;
	}
	
	
	/*
	 * This method is use for calculate the total number of the words in each line.
	 */
	
	private void getlength(){
		length = new int[stock.length]; 
		for(int i = 0; i < stock.length; i++){							//search each line and miner the first line number
			length[i] = stock[i].split(" ").length-1;			
		}
	}
	
	
	/*
	 * This method is use for compare the line a to line b with a number of word difference
	 * In addition, this method will print out the difference or claim they are the same if 
	 * line a and line b's distance is less than 1
	 * 
	 * @param a: the line a
	 * @param b: the line b
	 * @param dif: the number of words that line a more than line b
	 * 
	 */
	
	private void compare(String[] a, String[] b, int ai, int bi, int dif){
		int distance = 0;									//mark the distance between a and b
		int different = -1;									//mark the different index
		switch(dif){
		case 0:												//if they got the same number of total words
			for(int i = 1; i < a.length; i++)				//compare from the first word to the last
				if(!a[i].equals(b[i])){						
					distance++;								//if there is a word is different, distance plus 1
					if(distance == 1) different = i - 1;	//record the different index
					if(distance > 1) break;					//if distance more than 1, step over this sentence
				}
			
			if(distance == 0)								//if a and b are the same then print the result 
				System.out.println("Sentence " + (ai + firstindex) + " and Sentence " 
										+ (bi + firstindex) + " are the same, their distance is 0.");
			
			if(distance == 1) {								//if a and b's distance is 1, print out the result
				System.out.println("Sentence " + (ai + firstindex) +" and Sentence " 
										+ (bi + firstindex) + "'s distance is 1:");
				System.out.println("Substitute '" + a[different + 1] + "' at Sentence "+ (ai + firstindex) + "'s " 
										+ getth(different) + " bit by '" + b[different + 1] + "'.");
			}
			break;											//if the distance is more than 2 then print nothing		
			
		case 1:												//if a have one more word than b
			for(int i = 1; i < b.length; i++){				//compare from the first word to the last
				if(distance == 0 ){							
					if(!a[i].equals(b[i])){
						distance++;							//if there is a word is different, distance plus 1
						different = i - 1;					//record the different index
					}
					if(i == b.length - 1 && a[i].equals(b[i])){
						distance++;							//if except the last word in a, a and b are the same	
						different = i;						// distance plus 1, record the different index
						break;
					}
				}
				if(distance == 1)
					if(!a[i + 1].equals(b[i])){				//if distance is more than 1, step over this sentence
						distance++;
						break;
					}
			}
			if(distance == 1){								//if the distance is 1 then print the result
				System.out.println("Sentence " + (ai + firstindex) + " and Sentence " 
										+ (bi + firstindex) + "'s distance is 1:");
				System.out.println("Delete '"+ a[different + 1] + "' at Sentence " 
										+ (ai + firstindex) + "'s "+ getth(different) +" bit.");
			}
			break;											//if the distance is more than 2 then print nothing
			
		case -1:											//if b have one more word than a
			for(int i = 1; i < a.length; i++){				//compare from the first word to the last
				if(distance == 0){							
					if(!a[i].equals(b[i])){					
						distance++;							//if there is a word is different, distance plus 1
						different = i - 1;					//record the different index
					}
					if(i == a.length - 1 && a[i].equals(b[i])){
						distance++;							//if except the last word in b, a and b are the same
						different = i;						//distance plus 1, record the different index
						break;
					}
				}
				if(distance == 1)
					if(!a[i].equals(b[i + 1])){				//if distance is more than 1, step over this sentence
						distance++;
						break;
					}
			}
			if(distance == 1){								//if the distance is 1 then print the result
				System.out.println("Sentence " + (ai + firstindex) + " and Sentence " 
										+ (bi + firstindex) + "'s distance is 1:");
				System.out.println("Insert '" + b[different +1] + "' before Sentence " 
										+ (ai + firstindex) + "'s " + getth(different) + " bit.");
			}
			break;											//if the distance is more than 2 then print nothing
		}

	}
	
	/*
	 * This method is use for compare the line index to the line after it.
	 * 
	 * @param index: the line that used to compare with others
	 * 
	 */
	
	private void compare(int index){
			
		for(int i = index + 1; i < stock.length; i++) 								//search the line after index
			if(Math.abs(length[index] - length[i]) <= 1){							//if there is more than 1 number of the total words difference, just step over it.
				String[] temp1 = stock[index].split(" ");							//get each word in the line index
				String[] temp2 = stock[i].split(" ");								//get each word in the line i
				if(temp1[1].equals(temp2[1]) || 
						temp1[temp1.length - 1].equals(temp2[temp2.length - 1])){	//if the first and the last word both are not the same, step over it. 
					switch(length[index] - length[i]){								//compare line index with line i
					case 1: compare(temp1, temp2, index, i, 1); break;				//if the number of the total words in line index have one more than line i
					case -1: compare(temp1, temp2, index, i, -1); break;			//if the number of the total words in line index have one less than line i 
					default: compare(temp1, temp2, index, i, 0); break;				//if they are equal
					
					}
 				}
			}
	}
	
	/*
	 * This method is use for get the right order expression.
	 * 
	 * @param n: this is the nth
	 * @return return the right format of the order expression
	 */
	
	private String getth(int n){
		if(n == 1) return n + "st";			//if it is 1 return 1st
		if(n == 2) return n + "nd";			//if it is 2 return 2nd
		if(n == 3) return n + "rd";			//if it is 3 return 3rd
		return n + "th";					//if it is not 1, 2 or 3 return nth
	}


	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fname = args[0];
		SentenceDist sd = new SentenceDist();
		sd.stock = sd.fread(fname); 							//get stock
		sd.firstindex = Integer.parseInt(sd.stock[0].split(" ")[0]);
		sd.getlength();											//get length
		for(int i = 0; i < sd.stock.length; i++)				//compare the ith line to other lines
			sd.compare(i);
	}

}
