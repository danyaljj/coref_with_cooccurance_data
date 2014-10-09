package edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram;

import java.io.Serializable;
/**
 * @author khashab2
 *
 */
public class Tuple_comparable implements Serializable, Comparable<Tuple_comparable> {

	public String arg1_surface;
	public String arg1_type; 
	public boolean arg1_type_equals; 
	public String relation; 
	public String arg2_surface; 
	public String arg2_type; 
	public boolean arg2_type_equals; 
	public Long count; 
	
	@Override 
	public boolean equals(Object o) { 
        if (!(o instanceof Tuple_comparable))
            return false;
        Tuple_comparable oo = (Tuple_comparable)o; 
        if( oo.arg1_type.length() != this.arg1_type.length() )
        	return false; 
        if( oo.relation.length() != this.relation.length() )
        	return false; 
        if( oo.arg2_type.length() != this.arg2_type.length() )
        	return false; 
        if( !oo.arg1_type.equals( this.arg1_type ) )
        	return false; 
        if( !oo.arg2_type.equals( this.arg2_type ) )
        	return false; 
        if( !oo.relation.equals( this.relation ) )
        	return false;         	
        return true; 
	}
	
	@Override
	public int hashCode() { 
        return this.arg1_type.length() + this.relation.length() + this.arg2_type.length(); 
	}

	@Override
	public int compareTo(Tuple_comparable o) {
		// TODO Auto-generated method stub
		return 1;
	}
}
