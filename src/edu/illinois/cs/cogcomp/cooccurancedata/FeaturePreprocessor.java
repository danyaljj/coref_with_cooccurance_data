package edu.illinois.cs.cogcomp.cooccurancedata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.didion.jwnl.JWNLException;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.nlp.lemmatizer.AugmentedLemmatizer;

//this reads all of the instances, and creates feature tables. 
public class FeaturePreprocessor {
	List<WinogradCorefInstance2> allInstances = null; 
	public Map<String, Integer> tokenMap = new HashMap<String, Integer> ();
	public Map<String, Integer> tokenPairMap1 = new HashMap<String, Integer> ();
	public Map<String, Integer> tokenPairMap2 = new HashMap<String, Integer> ();
	public Map<String, Integer> pairwiseDependentMap = new HashMap<String, Integer> ();
	
	// i-th key corresponds to the i-th sentence. The value corresponds to the token start and end index. 
	public Map<Integer, Pair<Integer, Integer>> antVerbIndex = new HashMap<Integer, Pair<Integer, Integer>> (); 
	public Map<Integer, Pair<Integer, Integer>> pronounVerbIndex = new HashMap<Integer, Pair<Integer, Integer>> (); 
		
	boolean withLemmatization = false; 
	
	/// goldVerns : if it is true, we would use the gold verbs, some extracted manually, and other using SRL
	// 				if it is false, we would use the verbs discovered using SRL 
	public FeaturePreprocessor(List<WinogradCorefInstance2> allInstances, boolean withLemmatization, boolean goldVerbs) { 
		this.allInstances = allInstances;
		this.withLemmatization = withLemmatization; 
		if( withLemmatization ){ 		
			String configFile = "/home/khashab2/Downloads/illinois-lemmatizer/config/lemmatizerConfig.txt"; 
			try
			{
				AugmentedLemmatizer.init( configFile );
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
		}		
	}

	public void readInstanceVerbs() { 		
		String folder = "/shared/experiments/khashab2/workspace1/EventCooccurance/docs/extractedVerbs_withManualAnnotatation2/";
		
		// antecedents 
		int lineIter = 0; 
		try {
			//System.out.println("Reading the files done! "); 
			FileReader fileReader = new FileReader(new File(folder + "ant1.txt"));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if( !line.contains(" **** ") ) { 
					System.out.println("The separator not found at the line = " + lineIter + "! ");
				}
				String[] tokenized = line.split(" "); 
				int tokenInd1 = -1; 
				int tokenInd2 = -1;
				for( int i = 0; i < tokenized.length; i++) { 
					//System.out.println(tokenized[i]);
					if( tokenized[i].equals("****") ) { 
						if( tokenInd1 == -1 )
							tokenInd1 = i; 
						else 
							tokenInd2 = i; 
					}
				}
				if( tokenInd1 == -1 || tokenInd2 == -1 )
					break; 
				
				antVerbIndex.put(lineIter, new Pair(tokenInd1, tokenInd2-1) );  
				//System.out.println("lineIter = " + lineIter);
				//System.out.println("tokenInd1  = " + tokenInd1 + "  :  "  +  tokenized[tokenInd1]); 
				//System.out.println("tokenInd2 = " + tokenInd2 + "  :  " + tokenized[tokenInd2]); 
				lineIter++; 
			}
			fileReader.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// pronouns
		lineIter = 0; 
		try {
			//System.out.println("Reading the files done! "); 
			FileReader fileReader = new FileReader(new File(folder + "pro.txt"));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if( !line.contains(" **** ") ) { 
					System.out.println("The separator not found at the line = " + lineIter + "! ");
				}
				String[] tokenized = line.split(" "); 
				int tokenInd1 = -1; 
				int tokenInd2 = -1;
				for( int i = 0; i < tokenized.length; i++) { 
					//System.out.println(tokenized[i]);
					if( tokenized[i].equals("****") ) { 
						if( tokenInd1 == -1 )
							tokenInd1 = i; 
						else 
							tokenInd2 = i; 
					}
				}
				if( tokenInd1 == -1 || tokenInd2 == -1 )
					break; 
				
				pronounVerbIndex.put(lineIter, new Pair(tokenInd1, tokenInd2-1) );  
				//System.out.println("lineIter = " + lineIter);
				//System.out.println("tokenInd1  = " + tokenInd1 + "  :  "  +  tokenized[tokenInd1]); 
				//System.out.println("tokenInd2 = " + tokenInd2 + "  :  " + tokenized[tokenInd2]); 
				lineIter++; 
			}
			fileReader.close();	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Process() {
		// read verbs : 
		//System.out.println("Reading the verbs from disk"); 
		readInstanceVerbs(); 
		
		// add all the tokens to the hashmap 
		for(WinogradCorefInstance2 ins : allInstances) { 
			TextAnnotation ta = null; 
			try {
				ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] toks = ta.getTokens(); 
			for( int i = 0; i < toks.length; i++) { 
				if( !tokenMap.containsKey(toks[i]) )
				{ 
					tokenMap.put(toks[i], tokenMap.size()); 
				}
			}
		}	
	}
	
	public Pair<Integer,Integer> getVerbTokenIndexGivenInstanceIndex_antecedant(int instanceIndex) { 
		return antVerbIndex.get(instanceIndex); 
	}
	
	public Pair<Integer,Integer> getVerbTokenIndexGivenInstanceIndex_pronoun(int instanceIndex) { 
		return pronounVerbIndex.get(instanceIndex); 
	}
	
}
