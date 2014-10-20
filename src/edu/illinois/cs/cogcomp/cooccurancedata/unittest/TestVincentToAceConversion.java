package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import org.apache.commons.lang.StringEscapeUtils;

import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;
import edu.illinois.cs.cogcomp.cooccurancedata.writers.OutputPronounResolutionDataInACE2004Format;

public class TestVincentToAceConversion {

	public static void main(String[] args) { 
		testIt(); 
	}
	
	public static void testIt() { 
		String outputFolder = "/shared/shelley/khashab2/CorporaAndDumps/Vincent_Data_Pronoun_Resolution_in_ACE2004_format/"; 

		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		String fileList = ""; 
		for( int i = 0; i < pr.allInstances_withAntecedentAnnotations.size(); i++ )  {
			// convert 
			
			String docNum = Integer.toString(i); 
			String str = "";
			try {
				str = OutputPronounResolutionDataInACE2004Format.convert( pr.allInstances_withAntecedentAnnotations.get(i), i, docNum.length() + 6);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			} 
			//System.out.println(str); 
			
			OutputPronounResolutionDataInACE2004Format.printInFile(outputFolder+Integer.toString(i) + ".apf.xml", str);
						
			String escapedXml_sentence = StringEscapeUtils.escapeXml(pr.allInstances_withAntecedentAnnotations.get(i).sentence);
			//System.out.println( escapedXml_sentence.toString() );
						
			String plainTest = "<DOC>" + 
								"\n<DOCNO>" + docNum + "</DOCNO>" + 
								"\n<DOCTYPE SOURCE=\"newswire\"></DOCTYPE>" + 
								"\n<DATE_TIME></DATE_TIME>" + 
								"\n<BODY>" + 
								"\n<TEXT>\n" + 
								escapedXml_sentence.toString() + 
								"\n</TEXT>" + 
								"\n</BODY>" + 
								"\n</DOC>"; 

			OutputPronounResolutionDataInACE2004Format.printInFile(outputFolder+Integer.toString(i) + ".sgm", plainTest);
			
			fileList += Integer.toString(i) + ".apf.xml\n"; 
		}
		
		// generate file list
		System.out.println(fileList); 	
		OutputPronounResolutionDataInACE2004Format.printInFile(outputFolder+"fileList.txt", fileList);
	}	
}
