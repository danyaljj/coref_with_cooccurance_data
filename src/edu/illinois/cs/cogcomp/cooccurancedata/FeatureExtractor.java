package edu.illinois.cs.cogcomp.cooccurancedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaRoles;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.edison.sentences.Constituent;
import edu.illinois.cs.cogcomp.edison.sentences.EdisonSerializationHelper;
import edu.illinois.cs.cogcomp.edison.sentences.Relation;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotation;
import edu.illinois.cs.cogcomp.edison.sentences.TextAnnotationUtilities;
import edu.illinois.cs.cogcomp.edison.sentences.View;

class MySpan {
	public int start;
	public int end;
}

public class FeatureExtractor {
	static public WinogradCorefInstance2 ins; 
	
	public int antecend1_verb_start_word_offset; 
	public int antecend1_verb_end_word_offset; 
	public int antecend1_verb_start_char_offset; 
	public int antecend1_verb_end_char_offset; 
	public int antecend2_verb_start_word_offset; 
	public int antecend2_verb_end_word_offset; 
	public int antecend2_verb_start_char_offset; 
	public int antecend2_verb_end_char_offset;
	public int pronoun_verb_start_word_offset; 
	public int pronoun_verb_end_word_offset; 
	public int pronoun_verb_start_char_offset; 
	public int pronoun_verb_end_char_offset;
	
	public int antecend2_adjective_start_word_offset; 
	public int antecend2_adjective_end_word_offset; 
	public int antecend2_adjective_start_char_offset; 
	public int antecend2_adjective_end_char_offset; 
	
	public int connective_word_start_word_offset;
	public int connective_word_end_word_offset;
	
	public int antecend1_head_start_word_offset;
	public int antecend1_head_end_word_offset;
	public int antecend2_head_start_word_offset;
	public int antecend2_head_end_word_offset;
	public int pronoun_head_start_word_offset;
	public int pronoun_head_end_word_offset;
	
	public double generalScore=0;
	public double verbScore1=0;
	public double verbScore2=0;
	public double roleScoreMin=0;
	public double roleScoreMax=0;
	public double roleScoreAvg=0;
	
	public ArrayList<NarrativeSchemaInstance> schemas;

	private ArrayList<String> connectives = new ArrayList<String>();
	
	public FeatureExtractor() { 
		connectiveSetup();
	}
	
	public void setInstance( WinogradCorefInstance2 ins ) { 
		this.ins = ins;
	}
	
	public void setNarrativeSchema(ArrayList<NarrativeSchemaInstance> schemas) { 
		this.schemas = schemas;
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
	
	public void extractHeadNoun() {
		MySpan span;
		span=extractHead(ins.antecedent1_token_start,ins.antecedent1_token_end);
		antecend1_head_start_word_offset=span.start;
		antecend1_head_end_word_offset=span.end;

		span=extractHead(ins.antecedent2_token_start,ins.antecedent2_token_end);
		antecend2_head_start_word_offset=span.start;
		antecend2_head_end_word_offset=span.end;

		span=extractHead(ins.pronoun_word_start,ins.pronoun_word_end);
		pronoun_head_start_word_offset=span.start;
		pronoun_head_end_word_offset=span.end;
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
	
	public void Extract() { 
		// the final extraction algorithm here 
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
		TextAnnotation ta = null; 
		try {
			ta = EdisonSerializationHelper.deserializeFromBytes(ins.textAnnotation);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String verb=ta.getToken(start);
		for (int i=start+1;i<end;i++) {
			verb=verb+" "+ta.getToken(i);
		}
		
		return verb;
	}
	
	private int checkVerb(String verb, String[] events) {
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
		String verb1="";
		if (p==1) {
			verb1=getVerb(antecend1_verb_start_word_offset,antecend1_verb_end_word_offset);
		}
		else {
			verb1=getVerb(antecend2_verb_start_word_offset,antecend2_verb_end_word_offset);
		}
		String verb2=getVerb(pronoun_verb_start_word_offset,pronoun_verb_end_word_offset);
		
		// Assume we have agr1, agr2
		String role1="o";String role2="s";
		for (int i=0;i<schemas.size();i++) {
			NarrativeSchemaInstance schema=schemas.get(i);
			int a=checkVerb(verb1,schema.events);
			int b=checkVerb(verb2,schema.events);
			if (a!=-1 && b!=-1) {
				generalScore=schema.generalScore;
				verbScore1=schema.eventScores[a];
				verbScore2=schema.eventScores[b];
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
				break;
			}
		}
	}
}
