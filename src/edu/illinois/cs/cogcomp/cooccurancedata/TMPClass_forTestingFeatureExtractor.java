package edu.illinois.cs.cogcomp.cooccurancedata;

import java.io.IOException;
import java.util.ArrayList;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.NarrativeSchemaReader;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class TMPClass_forTestingFeatureExtractor {

	public static void main(String[] argc) throws Exception {
		// read data
		PronounDisambiguationDataReader rd = new PronounDisambiguationDataReader(); 
		rd.deserializeData2();
		
		NarrativeSchemaReader nsreader=new NarrativeSchemaReader();
		ArrayList<NarrativeSchemaInstance> allInstances = nsreader.readSchema(6); // 6,8,10,12
		
		// extract the connectives & head nouns & NarrativeSchema
		FeatureExtractor fe = new FeatureExtractor();
		fe.setNarrativeSchema(allInstances);
		for (int i=0;i<rd.allInstances_withAntecedentAnnotations.size();i++) {
			fe.setInstance(rd.allInstances_withAntecedentAnnotations.get(i));
			fe.extractConnective();
			fe.extractHeadNoun(); // !!! Do we need lemmatizer for normalization
			fe.extractNarrativeSchema(1);
			fe.extractNarrativeSchema(2);
		}
		
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
