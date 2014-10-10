package edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram;

import java.io.Serializable;

//import edu.illinois.cs.cogcomp.cooccurancedata.readers.str;
/**
 * @author khashab2
 *
 */
public class TuplePair_WithEqualityConstraints implements Serializable, Comparable<TuplePair_WithEqualityConstraints>  {
	public Tuple_WithEqualityConstraints tuple1; 
	public Tuple_WithEqualityConstraints tuple2; 
	public long[] counts_undirected; 
	public long[] counts_directed; 
	public double[] forward_probablities_directed; 
	public double[] forward_probablities_undirected; 
	public double[] backward_probablities_directed; 
	public double[] backward_probablities_undirected; 	
	
	@Override
	public String toString() { 
		String str = ""; 
		str += "-----------------------------------\n"; 
		str += tuple1.toString() + "\n"; 
		str += tuple2.toString() + "\n"; 
		str += "-----------------------------------\n"; 		
		return str; 
	}
	
	public String getBackwardProbabilitiesString() { 
		String str = ""; 
		str += "-----------------------------------\n"; 
		String directed = ""; 
		String undirected = ""; 
		for( int i = 0; i < 7; i++) { 
			directed += Double.toString(backward_probablities_directed[i]) + ", "; 
			undirected += Double.toString(forward_probablities_undirected[i]) + ", ";
		}
		str += directed + "\n"; 
		str += undirected + "\n"; 
		str += "-----------------------------------\n"; 		
		return str; 
	}
	
	public String getForwardProbabilitiesString() { 
		String str = ""; 
		str += "-----------------------------------\n"; 
		String directed = ""; 
		String undirected = ""; 
		for( int i = 0; i < 7; i++) { 
			directed += Double.toString(forward_probablities_directed[i]) + ", "; 
			undirected += Double.toString(backward_probablities_undirected[i]) + ", ";
		}
		str += directed + "\n"; 
		str += undirected + "\n"; 
		str += "-----------------------------------\n"; 		
		return str; 
	}

	public String getCountsString() { 
		String str = ""; 
		str += "-----------------------------------\n"; 
		String directed = ""; 
		String undirected = ""; 
		for( int i = 0; i < 7; i++) { 
			directed += Long.toString(counts_directed[i]) + ", "; 
			undirected += Long.toString(counts_undirected[i]) + ", ";			
		}
		str += directed + "\n"; 
		str += undirected + "\n"; 
		str += "-----------------------------------\n"; 		
		return str; 
	}

	@Override 
	public boolean equals(Object o) { 
        if (!(o instanceof TuplePair_WithEqualityConstraints))
            return false;
        TuplePair_WithEqualityConstraints oo = (TuplePair_WithEqualityConstraints)o;        
        if( !oo.tuple1.equals(this.tuple1) )
        	return false; 
        if( !oo.tuple2.equals(this.tuple2) )
        	return false; 
        return true; 
	}
	
	@Override
	public int hashCode() { 
        return this.tuple1.relation.length() * 3 + this.tuple1.arg1_type.length() * 5 + this.tuple1.arg2_type.length() * 7 +
        		this.tuple2.relation.length() * 11 + this.tuple2.arg1_type.length() * 13 + this.tuple2.arg2_type.length() * 19; 
	}

	@Override
	public int compareTo(TuplePair_WithEqualityConstraints o) {
		// TODO Auto-generated method stub
		return 1;
	}
}
