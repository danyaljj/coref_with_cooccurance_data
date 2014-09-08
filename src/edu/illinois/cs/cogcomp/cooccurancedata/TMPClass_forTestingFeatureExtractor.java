package edu.illinois.cs.cogcomp.cooccurancedata;

import java.io.IOException;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class TMPClass_forTestingFeatureExtractor {

	public static void main(String[] argc) {
		// read data
		PronounDisambiguationDataReader rd = new PronounDisambiguationDataReader(); 
		rd.deserializeData();
		
		// extract the verbs 
		FeatureExtractor fe = new FeatureExtractor( rd.allInstances.get(0) );
		
		fe.getVerbGivenMention(fe.ins.antecedent1);
		
		fe.getVerbGivenMention(fe.ins.antecedent2);
		
		fe.getVerbGivenMention(fe.ins.pronoun);
		
		
		/*try {
			fe.extractVerbs();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//fe = new FeatureExtractor( rd.allInstances.get(1) );
		//fe.extractVerbs();
		
		/*for( WinogradCorefInstance ins : rd.allInstances ) { 
			FeatureExtractor fe = new FeatureExtractor( ins ); 
			fe.extractVerbs();
		}*/
	}
}
