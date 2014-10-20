package edu.illinois.cs.cogcomp.cooccurancedata.writers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;

public class OutputPronounResolutionDataInACE2004Format {
	
	public static String convert(WinogradCorefInstance2 ins, int i, int offset) throws Exception { 
		String str = "<?xml version=\"1.0\"?> \n <!DOCTYPE source_file SYSTEM \"alf.v4.0.1.dtd\"> "
				+ "\n<source_file URI=\"APW20001001.2021.0521.sgm\" SOURCE=\"newswire\" TYPE=\"text\" VERSION=\"4.0\" AUTHOR=\"LDC\" ENCODING=\"UTF-8\">"
				+ "\n <document DOCID=\""+ Integer.toString(i) + "\">"; 
		
		String p_xml = StringEscapeUtils.escapeXml(ins.pronoun);
		String a1_xml = StringEscapeUtils.escapeXml(ins.antecedent1);
		String a2_xml = StringEscapeUtils.escapeXml(ins.antecedent2);
		String c_xml = StringEscapeUtils.escapeXml(ins.correct_antecedent);
		String sentence_xml = StringEscapeUtils.escapeXml(ins.sentence);
		
		String p = p_xml.toString(), a1 = a1_xml.toString(), a2 = a2_xml.toString(), s = sentence_xml.toString(), c = c_xml.toString();
		
		if( a1.equals(" Mitt Romney") )
			a1 = "Mitt Romney"; 
		if( a2.equals(" Mitt Romney") )
			a2 = "Mitt Romney"; 
		
		int p_start = 0; 
		int a1_start = 0; 
		int a2_start = 0; 
		// pronoun
		int ind1 = s.indexOf(" " + p + " " );
		int ind2 = s.indexOf(" " + p );
		int ind3 = s.indexOf( p + " " );
		int ind4 = s.indexOf( p );
		if( ind1 != -1)
			p_start = ind1 + offset + 1;
		else if( ind2 != -1)
			p_start = ind2 + offset + 1;
		else if( ind3 != -1)
			p_start = ind3 + offset;
		else
			p_start = ind4 + offset;

		// a1 
		ind1 = s.indexOf(" " + a1 + " " );
		ind2 = s.indexOf(" " + a1 );
		ind3 = s.indexOf( a1 + " " );
		ind4 = s.indexOf( a1 );
		if( ind1 != -1)
			a1_start = ind1 + offset + 1;
		else if( ind2 != -1)
			a1_start = ind2 + offset + 1;
		else if( ind3 != -1)
			a1_start = ind3 + offset;
		else
			a1_start = ind4 + offset;

		// a2 
		ind1 = s.indexOf(" " + a2 + " " );
		ind2 = s.indexOf(" " + a2 );
		ind3 = s.indexOf( a2 + " " );
		ind4 = s.indexOf( a2 );
		if( ind1 != -1)
			a2_start = ind1 + offset + 1;
		else if( ind2 != -1)
			a2_start = ind2 + offset + 1;
		else if( ind3 != -1)
			a2_start = ind3 + offset;
		else
			a2_start = ind4 + offset;

		int p_end = p_start + p.length() - 1; 
		int a1_end = a1_start + a1.length() - 1; 
		int a2_end = a2_start + a2.length() - 1; 
		 
		System.out.println("Sentence = " + s);
		System.out.println("p = " + p);
		System.out.println("a1 = " + a1);
		System.out.println("a2 = " + a2);
		
		System.out.println( "p_start = " + p_start ); 
		System.out.println( "a1_start = " + a1_start );
		System.out.println( "a2_start = " + a2_start );
		
		if( p_start == -1 || a1_start == -1 || a2_start == -1 ) { 
			System.out.println("-----------------------------------> problem in finding the right index");
			System.out.println("Sentence = " + s);
			System.out.println("p = " + p);
			System.out.println("a1 = " + a1);
			System.out.println("a2 = " + a2);
		}
				
		str +=  "\n<entity ID=\"1\" TYPE=\"GPE\" SUBTYPE=\"Nation\" CLASS=\"SPC\">" 
			    + "\n<entity_mention ID=\"1-1\"  TYPE=\"NAM\" LDCTYPE=\"NAM\" ROLE=\"GPE\">" 
			    + "\n<extent>" 
			    + "\n<charseq START=\"" + p_start + "\" END=\"" + p_end + "\">" + p + "</charseq>" 
			    + "\n</extent>"
			    + "\n<head>" 
			    + "\n<charseq START=\"" + p_start + "\" END=\"" + p_end + "\">" + p + "</charseq>"
			    + "\n</head>"
			    + "\n</entity_mention>"; 
		
		if( c.equals(a1) ) { 
			str +=  "\n<entity_mention ID=\"1-2\"  TYPE=\"NAM\" LDCTYPE=\"NAM\" ROLE=\"GPE\">" 
				    + "\n<extent>" 
				    + "\n<charseq START=\"" + a1_start + "\" END=\"" + a1_end + "\">" + a1 + "</charseq>" 
				    + "\n</extent>"
				    + "\n<head>" 
				    + "\n<charseq START=\"" + a1_start + "\" END=\"" + a1_end + "\">"+ a1 + "</charseq>"
				    + "\n</head>"
				    + "\n</entity_mention>"; 
			
			// end 
			str += "\n</entity>"; 
			
			str += "\n<entity ID=\"2\" TYPE=\"GPE\" SUBTYPE=\"Nation\" CLASS=\"SPC\">" 
			 		+ "\n<entity_mention ID=\"1-3\"  TYPE=\"NAM\" LDCTYPE=\"NAM\" ROLE=\"GPE\">" 
				    + "\n<extent>" 
				    + "\n<charseq START=\"" + a2_start + "\" END=\"" + a2_end + "\">" + a2 + "</charseq>" 
				    + "\n</extent>"
				    + "\n<head>" 
				    + "\n<charseq START=\"" + a2_start + "\" END=\"" + a2_end + "\">"+ a2 + "</charseq>"
				    + "\n</head>"
				    + "\n</entity_mention>"; 
			// end 
			str += "\n</entity>"; 
			
		} else if( c.equals(a2) ) { 

			str += "\n<entity_mention ID=\"1-2\"  TYPE=\"NAM\" LDCTYPE=\"NAM\" ROLE=\"GPE\">" 
				    + "\n<extent>" 
				    + "\n<charseq START=\"" + a2_start + "\" END=\"" + a2_end + "\">" + a2 + "</charseq>" 
				    + "\n</extent>"
				    + "\n<head>" 
				    + "\n<charseq START=\"" + a2_start + "\" END=\"" + a2_end + "\">"+ a2 + "</charseq>"
				    + "\n</head>"
				    + "\n</entity_mention>"; 
			
			// end 
			str += "\n</entity>"; 
			
			str += "\n<entity ID=\"2\" TYPE=\"GPE\" SUBTYPE=\"Nation\" CLASS=\"SPC\">" + 
					"\n<entity_mention ID=\"1-3\"  TYPE=\"NAM\" LDCTYPE=\"NAM\" ROLE=\"GPE\">" 
			 		//+ "\n<entity_mention ID=\"1-2\" TYPE=\"NAM\" ROLE=\"ORG\">" 
				    + "\n<extent>" 
				    + "\n<charseq START=\"" + a1_start + "\" END=\"" + a1_end + "\">" + a1 + "</charseq>" 
				    + "\n</extent>"
				    + "\n<head>" 
				    + "\n<charseq START=\"" + a1_start + "\" END=\"" + a1_end + "\">"+ a1 + "</charseq>"
				    + "\n</head>"
				    + "\n</entity_mention>"; 
			// end 
			str += "\n</entity>"; 

		} else { 
			System.out.println("The correct label not found!!"); 
			System.out.println("Answer =|" + ins.correct_antecedent + "|"); 
			System.out.println("Ant1 =|" + ins.antecedent1 + "|"); 
			System.out.println("Ant2 =|" + ins.antecedent2 + "|"); 
			throw new Exception(); 
		}
		
		str += "</document>\n</source_file>"; 
		
		return str; 
	}
	
	public static void printInFile(String fileName, String content) { 
		
		File file = new File(fileName);

		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
