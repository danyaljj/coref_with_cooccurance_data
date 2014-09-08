package edu.illinois.cs.cogcomp.cooccurancedata.datastructures;

import java.io.Serializable;

public class WinogradCorefInstance implements Serializable {
	// to be filled in when reading the data 
	public String sentence; 
	public String pronoun; 
	public String antecedent1; 
	public String antecedent2; 
	public String correct_antecedent; 
	public int test_or_train; // 1: test   0: train 
	
	// to be filled in, after preprocessing 
	public int pronoun_char_offset; 
	public int antecedent1_char_offset; 
	public int antecedent2_char_offset; 
	
	public int pronoun_word_offset; 
	public int antecedent1_word_offset; 
	public int antecedent2_word_offset; 
	
	// curator annotation into bytes 
	public byte[] textAnnotation; 
	public String textAnnotation_string;
}
