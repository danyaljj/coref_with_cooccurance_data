package edu.illinois.cs.cogcomp.cooccurancedata.datastructures;

import java.io.Serializable;

public class WinogradCorefInstance2 implements Serializable {
	// to be filled in when reading the data 
	public String sentence; 
	public String pronoun; 
	public String antecedent1; 
	public String antecedent2; 
	public String correct_antecedent; 
	public int test_or_train; // 0: test   1: train 
	
	// to be filled in, after preprocessing 
	public int pronoun_char_start; 
	public int antecedent1_char_start; 
	public int antecedent2_char_start; 
	public int pronoun_char_end; 
	public int antecedent1_char_end; 
	public int antecedent2_char_end; 
	
	//  token based
	public int pronoun_word_start;
	public int pronoun_word_end;
	public int antecedent1_token_start; 
	public int antecedent1_token_end; 
	public int antecedent2_token_start; 
	public int antecedent2_token_end; 
	
	// curator annotation into bytes 
	public byte[] textAnnotation; 
	public String textAnnotation_string;
	
	public byte[] textAnnotation_ant1; 
	public String textAnnotation_string_ant1;
	
	public byte[] textAnnotation_ant2; 
	public String textAnnotation_string_ant2;
	
	public byte[] textAnnotation_pronoun; 
	public String textAnnotation_string_pronoun;
}

