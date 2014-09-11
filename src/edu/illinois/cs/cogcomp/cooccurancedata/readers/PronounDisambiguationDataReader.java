package edu.illinois.cs.cogcomp.cooccurancedata.readers;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.thrift.TException;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.printing.printWinogradInstance;
import edu.illinois.cs.cogcomp.edison.data.curator.CuratorClient;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.ViewNames;
import edu.illinois.cs.cogcomp.thrift.base.AnnotationFailedException;
import edu.illinois.cs.cogcomp.thrift.base.ServiceUnavailableException;

public class PronounDisambiguationDataReader {

	public static List<WinogradCorefInstance> allInstances = new ArrayList<WinogradCorefInstance>(); 
	public static List<WinogradCorefInstance2> allInstances_withAntecedentAnnotations = new ArrayList<WinogradCorefInstance2>(); 
	static String train_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/train.c.txt"; 
	static String test_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/test.c.txt";
	static String cache_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/cached_data.bin";
	static String cache_file_withSRL = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/cached_data_with_SRL.bin";
	static String cache_file_withSRL_withAnts = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/cached_data_with_SRL_withAntecedantAnnotation.bin";

	// testing the reader 
	public static void main(String[] argc) { 
		/*try {
			Read_all_instances();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		PostProcessData();
		serializeData(); 
		*/
		/*
		deserializeData();
		copyAnnotation();
		PostProcessData_antecedents();
		serializeData2();
		*/
				
		/*deserializeData2();
		findTokenOffsetsForArguments(); 
		serializeData2();
		*/
		
		deserializeData2();
		for( WinogradCorefInstance2 ins : allInstances_withAntecedentAnnotations ) { 
			System.out.println("ins.antecedent1_token_start = " + ins.antecedent1_token_start); 
			System.out.println("ins.antecedent1_token_end = " + ins.antecedent1_token_end); 
			 
			System.out.println("ins.antecedent1_token_start = " + ins.antecedent2_token_start); 
			System.out.println("ins.antecedent1_token_end = " + ins.antecedent2_token_end); 
			
			System.out.println("ins.pronoun_word_start = " + ins.pronoun_word_start); 
			System.out.println("ins.pronoun_word_end = " + ins.pronoun_word_end); 
		}
	}
	
	private static void findTokenOffsetsForArguments() {

		int iter = 0; 
		for( WinogradCorefInstance2 ins : allInstances_withAntecedentAnnotations ) {			
			TextAnnotation ta = null; 
			try {
				ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			String[] tok = ta.getTokens(); 
			
			int antTokOff1 = -1; 
			int antTokOff2 = -1; 
			int pronounOff = -1; 
			
			int antTokOff1_end = -1; 
			int antTokOff2_end = -1;
			int pronounOff_end = -1;
			
			TextAnnotation ta_arg1 = null; 
			try {
				ta_arg1 = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation_ant1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			String[] ant1_toks = ta_arg1.getTokens();
			
			TextAnnotation ta_arg2 = null; 
			try {
				ta_arg2 = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation_ant2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			String[] ant2_toks = ta_arg2.getTokens();

			TextAnnotation ta_pronoun = null; 
			try {
				ta_pronoun = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation_pronoun);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			String[] pronoun_toks = ta_pronoun.getTokens();
			
			int j = 0;
			
//			int kk = 0;
//			for(String str: tok){ 
//				System.out.println("kk = " + kk +  " /  str = " + str );
//				kk++; 
//			} 

//			kk = 0; 
//			for(String str_ment_tok : ant1_toks) {
//				System.out.println("kk = " + kk +  " / str_ment_tok = " + str_ment_tok );
//				kk++; 
//			}

//			kk = 0; 
//			for(String str_ment_tok : ant2_toks) {
//				System.out.println("kk = " + kk +  " / str_ment_tok = " + str_ment_tok );
//				kk++; 
//			}
			
//			int kk = 0; 
//			for(String str_ment_tok : pronoun_toks) {
//				System.out.println("kk = " + kk +  " / str_ment_tok = " + str_ment_tok );
//				kk++; 
//			}

			for(String str: tok){ 
				int k = 0; 
				for(String str_ment_tok : ant1_toks) { 
					
					if( str_ment_tok.equals("Inc") && tok[j+k].equals("Inc.") && k == 2 )
					{ 
						antTokOff1 = j; 
						antTokOff1_end = j + ant1_toks.length - 1; 
					}
						
					if( ! str_ment_tok.toLowerCase().equals( tok[j+k].toLowerCase() ) )
						break; 
					//System.out.println("---->  k " + k + "  j = " + j); 
					if( k >= ant1_toks.length - 1 ) { 
						antTokOff1 = j;
						antTokOff1_end = j + ant1_toks.length;
					}
					k++; 
				}
				
				k = 0; 
				for(String str_ment_tok : ant2_toks) { 
					if( str_ment_tok.equals("Inc") && tok[j+k].equals("Inc.") && k == 2 )
					{ 
						antTokOff2 = j; 
						antTokOff2_end = j + ant2_toks.length - 1;
					}
					if( ! str_ment_tok.toLowerCase().equals( tok[j+k].toLowerCase() ) )
						break; 
					//System.out.println("---->  k " + k + "  j = " + j); 
					if( k >= ant2_toks.length - 1 ) { 
						antTokOff2 = j;
						antTokOff2_end = j + ant2_toks.length;
					}
					k++; 
				}
				
				k = 0; 
				for(String str_ment_tok : pronoun_toks) { 
					if( ! str_ment_tok.toLowerCase().equals( tok[j+k].toLowerCase() ) )
						break; 
					//System.out.println("---->  k " + k + "  j = " + j); 
					if( k >= pronoun_toks.length - 1 ) { 
						pronounOff = j;
						pronounOff_end = j + pronoun_toks.length; 
					}
					k++; 
				}
				
//				if( str.equals( ins.pronoun ) )
//					pronounOff = j; 
				j++; 
			}
			System.out.println("Sentence = " + ins.sentence );
			System.out.println("Ant1 = " + ins.antecedent1 + " Tok = " + antTokOff1 ); 
			System.out.println("Ant2 = " + ins.antecedent2 + " Tok = " + antTokOff2 ); 
			System.out.println("Pronoun = " + ins.pronoun + " Tok = " + pronounOff ); 
			iter++; 
			//if( iter > 100 )
			//	break;
			if( antTokOff1 == -1)
				break;
			if( antTokOff2 == -1)
				break;
			if( pronounOff == -1)
				break;
			
			ins.antecedent1_token_start = antTokOff1; 
			ins.antecedent1_token_end = antTokOff1_end; 

			ins.antecedent2_token_start = antTokOff2; 
			ins.antecedent2_token_end = antTokOff2_end; 

			ins.pronoun_word_start = pronounOff; 
			ins.pronoun_word_end = pronounOff_end; 
		}
	}

	private static void copyAnnotation() {
		for( WinogradCorefInstance ins :  allInstances )
		{ 
			WinogradCorefInstance2 ins2 = new WinogradCorefInstance2(); 
			ins2.sentence = ins.sentence; 
			ins2.pronoun = ins.pronoun; 
			ins2.antecedent1 = ins.antecedent1; 
			ins2.antecedent2 = ins.antecedent2; 
			ins2.correct_antecedent = ins.correct_antecedent; 
			ins2.test_or_train = ins.test_or_train; 
			ins2.pronoun_char_start = ins.pronoun_char_offset; 
			ins2.antecedent1_char_start = ins.antecedent1_char_offset; 
			ins2.antecedent2_char_start = ins.antecedent2_char_offset; 
			ins2.textAnnotation = ins.textAnnotation; 
			ins2.textAnnotation_string = ins.textAnnotation_string; 
			
			allInstances_withAntecedentAnnotations.add(ins2); 
		}
	}

	public static void Read_from_file(String file) throws FileNotFoundException, IOException { 

		int lineIter = 0;
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = ""; 
		
		WinogradCorefInstance ins = new WinogradCorefInstance();
		while (line != null) {
			line = br.readLine();
			//System.out.println("---Line : " + line);

			if( lineIter % 5 == 0 )	{ 
				ins.sentence = line; 
				//System.out.println("Sentence");
			}
			else if( lineIter % 5 == 1 )	{ 
				ins.pronoun = line; 
				//System.out.println("Pronoun");
			}
			else if( lineIter % 5 == 2 )	{ 
				int ind = line.indexOf(','); 
				ins.antecedent1 = line.substring(0, ind); 
				ins.antecedent2 = line.substring(ind+1);
				//System.out.println("Ant1");
			}
			else if( lineIter % 5 == 3 )	{ 
				ins.correct_antecedent = line; 
				//System.out.println("Ant2");
			}
			else {
				if( file.toLowerCase().contains("train") )
					ins.test_or_train = 1; 
				else 
					ins.test_or_train = 0;
				//printWinogradInstance.print(ins);
				allInstances.add(ins);
				ins = new WinogradCorefInstance();
				//System.out.println("Adding it! ");
			}
			lineIter++;
		}
	}

	public static void Read_all_instances() throws FileNotFoundException, IOException { 
		Read_from_file(train_file); 
		Read_from_file(test_file); 
	}

	public static void PostProcessData() {
		System.out.println("Start to post process! "); 
		for( WinogradCorefInstance ins : allInstances ) { 
			ins.pronoun_char_offset = ins.sentence.indexOf(ins.pronoun); 
			ins.antecedent1_char_offset = ins.sentence.indexOf(ins.antecedent1); 
			ins.antecedent2_char_offset = ins.sentence.indexOf(ins.antecedent2);

			if( ins.pronoun_char_offset == -1 ) { 
				System.out.println("One of the elements not found in the text!--> Pronoun  "); 
				printWinogradInstance.print(ins);
			}

			if( ins.antecedent1_char_offset == -1  ) { 
				System.out.println("One of the elements not found in the text!--> Ant1 "); 
				printWinogradInstance.print(ins);
			}

			if( ins.antecedent2_char_offset == -1 ) { 
				System.out.println("One of the elements not found in the text!--> Ant2 "); 
				printWinogradInstance.print(ins);
			}
		}
		
		// adding curator annotation 
		System.out.println("adding the curator annotation ");		
		
		// process here: 
		// tokenize line by line : 
		String corpus = "Winograd schemas data ";
		String textId = "textId"; 
		String curatorHost = "trollope.cs.illinois.edu";
		int curatorPort = 9010;
		boolean forceUpdate = false;
		
		int iter = 0; 
		for( WinogradCorefInstance ins : allInstances ) {  

			System.out.println("processing the paragraph = " + ins.sentence);
			
			//if( iter > 5 )
			//	break; 
			CuratorClient client = new CuratorClient(curatorHost, curatorPort);
			TextAnnotation ta;
			try {
				ta = client.getTextAnnotation(corpus, textId, ins.sentence, forceUpdate);
				System.out.println("TA = " + ta); 
				
				if( !ta.hasView("POS") ) { 
					System.out.println("POS"); 
					// System.out.println(ta.getTokenizedText());
					client.addPOSView(ta, forceUpdate);
					//System.out.println(ta.getView(ViewNames.POS));
				}

				if( !ta.hasView(ViewNames.NER) ) {
					System.out.println("NER"); 
					// Add the named entity view and print it.
					client.addNamedEntityView(ta, forceUpdate);
					// System.out.println(ta.getView(ViewNames.NER));
				}

				if( !ta.hasView(ViewNames.SRL) ) {
					System.out.println("SRL = ");
					client.addPredicateArgumentView(ta, forceUpdate, "srl", ViewNames.SRL);
					// System.out.println(ta.getView(ViewNames.SRL));
				}
				
				if( !ta.hasView(ViewNames.NOM) ) {
					System.out.println("Nom = ");
					client.addPredicateArgumentView(ta, forceUpdate, "nom", ViewNames.NOM);
					// System.out.println(ta.getView(ViewNames.NOM));
				}

				if( !ta.hasView("PREP") ) {
					// There is no corresponding string for prep in ViewNames
					System.out.println("Prep = ");
					client.addPredicateArgumentView(ta, forceUpdate, "prep", "PREP");
					// System.out.println(ta.getView("PREP"));
				}
				/*
				Set<String> view = ta.getAvailableViews(); 
				for( String viewi : view ) 
					System.out.println("Viewi = " + viewi);
				 */			
				//ins.textAnnotation = EdisonSerializationHelper.serializeToBytes(ta);
				allInstances.get(iter).textAnnotation = EdisonSerializationHelper.serializeToBytes(ta);
				//System.out.println("allInstances.get(iter).textAnnotation = " + allInstances.get(iter).textAnnotation);
				allInstances.get(iter).textAnnotation_string = EdisonSerializationHelper.serializeToJson(ta);
				//System.out.println("allInstances.get(iter).textAnnotation_string = " + allInstances.get(iter).textAnnotation_string);
				
				//System.out.println(ins.textAnnotation); 
//				if( iter > 10 )
//					break; 
			} catch (ServiceUnavailableException e) {
				e.printStackTrace();
			} catch (AnnotationFailedException e) {
				e.printStackTrace();
			} catch (TException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			iter++;
		}
	}

	public static void PostProcessData_antecedents() {		
		// adding curator annotation 
		System.out.println("adding the curator annotation ");		
		
		// process here: 
		// tokenize line by line : 
		String corpus = "Winograd schemas data ";
		String textId = "textId"; 
		String curatorHost = "trollope.cs.illinois.edu";
		int curatorPort = 9010;
		boolean forceUpdate = false;
		
		int iter = 0; 
		for( WinogradCorefInstance2 ins : allInstances_withAntecedentAnnotations ) {  
			
			CuratorClient client = new CuratorClient(curatorHost, curatorPort);
			TextAnnotation ta;
			try {
				ta = client.getTextAnnotation(corpus, textId, ins.antecedent1, forceUpdate);
				System.out.println("TA = " + ta); 
				
				if( !ta.hasView("POS") ) { 
					System.out.println("POS"); 
					// System.out.println(ta.getTokenizedText());
					client.addPOSView(ta, forceUpdate);
					//System.out.println(ta.getView(ViewNames.POS));
				}

				if( !ta.hasView(ViewNames.NER) ) {
					System.out.println("NER"); 
					// Add the named entity view and print it.
					client.addNamedEntityView(ta, forceUpdate);
					// System.out.println(ta.getView(ViewNames.NER));
				}
				allInstances_withAntecedentAnnotations.get(iter).textAnnotation_ant1 = EdisonSerializationHelper.serializeToBytes(ta);
				allInstances_withAntecedentAnnotations.get(iter).textAnnotation_string_ant1 = EdisonSerializationHelper.serializeToJson(ta);
				
			} catch (ServiceUnavailableException e) {
				e.printStackTrace();
			} catch (AnnotationFailedException e) {
				e.printStackTrace();
			} catch (TException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			iter++;
		}
		
		iter = 0; 
		for( WinogradCorefInstance2 ins : allInstances_withAntecedentAnnotations ) {  
			
			CuratorClient client = new CuratorClient(curatorHost, curatorPort);
			TextAnnotation ta;
			try {
				ta = client.getTextAnnotation(corpus, textId, ins.antecedent2, forceUpdate);
				System.out.println("TA = " + ta); 
				
				if( !ta.hasView("POS") ) { 
					System.out.println("POS"); 
					// System.out.println(ta.getTokenizedText());
					client.addPOSView(ta, forceUpdate);
					//System.out.println(ta.getView(ViewNames.POS));
				}

				if( !ta.hasView(ViewNames.NER) ) {
					System.out.println("NER"); 
					// Add the named entity view and print it.
					client.addNamedEntityView(ta, forceUpdate);
					// System.out.println(ta.getView(ViewNames.NER));
				}
				allInstances_withAntecedentAnnotations.get(iter).textAnnotation_ant2 = EdisonSerializationHelper.serializeToBytes(ta);
				allInstances_withAntecedentAnnotations.get(iter).textAnnotation_string_ant2 = EdisonSerializationHelper.serializeToJson(ta);
				
			} catch (ServiceUnavailableException e) {
				e.printStackTrace();
			} catch (AnnotationFailedException e) {
				e.printStackTrace();
			} catch (TException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			iter++;
		}
		
		iter = 0; 
		for( WinogradCorefInstance2 ins : allInstances_withAntecedentAnnotations ) {  
			
			CuratorClient client = new CuratorClient(curatorHost, curatorPort);
			TextAnnotation ta;
			try {
				ta = client.getTextAnnotation(corpus, textId, ins.pronoun, forceUpdate);
				System.out.println("TA = " + ta); 
				
				if( !ta.hasView("POS") ) { 
					System.out.println("POS"); 
					// System.out.println(ta.getTokenizedText());
					client.addPOSView(ta, forceUpdate);
					//System.out.println(ta.getView(ViewNames.POS));
				}

				if( !ta.hasView(ViewNames.NER) ) {
					System.out.println("NER"); 
					// Add the named entity view and print it.
					client.addNamedEntityView(ta, forceUpdate);
					// System.out.println(ta.getView(ViewNames.NER));
				}
				allInstances_withAntecedentAnnotations.get(iter).textAnnotation_pronoun = EdisonSerializationHelper.serializeToBytes(ta);
				allInstances_withAntecedentAnnotations.get(iter).textAnnotation_string_pronoun = EdisonSerializationHelper.serializeToJson(ta);
				
			} catch (ServiceUnavailableException e) {
				e.printStackTrace();
			} catch (AnnotationFailedException e) {
				e.printStackTrace();
			} catch (TException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			iter++;
		}
	}
	
	// saving on disk 
	public static void serializeData() { 
		try
		{
			System.out.println("Starting serialization! "); 
			FileOutputStream fileOut = new FileOutputStream(cache_file_withSRL);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(allInstances);
			out.close();
			fileOut.close();
			// System.out.printf("Serialized data is saved in " + cache_file_withSRL);
			System.out.println("Serialized data is saved in " + cache_file_withSRL);
		} catch(IOException i)
		{
			i.printStackTrace();
		}
	}

	// saving on disk 
	public static void serializeData2() { 
		try
		{
			System.out.println("Starting serialization! "); 
			FileOutputStream fileOut = new FileOutputStream(cache_file_withSRL_withAnts);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(allInstances_withAntecedentAnnotations);
			out.close();
			fileOut.close();
			// System.out.printf("Serialized data is saved in " + cache_file_withSRL);
			System.out.println("Serialized data is saved in " + cache_file_withSRL_withAnts);
		} catch(IOException i)
		{
			i.printStackTrace();
		}
	}
	
	// reading from disk 
	public static void deserializeData() { 
		try
		{
			System.out.println("Starting deserialization! ");
			//FileInputStream fileIn = new FileInputStream(cache_file_withSRL);
			FileInputStream fileIn = new FileInputStream(cache_file_withSRL);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			allInstances = (List<WinogradCorefInstance>) in.readObject();
			System.out.println("Done reading the data from " + cache_file_withSRL);
			in.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void deserializeData2() { 
		try
		{
			System.out.println("Starting deserialization! ");
			//FileInputStream fileIn = new FileInputStream(cache_file_withSRL);
			FileInputStream fileIn = new FileInputStream(cache_file_withSRL_withAnts);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			allInstances_withAntecedentAnnotations = (List<WinogradCorefInstance2>) in.readObject();
			System.out.println("Done reading the data from " + cache_file_withSRL_withAnts);
			in.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
