package edu.illinois.cs.cogcomp.cooccurancedata.printing;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;

public class printWinogradInstance {
	public static void print(WinogradCorefInstance ins) { 
		System.out.println("ins.sentence = " + ins.sentence ); 
		System.out.println("ins.pronoun = " + ins.pronoun );
		System.out.println("ins.antecedent1 = " + ins.antecedent1 );
		System.out.println("ins.antecedent2 = " + ins.antecedent2 );
		System.out.println("ins.correct_antecedent = " + ins.correct_antecedent );
	}
}
