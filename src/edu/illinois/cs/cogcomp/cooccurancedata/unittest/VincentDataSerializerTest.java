package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.didion.jwnl.JWNLException;
import edu.illinois.cs.cogcomp.cooccurancedata.FeaturePreprocessor;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;
import edu.illinois.cs.cogcomp.edison.data.curator.CuratorClient;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.View;
import edu.illinois.cs.cogcomp.nlp.lemmatizer.AugmentedLemmatizer;

public class VincentDataSerializerTest {

	public static void main(String args[]) { 
		String configFile = "/home/khashab2/Downloads/illinois-lemmatizer/config/lemmatizerConfig.txt"; 
		try
		{
			AugmentedLemmatizer.init( configFile );
			String lemma = AugmentedLemmatizer.getSingleLemma("Falling", "V"); 
			System.out.println("Lemma of the Falling = " + lemma); 
		}
		catch ( IllegalArgumentException e )
		{
			e.printStackTrace();
			System.exit( -1 );
		}
		catch ( JWNLException e )
		{
			e.printStackTrace();
			System.exit( -1 );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			System.exit( -1 );
		}
		
		// reading vincent's data 
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		// testing the annotation 
		for( int i = 0; i < pr.allInstances_withAntecedentAnnotations.size(); i++ ) { 
			TextAnnotation ta = null; 
			try {
				ta = EdisonSerializationHelper.deserializeFromBytes(pr.allInstances_withAntecedentAnnotations.get(i).textAnnotation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] toks = ta.getTokens(); 
			Set<String> annotations = ta.getAvailableViews(); 
			for( String ant : annotations ) { 
				System.out.println(ant);	
			}
			
			String[] tokens = ta.getTokens(); 
			System.out.println("tokens counts = " + tokens.length); 
			View vu = ta.getView("POS"); 
			System.out.println("POS Count = " + vu.count() ); 
			List<Constituent> cons = vu.getConstituents(); 
			System.out.println("consts Count = " + cons.size() ); 
			View lemmaVu = null; 
			try {
				lemmaVu = AugmentedLemmatizer.createLemmaView( ta );
			} catch (JWNLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			List<Constituent> lemma_cons = lemmaVu.getConstituents(); 
			System.out.println("lemma count = " + lemma_cons.size() ); 
					
			assert( vu.count() == tokens.length );
			assert( cons.size() == tokens.length );
			assert( lemma_cons.size() == tokens.length );
			
			for( int j = 0; j < vu.count(); j++) { 
				System.out.println("cons.get(i).getLabel() = " + cons.get(j).getLabel()); 
			}
			for( int j = 0; j < vu.count(); j++) { 
				System.out.println("toks[j] = " + toks[j]); 
			}
			for( int j = 0; j < lemma_cons.size(); j++) { 
				System.out.println("lemma_cons.get(i).getLabel() = " + lemma_cons.get(j).getLabel()); 
			}
			
			//// 
//			String textId = "textId"; 
//			String curatorHost = "trollope.cs.illinois.edu";
//			int curatorPort = 9010;
//			boolean forceUpdate = false;
//			
//			System.out.println("processing the paragraph = " + ins.sentence);
//			
//			//if( iter > 5 )
//			//	break; 
//			CuratorClient client = new CuratorClient(curatorHost, curatorPort);
//			try {
//				
//				if( !ta.hasView("LEMMA") ) { 
//					System.out.println("POS"); 
//					// System.out.println(ta.getTokenizedText());
//					client.add(ta, forceUpdate);
//					//System.out.println(ta.getView(ViewNames.POS));
//				}
//			} 
			/// 
		}
	}
}
