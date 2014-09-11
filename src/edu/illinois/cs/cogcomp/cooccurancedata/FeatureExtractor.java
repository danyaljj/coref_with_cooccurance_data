package edu.illinois.cs.cogcomp.cooccurancedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.Relation;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotationUtilities;
import edu.illinois.cs.cogcomp.edison.sentences.View;

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
	
	public void extractConnective() {
		// Just Hack
		// should tokenize
		// in the middle , then check the beginning of the sentence
		// check pos
		ArrayList<String> connectives = connectiveSetup();
		
	}
	
	public void extractHeadNoun() {
		// should tokenize
		// use MD to detect head noun
		ArrayList<String> connectives = connectiveSetup();
		
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
	
	public void Extract() { 
		// the final extraction algorithm here 
	}
	
	public ArrayList<String> connectiveSetup() {
		ArrayList<String> connectives = new ArrayList<String>();
		connectives.add("even though");
		connectives.add("out of");
		connectives.add("so that");
		connectives.add("as if");
		connectives.add("because"); // prior before "when"
		connectives.add("so");
		connectives.add("although");
		connectives.add("though");
		connectives.add("but");
		connectives.add("unless");
		connectives.add("since");
		connectives.add("before");
		connectives.add("after");
		connectives.add("until");
		connectives.add("when");
		connectives.add("that");
		connectives.add("about");
		connectives.add("as");
		connectives.add("if");
		connectives.add("what");
		connectives.add("and");
		return connectives;
	}
}
