package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import java.util.ArrayList;

import edu.illinois.cs.cogcomp.cooccurancedata.FeatureExtractor;
import edu.illinois.cs.cogcomp.cooccurancedata.FeaturePreprocessor;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.NarrativeSchemaReader;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class ConnectiveExtractionTest {

	public static void main(String[] args) throws Exception { 
		
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		NarrativeSchemaReader nsreader=new NarrativeSchemaReader();
		ArrayList<NarrativeSchemaInstance> allInstances = nsreader.readSchema(6); // 6,8,10,12
		
		int instance_num = 0; 
		FeatureExtractor fe = new FeatureExtractor(); 
		fe.setNarrativeSchema(allInstances);
		FeaturePreprocessor fp = new FeaturePreprocessor( pr.allInstances_withAntecedentAnnotations, false, true);
		fp.Process();
		fe.setPreprocessor( fp );
		for( WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations ) { 
			fe.setInstance(ins); 
			fe.setInstanceNumber(instance_num);
			fe.setTheVerbIndices();
			fe.extractNarrativeSchema(1);
			fe.extractNarrativeSchema(2);
			instance_num++; 
		}
	}
}
