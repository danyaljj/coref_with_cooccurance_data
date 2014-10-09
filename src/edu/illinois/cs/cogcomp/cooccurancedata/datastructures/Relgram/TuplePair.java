package edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram;

import java.io.Serializable;
/**
 * @author khashab2
 *
 */
public class TuplePair implements Serializable  {
	public Tuple tuple1; 
	public Tuple tuple2; 
	public long[] counts_undirected; 
	public long[] counts_directed; 
	public double[] forward_probablities_directed; 
	public double[] forward_probablities_undirected; 
	public double[] backward_probablities_directed; 
	public double[] backward_probablities_undirected; 	
}
