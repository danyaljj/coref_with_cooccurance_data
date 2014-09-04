package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import java.util.ArrayList;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.printing.printingNarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.NarrativeSchemaReader;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;

// testing the reader 
public class ReaderTest {
	public static void main(String[] argc) throws Exception { 
		NarrativeSchemaReader nsreader=new NarrativeSchemaReader();
		ArrayList<NarrativeSchemaInstance> allInstances = nsreader.readSchema(6); // 6,8,10,12
		HashMap<Pair<String,String>,Integer> verbOrderMap=nsreader.readVerbOrdering();
		printingNarrativeSchemaInstance.print(allInstances.get(allInstances.size()-1));
	}

}
