package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.printing.printingNarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.IOManager;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.NarrativeSchemaReader;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;

// testing the reader 
public class ReaderTest {
	public static void main(String[] argc) throws Exception { 
		/*
		NarrativeSchemaReader nsreader=new NarrativeSchemaReader();
		ArrayList<NarrativeSchemaInstance> allInstances = nsreader.readSchema(6); // 6,8,10,12
		HashMap<Pair<String,String>,Integer> verbOrderMap = nsreader.readVerbOrdering();
		printingNarrativeSchemaInstance.print(allInstances.get(allInstances.size()-1));
		*/
		ArrayList<String> lines = IOManager.readLines("/shared/experiments/hpeng7/data/Winograd/all.txt");
		BufferedWriter bw = IOManager.openWriter("/shared/experiments/hpeng7/data/Winograd/5.txt");
		for (int i=0;i<lines.size();i=i+2) {
			String tmp=lines.get(i+1);
			if (!tmp.equals("") && Integer.parseInt(tmp)==5) {
				bw.write(lines.get(i)+"\n");
			}
		}
		bw.close();
	}
}
