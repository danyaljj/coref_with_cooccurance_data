package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import java.io.IOException;

import net.didion.jwnl.JWNLException;
import edu.illinois.cs.cogcomp.nlp.lemmatizer.AugmentedLemmatizer;

public class LemmatizerTest {

	public static void main(String[] args) { 
		System.out.println("Testing the lemmatizer! ");

		//String configFile = "config/lemmatizerConfig.txt";
		
		String configFile = "/home/khashab2/Downloads/illinois-lemmatizer/config/lemmatizerConfig.txt"; 
		try
		{
			AugmentedLemmatizer.init( configFile );
			String lemma = AugmentedLemmatizer.getSingleLemma("Falling", "V"); 
			System.out.println("Lemma of the Falling = " + lemma); 
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
