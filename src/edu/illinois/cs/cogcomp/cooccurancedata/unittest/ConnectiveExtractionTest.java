package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import edu.illinois.cs.cogcomp.cooccurancedata.FeatureExtractor;
import edu.illinois.cs.cogcomp.cooccurancedata.FeaturePreprocessor;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class ConnectiveExtractionTest {

	public static void main(String[] args) { 
		
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		int instance_num = 0; 
		for( WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations )
		{ 
			FeatureExtractor fe = new FeatureExtractor(); 
			fe.setInstance( ins ); 
			fe.setInstanceNumber( instance_num ); 
			fe.extractHeadNoun(); 
			fe.extractConnective(); 

			System.out.println( "connective_word_end_word_offset = " + fe.connective_word_end_word_offset ); 
			System.out.println( "connective_word_start_word_offset = " + fe.connective_word_start_word_offset ); // Daniel; in the current version this is always -1 
			
			instance_num++; 
		}
	}
}
