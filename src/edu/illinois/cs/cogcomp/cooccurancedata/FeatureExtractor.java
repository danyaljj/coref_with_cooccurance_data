package edu.illinois.cs.cogcomp.cooccurancedata;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;

public class FeatureExtractor {
	static public WinogradCorefInstance ins; 
	
	public int antecend1_verb_start_word_offset; 
	public int antecend1_verb_end_word_offset; 
	public int antecend1_verb_start_char_offset; 
	public int antecend1_verb_end_char_offset; 
	
	public int antecend2_adjective_start_word_offset; 
	public int antecend2_adjective_end_word_offset; 
	public int antecend2_adjective_start_char_offset; 
	public int antecend2_adjective_end_char_offset; 
	
	public int connective_word_offset; 
	public int connective_adjective_end_word_offset; 
	public int connective_adjective_start_char_offset; 
	public int connective_adjective_end_char_offset; 
	
	public FeatureExtractor() { 
		// nothing 
	}
	
	public FeatureExtractor( WinogradCorefInstance ins ) { 
		this.ins = ins; 
	}
	
	public static void extractConnective() {
		// Haoruo 
	}
	
	public static void extractVerbs() { 
		// Daniel 
	}
	
	public static void extractAdjectives() { 
		// Daniel 
	}
	
	public static void Extract() { 
		// the final extraction algorithm here 
	}
}
