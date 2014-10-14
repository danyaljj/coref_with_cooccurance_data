package edu.illinois.cs.cogcomp.cooccurancedata.unittest.relgram;

import java.io.File;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.TuplePair_WithEqualityConstraints;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram.Tuple_WithEqualityConstraints;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.RelgramReader;

public class RelgramReaderTest {
	static String serializationFolder = "/shared/shelley/khashab2/SummerProject/JSON"; 

	public static void main(String[] args) { 
		testReadFromMapDB();
		//testReadAndSaveToMapDB();
		//testForDuplications();
		//testCalculatingDenominators();
		// testDenominators();
		//testReadAddProbsAndSaveToMapDB(); 
	}
	
	// this shows how to read from mapDB 
	public static void testReadFromMapDB() { 
		
		// create mapdb 
/*		DB db = DBMaker.newFileDB(new File(serializationFolder + "allInstances_rgc_equality" + "_mapdb.bin" ))
				.readOnly()
				.make();
*/
		DB db = DBMaker.newFileDB(new File(serializationFolder + "allInstances_rgc_equality_withProbabilities" + "_mapdb.bin" ))
				.readOnly()
				.make();
		
		// Create a Map:
		Map<TuplePair_WithEqualityConstraints,TuplePair_WithEqualityConstraints> myMap = db.getHashMap("themap");
		System.out.println("Size of the map = " + myMap.size()); 
		int iter = 0; 
		for( TuplePair_WithEqualityConstraints ins : myMap.keySet() ) { 
			iter++; 
			if( iter > 30) 
				break; 
			System.out.println( "Pairs = \n" + ins.toString() );
			System.out.println( "Counts = \n" + ins.getCountsString() );
			System.out.println( "Probabilities : forward = \n" + ins.getForwardProbabilitiesString() );
			System.out.println( "Probabilities : backward = \n" + ins.getBackwardProbabilitiesString() );			
		}
	}
	
	// this shows how to read from raw file and save to mapDB
	public static void testCalculatingDenominators() { 
		System.out.println("A sanple msg!"); 
		RelgramReader rd = new RelgramReader();
		try {
			rd.ReadAllFiles();
			rd.calculateDenominatorCounts();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testDenominators() { 
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
		
		// create mapdb 
		DB db2 = DBMaker.newFileDB(new File(serializationFolder + "allInstances_rgc_equality" + "_mapdb.bin" ))
				.readOnly()
				.make();
		
		Map<TuplePair_WithEqualityConstraints,TuplePair_WithEqualityConstraints> myMap = db2.getHashMap("themap");
		
		/*
		// each probability separately 
		for( TuplePair_WithEqualityConstraints key : myMap.keySet() ) { 
			if( myMap_counts_first_tuple_directed.containsKey( key.tuple1 ) ) {
				//System.out.print("\n probability transition (directed) = " );					
				int[] countDirected = myMap_counts_first_tuple_directed.get(key.tuple1); 
				int[] countUndirected = myMap_counts_first_tuple_undirected.get(key.tuple1); 
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * key.counts_directed[i] / countDirected[i]; 
					if(score > 1)
					System.out.print( score	 + ", " ); 
				}
				
				//System.out.print("\n probability transition (directed) = " );					
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * key.counts_undirected[i] / countUndirected[i]; 
					if(score > 1)
					System.out.print( score + ", " ); 
				}
			}
		}
		
		for( TuplePair_WithEqualityConstraints key : myMap.keySet() ) { 
			if( myMap_counts_first_tuple_directed.containsKey( key.tuple1 ) ) {
				//System.out.print("\n probability transition (directed) = " );					
				int[] countDirected = myMap_counts_first_tuple_directed.get(key.tuple1); 
				int[] countUndirected = myMap_counts_first_tuple_undirected.get(key.tuple1); 
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * key.counts_directed[i] / countDirected[i]; 
					if(score > 1)
					System.out.print( score + ", " ); 
				}
				
				//System.out.print("\n probability transition (directed) = " );					
				for( int i = 0; i < 7; i++) {
					double score = 1.0 * key.counts_undirected[i] / countUndirected[i]; 
					if(score > 1)
					System.out.print( score + ", " ); 
				}
			}
		}
		*/
		
		// and they should also sum to one --> and they do! 
		/*
		for( Tuple_WithEqualityConstraints tuple1 : myMap_counts_first_tuple_directed.keySet() ) { 
			int[] countDirected = myMap_counts_first_tuple_directed.get(tuple1); 
			int[] countUndirected = myMap_counts_first_tuple_undirected.get(tuple1); 
			double[] probSum = new double[7]; 
			double[] probSum_undirected = new double[7]; 
			
			for( TuplePair_WithEqualityConstraints key : myMap.keySet() ) { 
				if( tuple1.equals( key.tuple1 ) ) {
					for( int i = 0; i < 7; i++) {  
						if( key.counts_directed[i] != 0 && countDirected[i] == 0 )
							System.out.println("denom is zero but numerator is not!!  [" + i + "] ");
						//else if( key.counts_directed[i] == 0 && countDirected[i] == 0 )
						//	System.out.println("Both zero [" + i + "] !");	
						probSum[i] += 1.0 * key.counts_directed[i] / countDirected[i];
						probSum_undirected[i] += 1.0 * key.counts_undirected[i] / countUndirected[i];
					}
				} 	
			}
			System.out.println("Probability sum (directed) = "); 
			for( int i = 0; i < 7; i++) {  
				System.out.print( probSum[i] + ", "); 
			}
			System.out.println(); 
			System.out.println("Probability sum (undirected) = "); 
			for( int i = 0; i < 7; i++) {  
				System.out.print( probSum_undirected[i] + ", "); 
			}
			System.out.println(); 
		}	*/
		
		
		for( Tuple_WithEqualityConstraints tuple2 : myMap_counts_second_tuple_directed.keySet() ) { 
			int[] countDirected = myMap_counts_second_tuple_directed.get(tuple2); 
			int[] countUndirected = myMap_counts_second_tuple_undirected.get(tuple2); 
			double[] probSum = new double[7]; 
			double[] probSum_undirected = new double[7]; 
	
			for( TuplePair_WithEqualityConstraints key : myMap.keySet() ) { 
				if( tuple2.equals( key.tuple2 ) ) {
					for( int i = 0; i < 7; i++) {  
						if( (key.counts_undirected[i] - key.counts_directed[i]) != 0 && countDirected[i] == 0 )
							System.out.println("denom is zero but numerator is not!!  [" + i + "] ");

						//if( (key.counts_undirected[i] - key.counts_directed[i]) == 0 && countDirected[i] != 0 )
						//	System.out.println("denom is zero but numerator is not!!  [" + i + "] ");

						//else if( key.counts_directed[i] == 0 && countDirected[i] == 0 )
						//	System.out.println("Both zero [" + i + "] !");	
						probSum[i] += 1.0 * ( key.counts_undirected[i] - key.counts_directed[i]) / ( countUndirected[i] - countDirected[i]  );
						probSum_undirected[i] += 1.0 * key.counts_undirected[i] / countUndirected[i];
					}
				} 	
			}
			System.out.println("Probability sum (directed) = "); 
			for( int i = 0; i < 7; i++) {  
				System.out.print( probSum[i] + ", "); 
			}
			System.out.println(); 
			System.out.println("Probability sum (undirected) = "); 
			for( int i = 0; i < 7; i++) {  
				System.out.print( probSum_undirected[i] + ", "); 
			}
			System.out.println(); 
		}
	}
	
	// this shows how to read from raw file and save to mapDB
	public static void testReadAndSaveToMapDB() { 
		System.out.println("A sanple msg!"); 
		RelgramReader rd = new RelgramReader();
		try {
			rd.ReadAllFiles();
			rd.saveToMapDB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// this shows how to read from raw file and save to mapDB
	public static void testReadAddProbsAndSaveToMapDB() { 
		System.out.println("A sanple msg!"); 
		RelgramReader rd = new RelgramReader();
		try {
			rd.ReadAllFiles();
			rd.addProbabilities();
			rd.saveToMapDB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// this shows how to read from raw file and save to mapDB
	public static void testForDuplications() { 
		System.out.println("A sanple msg!"); 
		RelgramReader rd = new RelgramReader();
		try {
			rd.ReadAllFiles();
			rd.AnyDuplications();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
