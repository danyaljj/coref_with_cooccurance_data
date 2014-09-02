package edu.illinois.cs.cogcomp.cooccurancedata.readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;

public class PronounDisambiguationDataReader {
	
	static List<WinogradCorefInstance> allInstances = new ArrayList<WinogradCorefInstance>(); 
	static String train_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/train.c.txt"; 
	static String test_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/test.c.txt";
	
	// testing the reader 
	public static void main(String[] argc) { 
		try {
			Read_all_instances();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		PostProcessData();
		
	}
	
	public static void Read_from_file(String file) throws FileNotFoundException, IOException { 
		
		int lineIter = 0; 
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
	        String line = br.readLine();
	         
	        WinogradCorefInstance ins = new WinogradCorefInstance();
	        while (line != null) {
	        	//System.out.println("---Line : " + line);
	        	line = br.readLine();
	             
	        	if( lineIter % 5 == 0 )	{ 
	        		ins = new WinogradCorefInstance();
	            	ins.sentence = line; 
	            	//System.out.println("Sentence");
	            }
	            else if( lineIter % 5 == 1 )	{ 
	            	ins.pronoun = line; 
	            	//System.out.println("Pronun");
	            }
	            else if( lineIter % 5 == 2 )	{ 
	            	ins.antecedent1 = line; 
	            	//System.out.println("Ant1");
	            }
	            else if( lineIter % 5 == 3 )	{ 
	            	ins.antecedent2 = line; 
	            	//System.out.println("Ant2");
	            }
	            else {
	            	if( file.toLowerCase().contains("train") )
	            		ins.test_or_train = 1; 
	            	else 
	            		ins.test_or_train = 0;
	            	
	            	allInstances.add(ins);
	            	//System.out.println("Adding it! ");
	            }
	        	lineIter++;
	        }
	    }
	}

	public static void Read_all_instances() throws FileNotFoundException, IOException { 
		Read_from_file(train_file); 
		Read_from_file(test_file); 
	}
	
	public static void PostProcessData() { 
		for( WinogradCorefInstance ins : allInstances ) { 
			ins.pronoun_char_offset = ins.sentence.indexOf(ins.pronoun); 
			ins.antecedent1_char_offset = ins.sentence.indexOf(ins.antecedent1); 
			ins.antecedent2_char_offset = ins.sentence.indexOf(ins.antecedent2);
			
			if( ins.pronoun_char_offset == -1 || ins.antecedent1_char_offset == -1 || ins.antecedent2_char_offset == -1 ) { 
				System.out.println("One of the elements not found in the text! "); 
			}
		}
	}
	
	public static void serializeData() { 
		
	}
	
	public static void deserializeData() { 
		
	}
}
