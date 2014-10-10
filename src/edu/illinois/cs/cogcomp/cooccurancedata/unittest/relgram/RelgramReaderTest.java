package edu.illinois.cs.cogcomp.cooccurancedata.unittest.relgram;

import edu.illinois.cs.cogcomp.cooccurancedata.readers.RelgramReader;

public class RelgramReaderTest {
	public static void main(String[] args) { 
		System.out.println("A sanple msg!"); 
		RelgramReader rd = new RelgramReader();
		try {
			rd.ReadAllFiles();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
