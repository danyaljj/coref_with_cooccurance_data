package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import edu.illinois.cs.cogcomp.cooccurancedata.FeaturePreprocessor;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;

public class VerbExtractorTester {

	public static void main(String[] args){ 
		
		// reading vincent's data 
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		// feature pre-processor 
		FeaturePreprocessor fp = new FeaturePreprocessor( pr.allInstances_withAntecedentAnnotations );
		fp.Process(); 
		
		// the size of the verbs must match the number of the instances 
		System.out.println( "pr.allInstances_withAntecedentAnnotations.size() = " + pr.allInstances_withAntecedentAnnotations.size() ); 
		System.out.println( "fp.antVerbIndex.size() = " + fp.antVerbIndex.size() ); 
		System.out.println( "fp.pronounVerbIndex.size() = " + fp.pronounVerbIndex.size() ); 
		
		assert(pr.allInstances_withAntecedentAnnotations.size() == fp.antVerbIndex.size()); 
		assert(pr.allInstances_withAntecedentAnnotations.size() == fp.pronounVerbIndex.size()); 
		
		// testing the verbs 
		for( int i = 0; i < 10; i ++ ) { 
			System.out.println("Sentence( " + i + ") = " + pr.allInstances_withAntecedentAnnotations.get(i).sentence ); 
			TextAnnotation ta = null; 
			try {
				ta = EdisonSerializationHelper.deserializeFromBytes(pr.allInstances_withAntecedentAnnotations.get(i).textAnnotation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] toks = ta.getTokens(); 
			
			Pair<Integer, Integer> ant_verb_index = fp.getVerbTokenIndexGivenInstanceIndex_antecedant(i); 
			String verb = ""; 
			for( int j = ant_verb_index.getFirst(); j < ant_verb_index.getSecond(); j++ )
				verb += " " + toks[j]; 
			System.out.println("Ant Verb = " +  verb );
			
			Pair<Integer, Integer> pro_verb_index = fp.getVerbTokenIndexGivenInstanceIndex_pronoun(i);
			verb = ""; 
			for( int j = pro_verb_index.getFirst(); j < pro_verb_index.getSecond(); j++ )
				verb += " " + toks[j]; 
			System.out.println("Pronoun Verb = " +  verb );
		
			System.out.println("Verb = " + fp.getVerbTokenIndexGivenInstanceIndex_antecedant(i) );
		}
		
		// size of all the vocab in the hashmap: 
		System.out.println("Size of the tokens = " + fp.tokenMap.size()); 
	}
}
