package edu.illinois.cs.cogcomp.cooccurancedata;

import java.io.IOException;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class TMPClass_forTestingFeatureExtractor {

	public static void main(String[] argc) {
		// read data
		PronounDisambiguationDataReader rd = new PronounDisambiguationDataReader(); 
		try {
			rd.Read_all_instances();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		// extract the verbs 
		for( WinogradCorefInstance ins : rd.allInstances ) { 
			FeatureExtractor fe = new FeatureExtractor( ins ); 
			fe.extractVerbs();
		}
	}	
}
