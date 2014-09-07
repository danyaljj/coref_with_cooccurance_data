package edu.illinois.cs.cogcomp.cooccurancedata.readers;

import java.io.BufferedReader;
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
import edu.illinois.cs.cogcomp.cooccurancedata.printing.printWinogradInstance;
import edu.illinois.cs.cogcomp.edison.data.curator.CuratorClient;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.ViewNames;
import edu.illinois.cs.cogcomp.thrift.base.AnnotationFailedException;
import edu.illinois.cs.cogcomp.thrift.base.ServiceUnavailableException;

public class PronounDisambiguationDataReader {

	public static List<WinogradCorefInstance> allInstances = new ArrayList<WinogradCorefInstance>(); 
	static String train_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/train.c.txt"; 
	static String test_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/test.c.txt";
	static String cache_file = "/shared/shelley/khashab2/CorporaAndDumps/Altaf_Ng_2012_Pronoun_Resolution/cached_data.bin";

	// testing the reader 
	public static void main(String[] argc) { 
		try {
			Read_all_instances();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		PostProcessData();
		serializeData(); 
	}

	public static void Read_from_file(String file) throws FileNotFoundException, IOException { 

		int lineIter = 0; 
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
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
		int iter = 0; 
		for( WinogradCorefInstance ins : allInstances ) { 

			iter++; 

			//if( iter > 5 )
			//	break; 

			System.out.println("processing the paragraph = " + ins.sentence);
			// process here: 
			// tokenize line by line : 
			String corpus = "Winograd schemas data ";
			String textId = "textId"; 
			String curatorHost = "trollope.cs.illinois.edu";
			int curatorPort = 9010;
			CuratorClient client = new CuratorClient(curatorHost, curatorPort);
			boolean forceUpdate = false;

			TextAnnotation ta;
			try {
				ta = client.getTextAnnotation(corpus, textId, ins.sentence, forceUpdate);

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
				ins.textAnnotation = EdisonSerializationHelper.serializeToBytes(ta);
			} catch (ServiceUnavailableException | AnnotationFailedException
					| TException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// saving on disk 
	public static void serializeData() { 
		try
		{
			System.out.printf("Starting serialization! "); 
			FileOutputStream fileOut = new FileOutputStream(cache_file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(allInstances);
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in " + cache_file);
		} catch(IOException i)
		{
			i.printStackTrace();
		}
	}

	/// reading from disk 
	public static void deserializeData() { 
		try
		{
			System.out.printf("Starting deserialization! ");
			FileInputStream fileIn = new FileInputStream(cache_file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			allInstances = (List<WinogradCorefInstance>) in.readObject();
			System.out.printf("Done reading the data from " + cache_file);
			in.close();
			fileIn.close();
		} catch(IOException | ClassNotFoundException i)
		{
			i.printStackTrace();
			return;
		}
	}
}
