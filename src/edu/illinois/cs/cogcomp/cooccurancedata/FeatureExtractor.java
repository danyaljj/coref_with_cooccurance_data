package edu.illinois.cs.cogcomp.cooccurancedata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.didion.jwnl.JWNLException;

import org.apache.commons.lang3.ArrayUtils;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaRoles;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.Relation;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotationUtilities;
import edu.illinois.cs.cogcomp.edison.sentences.View;
import edu.illinois.cs.cogcomp.nlp.lemmatizer.AugmentedLemmatizer;

class MySpan {
	public int start;
	public int end;
}

public class FeatureExtractor {
	public WinogradCorefInstance2 ins; 
	FeaturePreprocessor fp; 
	int instance_num; 
	boolean withLemmatization = true; 
	
	public int antecend1_verb_start_word_offset; 
	public int antecend1_verb_end_word_offset; 
	
	public int antecend2_verb_start_word_offset; 
	public int antecend2_verb_end_word_offset; 
	
	public int pronoun_verb_start_word_offset; 
	public int pronoun_verb_end_word_offset; 

	public int connective_word_start_word_offset;
	public int connective_word_end_word_offset;

	public int antecend1_head_start_word_offset;
	public int antecend1_head_end_word_offset;
	public int antecend2_head_start_word_offset;
	public int antecend2_head_end_word_offset;
	public int pronoun_head_start_word_offset;
	public int pronoun_head_end_word_offset;

	public double antecend1_generalScore=0;
	public double antecend1_verbScore1=0;
	public double antecend1_verbScore2=0;
	public double antecend1_roleScoreMin=0;
	public double antecend1_roleScoreMax=0;
	public double antecend1_roleScoreAvg=0;
	
	public double antecend2_generalScore=0;
	public double antecend2_verbScore1=0;
	public double antecend2_verbScore2=0;
	public double antecend2_roleScoreMin=0;
	public double antecend2_roleScoreMax=0;
	public double antecend2_roleScoreAvg=0;

	public ArrayList<NarrativeSchemaInstance> schemas;

	public TextAnnotation ta = null; 
	
	public ArrayList<String> connectives = new ArrayList<String>();

	public View lemmaVu = null; 
	List<Constituent> lemma_cons = null; 
		
	public FeatureExtractor() { 
		connectiveSetup();

		// initialize all of the global variables in the class 
		this.ins = null; 
		this.fp = null;
		this.instance_num = -1; 

		antecend1_verb_start_word_offset = -1; 
		antecend1_verb_end_word_offset = -1; 
		antecend2_verb_start_word_offset = -1; 
		antecend2_verb_end_word_offset = -1; 
		pronoun_verb_start_word_offset = -1; 
		pronoun_verb_end_word_offset = -1; 

		connective_word_start_word_offset = -1;
		connective_word_end_word_offset = -1;

		antecend1_head_start_word_offset = -1;
		antecend1_head_end_word_offset = -1;
		antecend2_head_start_word_offset = -1;
		antecend2_head_end_word_offset = -1;
		pronoun_head_start_word_offset = -1;
		pronoun_head_end_word_offset = -1;
		
		ta = null; 
	}

	public void setInstance( WinogradCorefInstance2 ins ) { 
		this.ins = ins;
	}

	public void setInstanceNumber( int instance_num ) { 
		this.instance_num = instance_num;
	}

	public void setPreprocessor( FeaturePreprocessor fp ) { 
		this.fp = fp;
		this.withLemmatization = fp.withLemmatization; 
	}

	public void setNarrativeSchema(ArrayList<NarrativeSchemaInstance> schemas) { 
		this.schemas = schemas;
	}

	public void setTheVerbIndices() { 
		
		//System.out.println("----> Setting the verb indices for the instance = " + instance_num);
		
		Pair<Integer, Integer> ant_verb_index = fp.getVerbTokenIndexGivenInstanceIndex_antecedant(instance_num);
		Pair<Integer, Integer> pro_verb_index = fp.getVerbTokenIndexGivenInstanceIndex_pronoun(instance_num);		
		
		//System.out.println("----> ant_verb_index = " + ant_verb_index);
		//System.out.println("----> pro_verb_index = " + pro_verb_index);
		
		antecend1_verb_start_word_offset = ant_verb_index.getFirst(); 
		antecend1_verb_end_word_offset = ant_verb_index.getSecond();
		antecend2_verb_start_word_offset = ant_verb_index.getFirst(); 
		antecend2_verb_end_word_offset = ant_verb_index.getSecond();
		pronoun_verb_start_word_offset = pro_verb_index.getFirst();
		pronoun_verb_end_word_offset = pro_verb_index.getSecond();
	}

	public void extractTextAnnotation() { 
		ta = null; 
		try {
			ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		try {
			lemmaVu = AugmentedLemmatizer.createLemmaView( ta );
		} catch (JWNLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		lemma_cons = lemmaVu.getConstituents(); 
	}
	
	// obsolete function; see 'Extract2' method bellow 
	// the final extraction algorithm here 
	public double[] Extract() throws Exception { 
		if(ins == null || fp == null || this.instance_num == -1 /*|| ta == null */ )
			throw new Exception(); 
		if( antecend1_verb_start_word_offset == -1 
				|| antecend1_verb_end_word_offset == -1  
				|| antecend2_verb_start_word_offset == -1  
				|| antecend2_verb_end_word_offset == -1  )
			throw new Exception(); 

		if( pronoun_verb_start_word_offset == -1 
				|| pronoun_verb_end_word_offset == -1 )
			throw new Exception(); 

		if( connective_word_start_word_offset == -1 
				/*|| connective_word_end_word_offset == -1 */ )
			throw new Exception(); 

		if( pronoun_head_start_word_offset == -1
				|| pronoun_head_end_word_offset == -1 )
			throw new Exception(); 

		if( antecend1_head_start_word_offset == -1 
				|| antecend1_head_end_word_offset == -1 
				|| antecend2_head_start_word_offset == -1 
				|| antecend2_head_end_word_offset == -1 )
			throw new Exception(); 

		String[] toks = ta.getTokens(); 
		if( connective_word_start_word_offset >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("connective_word_start_word_offset = " + connective_word_start_word_offset); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}

		if( antecend1_verb_start_word_offset  >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend1_verb_start_word_offset  = " + antecend1_verb_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( antecend2_verb_start_word_offset  >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend2_verb_start_word_offset  = " + antecend2_verb_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( pronoun_verb_start_word_offset   >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("pronoun_verb_start_word_offset   = " + pronoun_verb_start_word_offset  ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( antecend1_head_start_word_offset   >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend1_head_start_word_offset   = " + antecend1_head_start_word_offset  ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( antecend2_head_start_word_offset >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend2_head_start_word_offset = " + antecend2_head_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}		
		if( pronoun_head_start_word_offset >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("pronoun_head_start_word_offset  = " + pronoun_head_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		
		// all the toks must be found in the hashmap 
		for( int i = 0; i < toks.length; i++) { 
			if( !fp.tokenMap.containsKey(toks[i]) )
				throw new Exception(); 
		}

		double[] featuresAll = new double[0]; 

		// antecedent-independent features 
		// unigram features 
		double[] unigram_features = new double[fp.tokenMap.size()]; 
		for( int i = 0; i < toks.length; i++) { 
			// exclude the connective 
			if( i == connective_word_start_word_offset  ) //&& i < connective_word_end_word_offset
				continue; 
			unigram_features[ fp.tokenMap.get( toks[i] ) ] = 1; 
		}
		featuresAll = ArrayUtils.addAll(featuresAll, unigram_features);

		// bigram features 
		double[] bigram_features = new double[fp.tokenMap.size() * fp.tokenMap.size()]; 
		for( int i = 0; i < connective_word_start_word_offset; i++) { 
			for( int j = connective_word_start_word_offset+1; j < toks.length; j++) { 
				bigram_features[ fp.tokenMap.get( toks[i] ) + fp.tokenMap.size() * fp.tokenMap.get( toks[j] ) ] = 1; 
			}
		}
		featuresAll = ArrayUtils.addAll(featuresAll, bigram_features);

		//System.out.println("connective_word_start_word_offset = " +  connective_word_start_word_offset); 
		int connective_ind =  fp.tokenMap.get( toks[ connective_word_start_word_offset ] ); 

		// trigram 
//		int[] trigram_features = new int[fp.tokenMap.size() * fp.tokenMap.size() * fp.tokenMap.size()]; 
//		for( int i = 0; i < connective_word_start_word_offset; i++) { 
//			for( int j = connective_word_start_word_offset + 1 ; j < toks.length; j++) { 	 
//				trigram_features[ fp.tokenMap.get( toks[i] ) + fp.tokenMap.size() * fp.tokenMap.get( toks[j] ) + fp.tokenMap.size() * fp.tokenMap.size() * connective_ind ] = 1; 
//			}
//		}
//		featuresAll = ArrayUtils.addAll(featuresAll, trigram_features);

		// antecedent features 
		double[] bigram_features_dependent = new double[fp.tokenMap.size() * fp.tokenMap.size()];

		int head_noun_a1 = fp.tokenMap.get( toks[antecend1_head_start_word_offset] ); 
		int head_noun_a2 = fp.tokenMap.get( toks[antecend2_head_start_word_offset] ); 
		int head_noun_p = fp.tokenMap.get( toks[pronoun_head_start_word_offset] ); 

		int head_verb_a1 = fp.tokenMap.get( toks[antecend1_verb_start_word_offset] ); 
		int head_verb_a2 = fp.tokenMap.get( toks[antecend2_verb_start_word_offset] ); 
		int head_verb_p = fp.tokenMap.get( toks[pronoun_verb_start_word_offset] ); 

		int connective = fp.tokenMap.get( toks[connective_word_start_word_offset] ); 
		
		//System.out.println("head_noun_a1 = " + head_noun_a1); 
		//System.out.println("head_noun_a2 = " + head_noun_a2); 
		//System.out.println("head_noun_p = " + head_noun_p); 
		//System.out.println("head_verb_a1 = "+ head_verb_a1); 
		//System.out.println("head_verb_a2 = "+ head_verb_a2); 
		//System.out.println("head_verb_p = " + head_verb_p);
		//System.out.println("connective = " + connective); 
		//System.out.println("Size of the tokens = " + toks.length); 
				
		//		H(A1)-V(A1)
		//		H(A1)-V(P)
		//		H(A1)-V(A2)
		bigram_features_dependent[ head_noun_a1 + fp.tokenMap.size() * head_verb_a1 ] = 1;
		bigram_features_dependent[ head_noun_a1 + fp.tokenMap.size() * head_verb_p ] = 1;
		bigram_features_dependent[ head_noun_a1 + fp.tokenMap.size() * head_verb_a2 ] = 1;		

		//		H(A2)-V(A1)
		//		H(A2)-V(P)
		//		H(A2)-V(A2)
		bigram_features_dependent[ head_noun_a2 + fp.tokenMap.size() * head_verb_a1 ] = 1;
		bigram_features_dependent[ head_noun_a2 + fp.tokenMap.size() * head_verb_p ] = 1;
		bigram_features_dependent[ head_noun_a2 + fp.tokenMap.size() * head_verb_a2 ] = 1;		

		//		V(A1)-V(A2)
		//		V(A1)-V(P)
		//		V(A2)-V(P)
		bigram_features_dependent[ head_verb_a1 + fp.tokenMap.size() * head_verb_a2 ] = 1;
		bigram_features_dependent[ head_verb_a1 + fp.tokenMap.size() * head_verb_p ] = 1;
		bigram_features_dependent[ head_verb_a2 + fp.tokenMap.size() * head_verb_p ] = 1;		

		featuresAll = ArrayUtils.addAll(featuresAll, bigram_features_dependent);
		
		double[] narrative_schema_features=new double[12];
		narrative_schema_features[0]=antecend1_generalScore;
		narrative_schema_features[1]=antecend1_verbScore1;
		narrative_schema_features[2]=antecend1_verbScore2;
		narrative_schema_features[3]=antecend1_roleScoreMin;
		narrative_schema_features[4]=antecend1_roleScoreMax;
		narrative_schema_features[5]=antecend1_roleScoreAvg;
		narrative_schema_features[6]=antecend2_generalScore;
		narrative_schema_features[7]=antecend2_verbScore1;
		narrative_schema_features[8]=antecend2_verbScore2;
		narrative_schema_features[9]=antecend2_roleScoreMin;
		narrative_schema_features[10]=antecend2_roleScoreMax;
		narrative_schema_features[11]=antecend2_roleScoreAvg;
		
		featuresAll = ArrayUtils.addAll(featuresAll, narrative_schema_features);
		
		return featuresAll; 
	}
	
	// the more efficient version of Extract() 
	// the final extraction algorithm here 
	// option: can take 0 and 1. For zero or one as input, it swaps the features corresponding to antecedent1 and antecedent2. 
	public double[] Extract2(int option) throws Exception { 
		if(ins == null || fp == null || this.instance_num == -1 /*|| ta == null */ )
			throw new Exception(); 
		if( antecend1_verb_start_word_offset == -1 
				|| antecend1_verb_end_word_offset == -1  
				|| antecend2_verb_start_word_offset == -1  
				|| antecend2_verb_end_word_offset == -1  )
			throw new Exception(); 

		if( pronoun_verb_start_word_offset == -1 
				|| pronoun_verb_end_word_offset == -1 )
			throw new Exception(); 

		if( connective_word_start_word_offset == -1 
				/*|| connective_word_end_word_offset == -1 */ )
			throw new Exception(); 

		if( pronoun_head_start_word_offset == -1
				|| pronoun_head_end_word_offset == -1 )
			throw new Exception(); 

		if( antecend1_head_start_word_offset == -1 
				|| antecend1_head_end_word_offset == -1 
				|| antecend2_head_start_word_offset == -1 
				|| antecend2_head_end_word_offset == -1 )
			throw new Exception(); 

		String[] toks = null; 
		if( !withLemmatization )
			toks = ta.getTokens(); 
		else{ 
			assert(ta.getTokens().length == lemma_cons.size() ); 
			toks = new String[lemma_cons.size()]; 
			for(int i = 0; i < lemma_cons.size(); i++) { 
				Constituent cons = lemma_cons.get(i); 
				toks[i] = cons.getLabel(); 
			}
		}
	
		if( connective_word_start_word_offset >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("connective_word_start_word_offset = " + connective_word_start_word_offset); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}

		if( antecend1_verb_start_word_offset  >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend1_verb_start_word_offset  = " + antecend1_verb_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( antecend2_verb_start_word_offset  >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend2_verb_start_word_offset  = " + antecend2_verb_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( pronoun_verb_start_word_offset   >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("pronoun_verb_start_word_offset   = " + pronoun_verb_start_word_offset  ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( antecend1_head_start_word_offset   >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend1_head_start_word_offset   = " + antecend1_head_start_word_offset  ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		if( antecend2_head_start_word_offset >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("antecend2_head_start_word_offset = " + antecend2_head_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}		
		if( pronoun_head_start_word_offset >  toks.length ) { 
			System.out.println("inconsistent sizes"); 
			System.out.println("pronoun_head_start_word_offset  = " + pronoun_head_start_word_offset ); 
			System.out.println("toks.length = " + toks.length);
			throw new Exception(); 
		}
		
		// all the toks must be found in the hashmap 
		for( int i = 0; i < toks.length; i++) { 
			if( !fp.tokenMap.containsKey(toks[i]) ) {
				if( i == connective_word_start_word_offset ) //&& i < connective_word_end_word_offset
					continue;
				throw new Exception();
			}
		}

		double[] featuresAll = new double[0]; 

		// antecedent-independent features 
		// unigram features 
		double[] unigram_features = new double[fp.tokenMap.size()]; 
		for( int i = 0; i < toks.length; i++) { 
			// exclude the connective 
			if( i == connective_word_start_word_offset  ) //&& i < connective_word_end_word_offset
				continue; 
			unigram_features[ fp.tokenMap.get( toks[i] ) ] = 1; 
		}
		featuresAll = ArrayUtils.addAll(featuresAll, unigram_features);

		// bigram features 
		double[] bigram_features = new double[ fp.tokenPairMap.size() ]; 
		for( int i = 0; i < connective_word_start_word_offset; i++) { 
			for( int j = connective_word_start_word_offset+1; j < toks.length; j++) { 
				bigram_features[ fp.tokenPairMap.get( new Pair(toks[i], toks[j]) ) ] = 1; 
			}
		}
		featuresAll = ArrayUtils.addAll(featuresAll, bigram_features);

		//System.out.println("connective_word_start_word_offset = " +  connective_word_start_word_offset); 
		//int connective_ind =  fp.tokenMap.get( toks[ connective_word_start_word_offset ] ); 

		// trigram 
//		int[] trigram_features = new int[fp.tokenMap.size() * fp.tokenMap.size() * fp.tokenMap.size()]; 
//		for( int i = 0; i < connective_word_start_word_offset; i++) { 
//			for( int j = connective_word_start_word_offset + 1 ; j < toks.length; j++) { 	 
//				trigram_features[ fp.tokenMap.get( toks[i] ) + fp.tokenMap.size() * fp.tokenMap.get( toks[j] ) + fp.tokenMap.size() * fp.tokenMap.size() * connective_ind ] = 1; 
//			}
//		}
//		featuresAll = ArrayUtils.addAll(featuresAll, trigram_features);

		// antecedent features 
		int h_a1_v_a1 = fp.pairwiseDependentMap.get( new Pair(toks[antecend1_head_start_word_offset], toks[antecend1_verb_start_word_offset]) ); 
		int h_a1_v_p = fp.pairwiseDependentMap.get( new Pair(toks[antecend1_head_start_word_offset], toks[pronoun_verb_start_word_offset]) ); 
		int h_a1_v_a2 = fp.pairwiseDependentMap.get( new Pair(toks[antecend1_head_start_word_offset], toks[antecend2_verb_start_word_offset]) ); 

		int h_a2_v_a1 = fp.pairwiseDependentMap.get( new Pair(toks[antecend2_head_start_word_offset], toks[antecend1_verb_start_word_offset]) ); 
		int h_a2_v_p = fp.pairwiseDependentMap.get( new Pair(toks[antecend2_head_start_word_offset], toks[pronoun_verb_start_word_offset]) ); 
		int h_a2_v_a2 = fp.pairwiseDependentMap.get( new Pair(toks[antecend2_head_start_word_offset], toks[antecend2_verb_start_word_offset]) ); 

		int v_a1_v_a2 = fp.pairwiseDependentMap.get( new Pair(toks[antecend1_verb_start_word_offset], toks[antecend2_verb_start_word_offset]) ); 
		int v_a2_v_a1 = fp.pairwiseDependentMap.get( new Pair(toks[antecend2_verb_start_word_offset], toks[antecend1_verb_start_word_offset]) ); 
		int v_a1_v_p = fp.pairwiseDependentMap.get( new Pair(toks[antecend1_verb_start_word_offset], toks[pronoun_verb_start_word_offset]) ); 
		int v_a2_v_p = fp.pairwiseDependentMap.get( new Pair(toks[antecend2_verb_start_word_offset], toks[pronoun_verb_start_word_offset]) ); 

		double[] bigram_features_dependent_first_a1 = new double[fp.pairwiseDependentMap.size()];
		double[] bigram_features_dependent_first_a2 = new double[fp.pairwiseDependentMap.size()];
		
		//System.out.println("head_noun_a1 = " + head_noun_a1); 
		//System.out.println("head_noun_a2 = " + head_noun_a2); 
		//System.out.println("head_noun_p = " + head_noun_p); 
		//System.out.println("head_verb_a1 = "+ head_verb_a1); 
		//System.out.println("head_verb_a2 = "+ head_verb_a2); 
		//System.out.println("head_verb_p = " + head_verb_p);
		//System.out.println("connective = " + connective); 
		//System.out.println("Size of the tokens = " + toks.length); 
		
		
		// symmetric 		
		//		H(A1)-V(A1)
		bigram_features_dependent_first_a1[ h_a1_v_a1 ] = 1;
		//		H(A1)-V(P)
		bigram_features_dependent_first_a2[ h_a1_v_p ] = 1;
		//		H(A1)-V(A2)
		bigram_features_dependent_first_a1[ h_a1_v_a2 ] = 1;		
		//		V(A1)-V(A2)
		bigram_features_dependent_first_a1[ v_a1_v_a2 ] = 1;
		//		V(A1)-V(P)
		bigram_features_dependent_first_a1[ v_a1_v_p ] = 1;

		//		H(A2)-V(P)
		bigram_features_dependent_first_a2[ h_a2_v_p ] = 1;
		//		H(A2)-V(A1)
		bigram_features_dependent_first_a2[ h_a2_v_a1 ] = 1;		
		//		H(A2)-V(A2)
		bigram_features_dependent_first_a2[ h_a2_v_a2 ] = 1;
		//		V(A2)-V(P)
		bigram_features_dependent_first_a2[ v_a2_v_p ] = 1;
		//		V(A2)-V(A1)
		bigram_features_dependent_first_a2[ v_a2_v_a1 ] = 1;
		
		if( option == 0) { 
			featuresAll = ArrayUtils.addAll(featuresAll, bigram_features_dependent_first_a1);
			featuresAll = ArrayUtils.addAll(featuresAll, bigram_features_dependent_first_a2);
		} else if( option == 1) { 
			featuresAll = ArrayUtils.addAll(featuresAll, bigram_features_dependent_first_a2);
			featuresAll = ArrayUtils.addAll(featuresAll, bigram_features_dependent_first_a1);			
		} else 
			throw new Exception(); 
/*		
		double[] narrative_schema_features=new double[12];
		narrative_schema_features[0]=antecend1_generalScore;
		narrative_schema_features[1]=antecend1_verbScore1;
		narrative_schema_features[2]=antecend1_verbScore2;
		narrative_schema_features[3]=antecend1_roleScoreMin;
		narrative_schema_features[4]=antecend1_roleScoreMax;
		narrative_schema_features[5]=antecend1_roleScoreAvg;
		narrative_schema_features[6]=antecend2_generalScore;
		narrative_schema_features[7]=antecend2_verbScore1;
		narrative_schema_features[8]=antecend2_verbScore2;
		narrative_schema_features[9]=antecend2_roleScoreMin;
		narrative_schema_features[10]=antecend2_roleScoreMax;
		narrative_schema_features[11]=antecend2_roleScoreAvg;
*/
		double[] narrative_schema_features=new double[2];
		if (antecend1_generalScore>0) narrative_schema_features[0]=1;
		else narrative_schema_features[0]=0;
		if (antecend2_generalScore>0) narrative_schema_features[1]=1;
		else narrative_schema_features[1]=0;
		
//		featuresAll = ArrayUtils.addAll(featuresAll, narrative_schema_features);
		
		return featuresAll; 
	}
	
	public void extractConnective() {
		// Just Hack
		// check for token ? all the pronoun_char_offset
		// should we just care about string ?
		TextAnnotation ta = null; 
		try {
			ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean flag = false;

		// check in the middle
		for (int i=ins.antecedent2_token_end;i<ins.pronoun_word_start;i++) {
			String str=ta.getToken(i).toLowerCase();
			String strs=ta.getToken(i).toLowerCase()+" "+ta.getToken(i+1).toLowerCase();
			for (int j=0;j<connectives.size();j++) {
				if (strs.equals(connectives.get(j))) {
					flag=true;
					connective_word_start_word_offset=i;
					connective_word_start_word_offset=i+1;
					break;
				}
				if (str.equals(connectives.get(j))) {
					flag=true;
					connective_word_start_word_offset=i;
					connective_word_start_word_offset=i;
					break;
				}
			}
		}
		if (flag) return;

		// check the beginning
		for (int i=0;i<ins.antecedent1_token_start;i++) {
			String str=ta.getToken(i).toLowerCase();
			String strs=ta.getToken(i).toLowerCase()+" "+ta.getToken(i+1).toLowerCase();
			for (int j=0;j<connectives.size();j++) {
				if (strs.equals(connectives.get(j))) {
					flag=true;
					connective_word_start_word_offset=i;
					connective_word_start_word_offset=i+1;
					break;
				}
				if (str.equals(connectives.get(j))) {
					flag=true;
					connective_word_start_word_offset=i;
					connective_word_start_word_offset=i;
					break;
				}
			}
		}
		if (flag) return;

		// check the antecedent1 and pronoun
		for (int i=ins.antecedent1_token_end;i<ins.pronoun_word_start;i++) {
			String str=ta.getToken(i).toLowerCase();
			String strs=ta.getToken(i).toLowerCase()+" "+ta.getToken(i+1).toLowerCase();
			for (int j=0;j<connectives.size();j++) {
				if (strs.equals(connectives.get(j))) {
					flag=true;
					connective_word_start_word_offset=i;
					connective_word_start_word_offset=i+1;
					break;
				}
				if (str.equals(connectives.get(j))) {
					flag=true;
					connective_word_start_word_offset=i;
					connective_word_start_word_offset=i;
					break;
				}
			}
		}

		if (!flag) {
			System.out.println(ins.sentence);
			System.out.println(ins.antecedent2_token_end+" "+ins.pronoun_word_start);
			//System.out.println(ins.antecedent2+" "+ins.pronoun);
			System.exit(1);
		}
	}

	public void outputHead(int start, int end) {
		TextAnnotation ta = null; 
		try {
			ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i=start;i<end;i++)
			System.out.print(ta.getToken(i)+" ");
		System.out.println();
	}
	
	public void extractHeadNoun() {
		MySpan span;
		span=extractHead(ins.antecedent1_token_start,ins.antecedent1_token_end);
		antecend1_head_start_word_offset=span.start;
		antecend1_head_end_word_offset=span.end;
		//outputHead(span.start,span.end);

		span=extractHead(ins.antecedent2_token_start,ins.antecedent2_token_end);
		antecend2_head_start_word_offset=span.start;
		antecend2_head_end_word_offset=span.end;
		//outputHead(span.start,span.end);

		span=extractHead(ins.pronoun_word_start,ins.pronoun_word_end);
		pronoun_head_start_word_offset=span.start;
		pronoun_head_end_word_offset=span.end;
		//outputHead(span.start,span.end);
	}

	public MySpan extractHead(int start, int end) {
		TextAnnotation ta = null; 
		try {
			ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MySpan span=new MySpan();

		int index=-1;
		for (int i=start;i<end;i++) {
			if (ta.getToken(i).toLowerCase().equals("of")) {
				index=i;
			}
		}
		if (index==-1) {
			// last token
			span.start=end-1;
			span.end=end;
		}
		else {
			// special case for containing "of"
			span.start=index-1;
			span.end=index;
		}

		// if Capitalized, then check the token before, not "a, the, an"
		if (ta.getToken(span.start).charAt(0)>='A'&&ta.getToken(span.start).charAt(0)<='Z') {
			while (span.start>=1 && ta.getToken(span.start-1).charAt(0)>='A'&&ta.getToken(span.start-1).charAt(0)<='Z') {
				String str=ta.getToken(span.start-1).toLowerCase();
				if (str.equals("a")||str.equals("an")||str.equals("the")) break;
				span.start=span.start-1;
			}
		}

		return span;
	}

	public void extractVerbs() throws Exception { 
		/*TextAnnotation ta = EdisonSerializationHelper.deserializeFromBytes( ins.textAnnotation ); 
		Set<String> va = ta.getAvailableViews();
		for(String str : va) 
			System.out.println( "str = " + str ); */ 
	}

	public String getVerbGivenMention(String str) { 

		System.out.println("mention = " + str );
		System.out.println("Sentence = " + ins.sentence );

		String verb = ""; 
		TextAnnotation ta = null; 
		try {
			ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		View srlvu = ta.getView("SRL"); 

		// find constituent  
		List<Constituent> consts = srlvu.getConstituents(); 
		for( Constituent conIns : consts )
		{ 
			if( conIns.getSurfaceString().equals(str) )
			{ 
				List<Relation> incomRel = conIns.getIncomingRelations();
				System.out.println("Size of the relations = " + incomRel.size() ); 
				for( Relation rel : incomRel)
				{ 
					System.out.println("rel.getSource().getSurfaceString() = " + rel.getSource().getSurfaceString() );
				}
			}
		}


		// find relation

		return verb; 
	}

	public void extractAdjectives() { 
		// Daniel 
	}


	public void connectiveSetup() {
		connectives.add("even though");
		connectives.add("out of");
		connectives.add("so that");
		connectives.add("as if");
		connectives.add("because"); // prior before "when"
		connectives.add("so");
		connectives.add("although");
		connectives.add("though");
		connectives.add("however");
		connectives.add("but");
		connectives.add("unless");
		connectives.add("since");
		connectives.add("before");
		connectives.add("after");
		connectives.add("until");
		connectives.add("when");
		connectives.add("while");
		connectives.add("that");
		connectives.add("about");
		connectives.add("as");
		connectives.add("if");
		connectives.add("what");
		connectives.add("where");
		connectives.add("and");
		connectives.add("or");
		connectives.add("then");
		connectives.add("thus");
		connectives.add("hence");
		connectives.add("therefore");
		connectives.add(":");
	}

	private String getVerb(int start, int end) {
		String[] toks = null; 
		if( !withLemmatization )
			toks = ta.getTokens(); 
		else{ 
			assert(ta.getTokens().length == lemma_cons.size() ); 
			toks = new String[lemma_cons.size()]; 
			for(int i = 0; i < lemma_cons.size(); i++) { 
				Constituent cons = lemma_cons.get(i); 
				toks[i] = cons.getLabel(); 
			}
		}

		String verb=toks[start];
		for (int i=start+1;i<end;i++) {
			verb=verb+" "+toks[i];
		}

		return verb;
	}

	private int checkVerb(String verb, String[] events) {
		verb=verb.replace(' ', '_');
		for (int i=0;i<events.length;i++) {
			if (verb.equals(events[i])) {
				return i;
			}
		}
		return -1;
	}

	private int checkVerb(String verb, Vector<String> events) {
		for (int i=0;i<events.size();i++) {
			if (verb.equals(events.get(i))) {
				return i;
			}
		}
		return -1;
	}

	public void extractNarrativeSchema(int p) {
		// p=1 => for antecedent1
		// p=2 => for antecedent2
		
		if (instance_num==1056) return;
		//System.out.println(instance_num+"\t"+ins.sentence);
		//System.out.println(pronoun_verb_start_word_offset+" "+pronoun_verb_end_word_offset);
		
		String verb1="";
		if (p==1) {
			verb1=getVerb(antecend1_verb_start_word_offset,antecend1_verb_end_word_offset);
		}
		else {
			verb1=getVerb(antecend2_verb_start_word_offset,antecend2_verb_end_word_offset);
		}
		String verb2=getVerb(pronoun_verb_start_word_offset,pronoun_verb_end_word_offset);

		TextAnnotation ta = null; 
		try {
			ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		View srlvu = ta.getView("SRL"); 

		int start=0, end=0;
		if (p==1) {
			start=ins.antecedent1_token_start;
			end=ins.antecedent1_token_end;
		}
		else {
			start=ins.antecedent2_token_start;
			end=ins.antecedent2_token_end;
		}
		String role1=getRoleFromSRL(srlvu,verb1,start,end);
		String role2=getRoleFromSRL(srlvu,verb2,ins.pronoun_word_start,ins.pronoun_word_end);
		
		double generalScore=0, verbScore1=0, verbScore2=0, roleScoreMin=0, roleScoreMax=0, roleScoreAvg=0;
		
		for (int i=0;i<schemas.size();i++) {
			NarrativeSchemaInstance schema=schemas.get(i);
			int a=checkVerb(verb1,schema.events);
			int b=checkVerb(verb2,schema.events);
			if (a!=-1 && b!=-1) {
				/*
				System.out.println("hit");
				System.out.println(ins.sentence);
				for (int j=0;j<schema.length;j++) {
					System.out.print(schema.events[j]+" ");
				}
				System.out.println();
				*/
				generalScore=schema.generalScore;
				verbScore1=schema.eventScores[a];
				verbScore2=schema.eventScores[b];
				if (!role1.equals("") && !role2.equals("")) {
					for (int j=0;j<schema.roleVecs.size();j++) {
						NarrativeSchemaRoles roles=schema.roleVecs.get(j);
						int x=checkVerb(verb1,roles.roles);
						int y=checkVerb(verb2,roles.roles);
						if (x!=-1 && y!=-1 && roles.roleTypes.get(x).equals(role1) && roles.roleTypes.get(y).equals(role2)) {
							if (roles.headSize==0) break;
							roleScoreMin=roles.headScores.get(0);
							roleScoreMax=roles.headScores.get(0);
							roleScoreAvg=roles.headScores.get(0);
							for (int t=1;t<roles.headSize;t++) {
								if (roleScoreMin>roles.headScores.get(t)) {
									roleScoreMin=roles.headScores.get(t);
								}
								if (roleScoreMax<roles.headScores.get(t)) {
									roleScoreMax=roles.headScores.get(t);
								}
								roleScoreAvg+=roles.headScores.get(t);
							}
							roleScoreAvg=roleScoreAvg/roles.headSize;
							break;
						}
					}
				}
				break;
			}
		}
		
		if (p==1) {
			antecend1_generalScore=generalScore;
			antecend1_verbScore1=verbScore1;
			antecend1_verbScore2=verbScore2;
			antecend1_roleScoreMin=roleScoreMin;
			antecend1_roleScoreMax=roleScoreMax;
			antecend1_roleScoreAvg=roleScoreAvg;
		}
		else {
			antecend2_generalScore=generalScore;
			antecend2_verbScore1=verbScore1;
			antecend2_verbScore2=verbScore2;
			antecend2_roleScoreMin=roleScoreMin;
			antecend2_roleScoreMax=roleScoreMax;
			antecend2_roleScoreAvg=roleScoreAvg;
		}
	}
	
	private String getRoleFromSRL(View srlvu, String verb, int start, int end) {
		String role="";
		List<Constituent> consts = srlvu.getConstituents();
		for (Constituent conIns : consts ) { 
			if (conIns.getSurfaceString().equals(verb)) { 
				List<Relation> incomRel = conIns.getIncomingRelations();
				for( Relation rel : incomRel) {
					if (rel.getRelationName().equals("A0")) {
						Constituent c=rel.getTarget();
						// Gold mention covers SRL mention
						if (start<=c.getStartSpan() && end>=c.getEndSpan()) {
							role="s";
							break;
						}
					}
					if (rel.getRelationName().equals("A1")) {
						Constituent c=rel.getTarget();
						// Gold mention covers SRL mention
						if (start<=c.getStartSpan() && end>=c.getEndSpan()) {
							role="o";
							break;
						}
					}
				}
			}
		}
		return role;
	}

	public int getLabel() {
		if (ins.correct_antecedent.equals(ins.antecedent1)) return 1;
		if (ins.correct_antecedent.equals(ins.antecedent2)) return -1;
		System.out.println("Error: Instance Label can not decide\t"+ins.antecedent1+"\t"+ins.antecedent2+"\t"+ins.correct_antecedent);
		return -1;
	}
	// update the tables given the current features 
	public void updateTables() { 
		String[] toks = null; 
		if( !withLemmatization )
			toks = ta.getTokens(); 
		else{ 
			assert(ta.getTokens().length == lemma_cons.size() ); 
			toks = new String[lemma_cons.size()]; 
			for(int i = 0; i < lemma_cons.size(); i++) { 
				Constituent cons = lemma_cons.get(i); 
				toks[i] = cons.getLabel(); 
			}
		}
			
		// unigram features 
		double[] unigram_features = new double[fp.tokenMap.size()]; 
		for( int i = 0; i < toks.length; i++) { 
			// exclude the connective 
			if( i == connective_word_start_word_offset  ) //&& i < connective_word_end_word_offset
				continue; 
			if( !fp.tokenMap.containsKey( toks[i] )  )
				fp.tokenMap.put( toks[i], fp.tokenMap.size() ); 
		}
		
		// bigram features
		for( int i = 0; i < connective_word_start_word_offset; i++) { 
			for( int j = connective_word_start_word_offset+1; j < toks.length; j++) { 
				Pair<String, String> pairInstance = new Pair(toks[i], toks[j]); 
				if( !fp.tokenPairMap.containsKey( pairInstance ) )
					fp.tokenPairMap.put( pairInstance, fp.tokenPairMap.size() );
			}
		}
			
		Pair<String, String> pair_h_a1_v_a1 = new Pair(toks[antecend1_head_start_word_offset], toks[antecend1_verb_start_word_offset]); 
		Pair<String, String> pair_h_a1_v_p = new Pair(toks[antecend1_head_start_word_offset], toks[pronoun_verb_start_word_offset]); 
		Pair<String, String> pair_h_a1_v_a2 = new Pair(toks[antecend1_head_start_word_offset], toks[antecend2_verb_start_word_offset]); 
		Pair<String, String> pair_h_a2_v_a1 = new Pair(toks[antecend2_head_start_word_offset], toks[antecend1_verb_start_word_offset]); 
		Pair<String, String> pair_h_a2_v_p = new Pair(toks[antecend2_head_start_word_offset], toks[pronoun_verb_start_word_offset]); 
		Pair<String, String> pair_h_a2_v_a2 = new Pair(toks[antecend2_head_start_word_offset], toks[antecend2_verb_start_word_offset]); 
		Pair<String, String> pair_v_a1_v_a2 = new Pair(toks[antecend1_verb_start_word_offset], toks[antecend2_verb_start_word_offset]); 
		Pair<String, String> pair_v_a2_v_a1 = new Pair(toks[antecend2_verb_start_word_offset], toks[antecend1_verb_start_word_offset]); 
		Pair<String, String> pair_v_a1_v_p = new Pair(toks[antecend1_verb_start_word_offset], toks[pronoun_verb_start_word_offset]); 
		Pair<String, String> pair_v_a2_v_p = new Pair(toks[antecend2_verb_start_word_offset], toks[pronoun_verb_start_word_offset]); 
		
		if( !fp.pairwiseDependentMap.containsKey( pair_h_a1_v_a1 ) ) 
			fp.pairwiseDependentMap.put( pair_h_a1_v_a1, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_h_a1_v_p ) ) 
			fp.pairwiseDependentMap.put( pair_h_a1_v_p, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_h_a1_v_a2 ) ) 
			fp.pairwiseDependentMap.put( pair_h_a1_v_a2, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_h_a2_v_a1  ) ) 
			fp.pairwiseDependentMap.put( pair_h_a2_v_a1 , fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_h_a2_v_p ) ) 
			fp.pairwiseDependentMap.put( pair_h_a2_v_p, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_h_a2_v_p ) ) 
			fp.pairwiseDependentMap.put( pair_h_a2_v_p, fp.pairwiseDependentMap.size() );		
		if( !fp.pairwiseDependentMap.containsKey( pair_h_a2_v_a2 ) ) 
			fp.pairwiseDependentMap.put( pair_h_a2_v_a2, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_v_a1_v_a2 ) ) 
			fp.pairwiseDependentMap.put( pair_v_a1_v_a2, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_v_a2_v_a1 ) ) 
			fp.pairwiseDependentMap.put( pair_v_a2_v_a1, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_v_a1_v_p ) ) 
			fp.pairwiseDependentMap.put( pair_v_a1_v_p, fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( pair_v_a2_v_p ) ) 
			fp.pairwiseDependentMap.put( pair_v_a2_v_p, fp.pairwiseDependentMap.size() );
		
		
		// pairwise dependent table 
/*		if( !fp.pairwiseDependentMap.containsKey( toks[antecend1_head_start_word_offset] ) ) 
			fp.pairwiseDependentMap.put( toks[antecend1_head_start_word_offset], fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( toks[antecend2_head_start_word_offset] ) ) 
			fp.pairwiseDependentMap.put( toks[antecend2_head_start_word_offset], fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( toks[pronoun_head_start_word_offset] ) ) 
			fp.pairwiseDependentMap.put( toks[pronoun_head_start_word_offset], fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( toks[antecend1_verb_start_word_offset] ) ) 
			fp.pairwiseDependentMap.put( toks[antecend1_verb_start_word_offset], fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( toks[antecend2_verb_start_word_offset] ) ) 
			fp.pairwiseDependentMap.put( toks[antecend2_verb_start_word_offset], fp.pairwiseDependentMap.size() );
		if( !fp.pairwiseDependentMap.containsKey( toks[pronoun_verb_start_word_offset] ) ) 
			fp.pairwiseDependentMap.put( toks[pronoun_verb_start_word_offset], fp.pairwiseDependentMap.size() );		
*/	}
}
