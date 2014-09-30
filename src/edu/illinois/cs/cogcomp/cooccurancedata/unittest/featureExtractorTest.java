package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import edu.illinois.cs.cogcomp.cooccurancedata.FeatureExtractor;
import edu.illinois.cs.cogcomp.cooccurancedata.FeaturePreprocessor;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class featureExtractorTest {

	public static void main(String[] args){ 
		
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
		
		int instance_num = 0; 
		for( WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations )
		{ 
			System.out.println("---------------------------"); 
			System.out.println("ins.sentence = " + ins.sentence); 
			FeatureExtractor fe = new FeatureExtractor(); 
			fe.setInstance( ins ); 
			fe.setInstanceNumber( instance_num ); 
			fe.setPreprocessor( fp ); 
			fe.setTheVerbIndices(); 
			fe.extractHeadNoun();  
			fe.extractConnective(); 
			double[] featureVector = new double[0]; 
			try {
				featureVector = fe.Extract();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println( "featureVector.length = " + featureVector.length ); 
			
			// try just a few 
			if( instance_num > 10 )
				break; 
			
			instance_num++; 
		}
	} 
}
