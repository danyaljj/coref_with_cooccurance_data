package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import edu.illinois.cs.cogcomp.cooccurancedata.FeatureExtractor;
import edu.illinois.cs.cogcomp.cooccurancedata.FeaturePreprocessor;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class featureExtractorTest {

	public static void main(String[] args){
		method2(); // new 
		//method1(); // old 
	} 
	
	// the is the new one and more efficient 
	public static void method2()
	{ 
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		// feature pre-processor 
		FeaturePreprocessor fp = new FeaturePreprocessor( pr.allInstances_withAntecedentAnnotations, false, true);
		fp.readInstanceVerbs();
		//fp.Process(); 
		
		// the size of the verbs must match the number of the instances 
		System.out.println( "pr.allInstances_withAntecedentAnnotations.size() = " + pr.allInstances_withAntecedentAnnotations.size() ); 
		System.out.println( "fp.antVerbIndex.size() = " + fp.antVerbIndex.size() ); 
		System.out.println( "fp.pronounVerbIndex.size() = " + fp.pronounVerbIndex.size() ); 
		
		assert(pr.allInstances_withAntecedentAnnotations.size() == fp.antVerbIndex.size()); 
		assert(pr.allInstances_withAntecedentAnnotations.size() == fp.pronounVerbIndex.size()); 
		
		int instance_num = 0; 
		FeatureExtractor[] featureExtractors = new FeatureExtractor[pr.allInstances_withAntecedentAnnotations.size()]; 
		for( WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations )
		{ 
			System.out.println("---------------------------"); 
			System.out.println("ins.sentence = " + ins.sentence); 
			FeatureExtractor fe = new FeatureExtractor(); 
			fe.setInstance( ins ); 
			fe.setInstanceNumber( instance_num ); 
			fe.extractTextAnnotation(); 
			fe.setPreprocessor( fp ); 
			fe.setTheVerbIndices(); 
			fe.extractHeadNoun();  
			fe.extractConnective(); 
			fe.updateTables(); 
			featureExtractors[instance_num] = fe; 
			instance_num++; 
		}
		
		System.out.println( "fp.tokenMap.size() = " + fp.tokenMap.size() ); 
		System.out.println( "fp.tokenPairMap1.size() = " + fp.tokenPairMap1.size() ); 
		System.out.println( "fp.tokenPairMap2.size() = " + fp.tokenPairMap2.size() );
		System.out.println( "fp.pairwiseDependentMap.size() = " + fp.pairwiseDependentMap.size() );
		System.out.println( "featureExtractors[0].connectives.size() = " + featureExtractors[0].connectives.size() );
		System.out.println( "---------------------------- "); 
		int num1 = fp.tokenMap.size(); 
		System.out.println( "Size of the unigram features " + num1 );
		int num2 = fp.tokenPairMap1.size() * fp.tokenPairMap1.size() ; 
		System.out.println( "Size of the bigram features " +  num2 );
		int num3 = fp.tokenPairMap1.size() * fp.tokenPairMap1.size() * featureExtractors[0].connectives.size(); 
		System.out.println( "Size of the triple features " + num3 );
		int num4 = fp.pairwiseDependentMap.size() * fp.pairwiseDependentMap.size(); 
		System.out.println( "Size of the pairwise dependent features " + num4 );
		
		/*
		instance_num = 0; 
		for( WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations )
		{
			double[] featureVector = new double[0]; 
			FeatureExtractor fe = featureExtractors[instance_num]; 
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
		}
		 */
	} 
	
	// this produces a lot of redundant feature vectors 
	public static void method1() 
	{ 	
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		// feature pre-processor 
		FeaturePreprocessor fp = new FeaturePreprocessor( pr.allInstances_withAntecedentAnnotations, false, true);
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
