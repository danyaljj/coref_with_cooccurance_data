package edu.illinois.cs.cogcomp.cooccurancedata.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import ch.qos.logback.core.db.dialect.MySQLDialect;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.Tuple;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.TuplePair;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.TuplePair_WithEqualityConstraints;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.Tuple_WithEqualityConstraints;

public class RelgramReader {

	String folder = "/shared/corpora/khashab2/Relgrams/relgrams/rgc/equality"; 
	String serializationFolder = "/shared/shelley/khashab2/SummerProject/JSON"; 	
	
	List<TuplePair_WithEqualityConstraints> tuplePairs = null; 
	
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
	
	// this assumes the tuple pairs are already read and are in the disk 
	public void addProbabilities() { 
		DB db = DBMaker.newFileDB(new File(serializationFolder + "denominators" + "_mapdb.bin" ))
				.readOnly()
				.make();
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_first_tuple_directed = db.getHashMap("counts_first_tuple_directed");
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_first_tuple_undirected = db.getHashMap("counts_first_tuple_dunirected");
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_second_tuple_directed = db.getHashMap("counts_second_tuple_directed");
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_second_tuple_undirected = db.getHashMap("counts_second_tuple_dunirected");

		System.out.println("Size of the denoms = " + myMap_counts_first_tuple_directed.size() ); 
		System.out.println("Size of the denoms = " + myMap_counts_first_tuple_undirected.size() ); 
		System.out.println("Size of the denoms = " + myMap_counts_second_tuple_directed.size() ); 
		System.out.println("Size of the denoms = " + myMap_counts_second_tuple_undirected.size() ); 
		
		int iter = 0; 
		for( TuplePair_WithEqualityConstraints pairIns : tuplePairs ) { 
			iter++; 
			if( iter % 2000 == 10 )
				System.out.println("Processed % " +  100.0 * iter / tuplePairs.size() ); 
			// forward 
			if( myMap_counts_first_tuple_directed.containsKey( pairIns.tuple1 ) ) {
				int[] countDirected = myMap_counts_first_tuple_directed.get(pairIns.tuple1); 
				int[] countUndirected = myMap_counts_first_tuple_undirected.get(pairIns.tuple1); 
				pairIns.forward_probablities_directed = new double[7]; 
				pairIns.forward_probablities_undirected = new double[7]; 
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * pairIns.counts_directed[i] / countDirected[i]; 
					pairIns.forward_probablities_directed[i] = score; 
					if( score > 1.01 )
						System.out.println("--------> Forward: Why is this prob more than one?! "); 
				}
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * pairIns.counts_undirected[i] / countUndirected[i]; 
					pairIns.forward_probablities_undirected[i] = score; 
					if( score > 1.01 )
						System.out.println("--------> Forward: Why is this prob more than one?! "); 
				}
			}
			
			// backward 
			if( myMap_counts_second_tuple_directed.containsKey( pairIns.tuple2 ) ) {
				int[] countDirected = myMap_counts_second_tuple_directed.get(pairIns.tuple2); 
				int[] countUndirected = myMap_counts_second_tuple_undirected.get(pairIns.tuple2); 
				pairIns.backward_probablities_directed = new double[7]; 
				pairIns.backward_probablities_undirected = new double[7]; 
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * ( pairIns.counts_undirected[i] - pairIns.counts_directed[i] ) / ( countUndirected[i] - countDirected[i] ); 
					pairIns.backward_probablities_directed[i] = score; 
					if( score > 1.01 )
						System.out.println("--------> Backward: Why is this prob more than one?! "); 
				}
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * pairIns.counts_undirected[i] / countUndirected[i]; 
					pairIns.backward_probablities_undirected[i] = score; 
					if( score > 1.01 )
						System.out.println("--------> Backward: Why is this prob more than one?! "); 
				}
			}
		}
	}
	
	public void AnyDuplications() { 
		int numDuplicates = 0; 
		System.out.println("Start checking the duplications!!");
		for( int i = 0; i < tuplePairs.size(); i++  ) { 
			if( i % 1000 == 1)
				System.out.println("Processed = %" + 100.0 * i / tuplePairs.size() );
			TuplePair_WithEqualityConstraints pair_i = tuplePairs.get(i); 
			for( int j = i+1; j < tuplePairs.size(); j++  ) { 
				TuplePair_WithEqualityConstraints pair_j = tuplePairs.get(j); 
				if( pair_i.equals(pair_j) ) { 
					// System.out.println("We have a duplication!!");
					System.out.println(" \n ------------------------------------- \n Pair i = \n " + pair_i.toString() );
					System.out.println("\n ------------------------------------- \n Pair j = \n " + pair_j.toString() );					
					numDuplicates++; 
				}
			}
		} 
		System.out.println("Total duplications = " + numDuplicates);
	}
	
	// this function calculates the probabilities  
	public void calculateDenominatorCounts() { 
		
		Map<Tuple_WithEqualityConstraints, int[]> counts_first_tuple_directed = new HashMap<Tuple_WithEqualityConstraints, int[]> (); 
		Map<Tuple_WithEqualityConstraints, int[]> counts_first_tuple_dunirected = new HashMap<Tuple_WithEqualityConstraints, int[]> (); 
		Map<Tuple_WithEqualityConstraints, int[]> counts_second_tuple_directed = new HashMap<Tuple_WithEqualityConstraints, int[]> (); 
		Map<Tuple_WithEqualityConstraints, int[]> counts_second_tuple_dunirected = new HashMap<Tuple_WithEqualityConstraints, int[]> (); 
		
		for( int i = 0; i < tuplePairs.size(); i++ ) {
			if( i % 1000 == 10 )
			{ 
				System.out.println("Processed = % " + 100.0 * i / tuplePairs.size() ); 
				System.out.println("Directed num = " + counts_first_tuple_directed.size());
				System.out.println("Undirected num = "+ counts_second_tuple_directed.size());
				//break; 
			}
			TuplePair_WithEqualityConstraints pair_i = tuplePairs.get(i);  
			// check if it already has probabilities 
			if( !counts_first_tuple_directed.containsKey(pair_i.tuple1) ) { 
				int[] count_sum_directed = new int[7]; 
				int[] count_sum_undirected = new int[7]; 
				for( int j = i; j < tuplePairs.size(); j++ ) { 
					TuplePair_WithEqualityConstraints pair_j = tuplePairs.get(j);  
					if( pair_i.tuple1.equals(pair_j.tuple1) ) { 
						for( int l = 0; l < 7; l++) { 
							count_sum_directed[l] += pair_j.counts_directed[l]; 
							count_sum_undirected[l] += pair_j.counts_undirected[l]; 
						}
					}
				}
				counts_first_tuple_directed.put( pair_i.tuple1, count_sum_directed ); 
				counts_first_tuple_dunirected.put( pair_i.tuple1, count_sum_undirected ); 
			}
			
			// check again for the second argument 
			if( !counts_second_tuple_directed.containsKey(pair_i.tuple2) ) { 
				int[] count_sum_directed = new int[7]; 
				int[] count_sum_undirected = new int[7]; 
				for( int j = i; j < tuplePairs.size(); j++ ) { 
					TuplePair_WithEqualityConstraints pair_j = tuplePairs.get(j);  
					if( pair_i.tuple2.equals(pair_j.tuple2) ) { 
						for( int l = 0; l < 7; l++) { 
							count_sum_directed[l] += pair_j.counts_directed[l]; 
							count_sum_undirected[l] += pair_j.counts_undirected[l]; 
						}
					}
				}
				counts_second_tuple_directed.put( pair_i.tuple2, count_sum_directed ); 
				counts_second_tuple_dunirected.put( pair_i.tuple2, count_sum_undirected ); 
			}
		}
		
		// save to mapdb 
		DB db = DBMaker.newFileDB(new File(serializationFolder + "denominators" + "_mapdb.bin" ))
				//.closeOnJvmShutdown()
				.make();
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_first_tuple_directed = db.getHashMap("counts_first_tuple_directed");
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_first_tuple_undirected = db.getHashMap("counts_first_tuple_dunirected");
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_second_tuple_directed = db.getHashMap("counts_second_tuple_directed");
		Map<Tuple_WithEqualityConstraints, int[]> myMap_counts_second_tuple_undirected = db.getHashMap("counts_second_tuple_dunirected");

		for( Tuple_WithEqualityConstraints key : counts_first_tuple_directed.keySet() ) { 
			myMap_counts_first_tuple_directed.put( key, counts_first_tuple_directed.get(key) ); 
			myMap_counts_first_tuple_undirected.put( key, counts_first_tuple_dunirected.get(key) ); 
		}
		
		for( Tuple_WithEqualityConstraints key : counts_second_tuple_directed.keySet() ) { 
			myMap_counts_second_tuple_directed.put( key, counts_second_tuple_directed.get(key) ); 
			myMap_counts_second_tuple_undirected.put( key, counts_second_tuple_dunirected.get(key) ); 
		}
		db.commit();
		db.close();
	}
	
	public void ReadFile(String file) { 
		//int minSize = 20; 
		
		tuplePairs = new ArrayList<TuplePair_WithEqualityConstraints>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = br.readLine()) != null) {
				TuplePair_WithEqualityConstraints tp = new TuplePair_WithEqualityConstraints(); 
				Tuple_WithEqualityConstraints t1 = new Tuple_WithEqualityConstraints(); 
				Tuple_WithEqualityConstraints t2 = new Tuple_WithEqualityConstraints(); 
				
				// System.out.println("line = " + line );
				
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
				if(tokens[5].contains("XVAR"))
				{ 
					t2.arg1_XVAR = true;
					tokens[5] = tokens[5].replace("XVAR:", ""); 
				}
				if(tokens[5].contains("YVAR"))
				{ 
					t2.arg1_YVAR = true;
					tokens[5] = tokens[5].replace("YVAR:", ""); 
				}
				// T2: arg2 
				if(tokens[8].contains("XVAR"))
				{ 
					t2.arg2_XVAR = true;
					tokens[8] = tokens[8].replace("XVAR:", ""); 
				}
				if(tokens[8].contains("YVAR"))
				{ 
					t2.arg2_YVAR = true;
					tokens[8] = tokens[8].replace("YVAR:", ""); 
				}
				//System.out.println("number of tokens = " + tokens.length); 
				t1.arg1_type = tokens[0]; 
				t1.arg1_surface = tokens[1];
				t1.relation = tokens[2]; 
				t1.arg2_type = tokens[3]; 
				t1.arg2_surface = tokens[4];
				t2.arg1_type = tokens[5]; 
				t2.arg1_surface = tokens[6];
				t2.relation = tokens[7]; 
				t2.arg2_type = tokens[8]; 
				t2.arg2_surface = tokens[9];
				
				tp.tuple1 = t1; 
				tp.tuple2 = t2; 
				String[] counts1_toks = tokens[10].split(","); 
				String[] counts2_toks = tokens[11].split(","); 
				
				tp.counts_directed = new long[7];
				tp.counts_undirected = new long[7];
				//System.out.println("number of counts1_toks = " + counts1_toks.length); 
				//System.out.println("number of counts2_toks = " + counts2_toks.length); 
				for(int i = 0; i < 7; i++) { 
				    tp.counts_undirected[i] = Integer.parseInt(counts1_toks[i]);
				    tp.counts_directed[i] = Integer.parseInt(counts2_toks[i]);
				} 
				//System.out.println(line); 
				//System.out.println( tp.toString() + "\n" ); 
				//System.out.println( tp.getCountsString() + "\n" ); 
				tuplePairs.add(tp); 				
			}
			br.close();
		}catch(Exception e)
		{
			System.out.println("-->  oh no! exception thrown! \n " + e.getMessage() + "\n"); 	
		} //or write your own exceptions
	}
	
	public void saveToMapDB() { 
		
		System.out.println("Starting to save all of the pairs = " + tuplePairs.size() );
		
		// create mapdb 
		DB db = DBMaker.newFileDB(new File(serializationFolder + "allInstances_rgc_equality_withProbabilities" + "_mapdb.bin" ))
				//.closeOnJvmShutdown()
				.make();
		
		// Create a Map:
		//Map<Tuple_comparable,long[]> myMap = db.getTreeMap("testmap");
		Map<TuplePair_WithEqualityConstraints,TuplePair_WithEqualityConstraints> myMap = db.getHashMap("themap");
		
		int i = 0; 
		for( int iter = 0; iter < tuplePairs.size(); iter++ ) { 
			
			myMap.put(tuplePairs.get(i), tuplePairs.get(i));
			
			i++; 
			if( i % 1000 == 10)
			{ 
				System.out.println( "Proccessed = % " + 100.0 * i / tuplePairs.size()  + "  processed  = " + i + "  out of = " + tuplePairs.size() );
				System.out.println( "After myMap.size() = " + myMap.size() );
			}
		}
		db.commit();
		System.out.println("Size of the map = " + myMap.size()); 
		db.close();
	}
}
