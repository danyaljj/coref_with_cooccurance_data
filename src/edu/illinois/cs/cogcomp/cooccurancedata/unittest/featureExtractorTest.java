package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.illinois.cs.cogcomp.cooccurancedata.FeatureExtractor;
import edu.illinois.cs.cogcomp.cooccurancedata.FeaturePreprocessor;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.IOManager;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.NarrativeSchemaReader;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class featureExtractorTest {

	public static void main(String[] args) throws Exception{
		method2(); // new 
		//method1(); // old 
	} 
	
	// the is the new one and more efficient 
	public static void method2() throws Exception
	{ 
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		NarrativeSchemaReader nsreader=new NarrativeSchemaReader();
	    ArrayList<NarrativeSchemaInstance> allInstances = nsreader.readSchema(6); // 6,8,10,12
		
		// feature pre-processor 
		FeaturePreprocessor fp = new FeaturePreprocessor( pr.allInstances_withAntecedentAnnotations, true, true);
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
			//System.out.println("---------------------------"); 
			//System.out.println("ins.sentence = " + ins.sentence); 
			FeatureExtractor fe = new FeatureExtractor(); 
			fe.setInstance( ins ); 
			fe.setInstanceNumber( instance_num ); 
			fe.extractTextAnnotation(); 
			fe.setPreprocessor( fp ); 
			fe.setTheVerbIndices(); 
			fe.extractHeadNoun();  
			fe.extractConnective(); 
			fe.updateTables(); 
			fe.setNarrativeSchema(allInstances);
			fe.extractNarrativeSchema(1);
			fe.extractNarrativeSchema(2);
			featureExtractors[instance_num] = fe; 
			instance_num++; 
		}
		
		//System.out.println( "fp.tokenMap.size() = " + fp.tokenMap.size() ); 
		//System.out.println( "fp.tokenPairMap.size() = " + fp.tokenPairMap.size() ); 
		//System.out.println( "fp.pairwiseDependentMap.size() = " + fp.pairwiseDependentMap.size() );
		//System.out.println( "featureExtractors[0].connectives.size() = " + featureExtractors[0].connectives.size() );
		//System.out.println( "---------------------------- "); 
		//System.out.println( "SUM " + (fp.tokenMap.size() + fp.tokenPairMap.size() 
		//		+ fp.pairwiseDependentMap.size() + featureExtractors[0].connectives.size()) ); 
	
		BufferedWriter bw1=IOManager.openWriter("train.txt");
		BufferedWriter bw2=IOManager.openWriter("test.txt");
		int qid1=2, qid2=2;
		
		instance_num = 0;
		for( WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations )
		{
			double[] featureVector = null; 
			FeatureExtractor fe = featureExtractors[instance_num]; 
			try {
				featureVector = fe.Extract2();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			instance_num++;
			
			if (ins.test_or_train==1) {
				getMat(fe.getLabel(),qid1,featureVector,bw1);
				qid1++;
			}
			if (ins.test_or_train==0) {
				getMat(fe.getLabel(),qid2,featureVector,bw2);
				qid2++;
			}
			//System.out.println( "featureVector.length = " + featureVector.length ); 
		}
		
		bw1.close();bw2.close();
	} 
	
	private static void getMat(int label, int qid, double[] vec, BufferedWriter bw) throws Exception {
		if (label==1) bw.write("2");
		else bw.write("1");
		bw.write(" qid:"+qid/2);
		for (int i=0;i<vec.length;i++) {
			if (vec[i]!=0) {
				int index=i+1;
				double value=vec[i];
				bw.write(" "+index+":"+value);
			}
		}
		bw.write("\n");
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
