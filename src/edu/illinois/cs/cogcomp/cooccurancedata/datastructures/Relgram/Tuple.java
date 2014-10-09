package edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram;

import java.io.Serializable;
/**
 * @author khashab2
 *
 */
public class Tuple implements Serializable {

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
        if (!(o instanceof Tuple))
            return false;
        Tuple oo = (Tuple)o; 
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
}
