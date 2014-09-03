package edu.illinois.cs.cogcomp.cooccurancedata.readers;

import java.io.IOException;

public class NarrativeSchemaReader {
	
	static String schema_file_06 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size6"; 
	static String schema_file_08 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size8";
	static String schema_file_10 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size10";
	static String schema_file_12 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size12";
	static String verb_ordering_file = "/shared/experiments/hpeng7/data/NarrativeSchema/ordering.lemmas.closed.nones0.5.stats";
	
	// testing the reader 
	public static void main(String[] argc) { 
		Read_all_instances();
		PostProcessData();
		serializeData(); 
	}

	public void read() {
		
	}
}
