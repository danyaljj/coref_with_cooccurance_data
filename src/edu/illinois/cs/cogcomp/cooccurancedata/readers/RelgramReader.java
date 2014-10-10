package edu.illinois.cs.cogcomp.cooccurancedata.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.Tuple;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.TuplePair;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.TuplePair_WithEqualityConstraints;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.Tuple_WithEqualityConstraints;

public class RelgramReader {

	String folder = "/shared/corpora/khashab2/Relgrams/relgrams/rgc/equality"; 

	public void ReadAllFiles() throws Exception { 
		// this loops over all the files in the folder 
		File dir = new File(folder);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			int iter = 0; 
			for (File child : directoryListing) {
				// Do something with child
				System.out.println("reading the files from " + child.getPath()); 
				ReadFile(child.getPath()); 
				iter++; 
				System.out.println("Processed : % " + 100.0 * iter / directoryListing.length ); 
			}
		} else {
			System.out.println("invalid folder!!"); 
			throw new Exception(); 
		}
	}
	
	public void ReadFile(String file) { 
		int minSize = 20; 
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = br.readLine()) != null) {
				TuplePair_WithEqualityConstraints tp = new TuplePair_WithEqualityConstraints(); 
				Tuple_WithEqualityConstraints t1 = new Tuple_WithEqualityConstraints(); 
				Tuple_WithEqualityConstraints t2 = new Tuple_WithEqualityConstraints(); 
				
				String[] tokens = line.split("\t");
				// T1: arg1
				if(tokens[0].contains("XVAR"))
				{ 
					t1.arg1_XVAR = true;
					tokens[0] = tokens[0].replace("XVAR:", ""); 
				}
				if(tokens[0].contains("YVAR"))
				{ 
					t1.arg1_YVAR = true;
					tokens[0] = tokens[0].replace("YVAR:", ""); 
				}
				// T1: arg2
				if(tokens[3].contains("XVAR"))
				{ 
					t1.arg2_XVAR = true;
					tokens[3] = tokens[3].replace("XVAR:", ""); 
				}
				if(tokens[3].contains("YVAR"))
				{ 
					t1.arg2_YVAR = true;
					tokens[3] = tokens[3].replace("YVAR:", ""); 
				}
				// T2: arg1
				if(tokens[6].contains("XVAR"))
				{ 
					t2.arg1_XVAR = true;
					tokens[6] = tokens[6].replace("XVAR:", ""); 
				}
				if(tokens[6].contains("YVAR"))
				{ 
					t2.arg1_YVAR = true;
					tokens[6] = tokens[6].replace("YVAR:", ""); 
				}
				// T2: arg2 
				if(tokens[9].contains("XVAR"))
				{ 
					t2.arg2_XVAR = true;
					tokens[9] = tokens[9].replace("XVAR:", ""); 
				}
				if(tokens[9].contains("YVAR"))
				{ 
					t2.arg2_YVAR = true;
					tokens[9] = tokens[9].replace("YVAR:", ""); 
				}				
				t1.arg1_type = tokens[0]; 
				t1.arg1_surface = tokens[1];
				t1.relation = tokens[2]; 
				t1.arg2_type = tokens[3]; 
				t1.arg2_surface = tokens[4];
				t2.arg1_surface = tokens[5];
				t2.arg1_type = tokens[6]; 
				t2.relation = tokens[7]; 
				t2.arg2_surface = tokens[8];
				t2.arg2_type = tokens[9]; 
				
				tp.tuple1 = t1; 
				tp.tuple2 = t2; 
				String[] counts1_toks = tokens[10].split(","); 
				String[] counts2_toks = tokens[11].split(","); 
				
				tp.counts_directed = new long[7];
				tp.counts_undirected = new long[7];
				for(int i = 0; i < 7; i++) { 
				    tp.counts_undirected[i] = Integer.parseInt(counts1_toks[i]);
				    tp.counts_directed[i] = Integer.parseInt(counts2_toks[i]);
				} 
				//System.out.println(line); 
				//System.out.println( tp.toString() + "\n" ); 
				//System.out.println( tp.getCountsString() + "\n" ); 
			}
			br.close();
		}catch(Exception e){} //or write your own exceptions
	}
	
	public void saveToMapDB() { 
		
		DB db = DBMaker.newFileDB(new File(folder + "unique_pairs_withDenoms_directed_forward" + "_mapdb.bin" ))
				//.closeOnJvmShutdown()
				.make();
		
		// Create a Map:
		Map<Tuple_comparable,long[]> myMap = db.getHashMap("themap");
		
		int i = 0; 
		for( Map.Entry<Tuple, long[]> entryMap : denomCount.entrySet() ) { 
			// Work with the Map using the normal Map API.
			Tuple_comparable tc = new Tuple_comparable(); 
			Tuple t = entryMap.getKey(); 
			tc.arg1_surface = t.arg1_surface; 
			tc.arg1_type = t.arg1_type; 
			tc.arg1_type_equals = t.arg1_type_equals; 
			tc.relation = t.relation;		
			tc.arg2_surface = t.arg2_surface; 
			tc.arg2_type = t.arg2_type; 
			tc.arg2_type_equals = t.arg2_type_equals; 
			tc.count = t.count; 
			
			myMap.put(tc, entryMap.getValue());
			
			i++; 
			if( i % 100 == 10)
			{ 
				System.out.println( "Proccessed = % " + 100.0 * i / denomCount.size() );
				System.out.println( "After myMap.size() = " + myMap.size() );
			}
			if( i % 100 == 10)
			{ 
				db.commit();
			}
			//System.out.println( "After myMap.size() = " + myMap.size() );			
		}
		db.close();		

		
	}
}
