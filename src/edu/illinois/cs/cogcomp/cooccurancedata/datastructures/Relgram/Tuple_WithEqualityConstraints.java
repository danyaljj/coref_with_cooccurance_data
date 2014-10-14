package edu.illinois.cs.cogcomp.cooccurancedata.datastructures.Relgram;

import java.io.Serializable;

//import edu.illinois.cs.cogcomp.cooccurancedata.readers.str;
/**
 * @author khashab2
 *
 */
public class Tuple_WithEqualityConstraints implements Serializable, Comparable<Tuple_WithEqualityConstraints>{

	public String arg1_headnoun;
	public String arg1_surface;
	public String arg1_type; 
	public boolean arg1_XVAR;
	public boolean arg1_YVAR;
	public String relation; 
	public String arg2_surface; 
	public String arg2_headnoun; 
	public String arg2_type;
	public boolean arg2_type_equals; 
	public boolean arg2_XVAR;
	public boolean arg2_YVAR;
	public Long count;
	
	@Override 
	public boolean equals(Object o) { 
        if (!(o instanceof Tuple_WithEqualityConstraints))
            return false;
        Tuple_WithEqualityConstraints oo = (Tuple_WithEqualityConstraints)o; 
        if( oo.arg1_type.length() != this.arg1_type.length() )
        	return false; 
        if( oo.relation.length() != this.relation.length() )
        	return false; 
        if( oo.arg2_type.length() != this.arg2_type.length() )
        	return false; 
        if( oo.arg1_XVAR != this.arg1_XVAR )
        	return false;         	
        if( oo.arg2_XVAR != this.arg2_XVAR )
        	return false;         	
        if( oo.arg1_YVAR != this.arg1_YVAR )
        	return false;         	
        if( oo.arg2_YVAR != this.arg2_YVAR )
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
	public String toString() { 
		String str = ""; 
		str += "Arg1-Surface = " + arg1_surface 
				+ ", Head = " + arg1_headnoun 
				+ ", Arg1-type = " + arg1_type
				+ ", arg1_XVAR = " + arg1_XVAR 
				+ ", arg1_YVAR = " + arg1_YVAR 
				+ ", Relation = " + relation 
				+ ", Arg2-Surface = " +  arg2_surface 
				+ ", Arg2-Head = " + arg2_headnoun 
				+ ", Arg2-type = " + arg2_type 				
				+ ", arg2_XVAR = " + arg2_XVAR 
				+ ", arg2_XVAR = " + arg2_YVAR;
		return str; 
	}
		
	@Override
	public int hashCode() { 
        return 3 * this.arg1_type.length() + 7 * this.relation.length() + 13 * this.arg2_type.length(); 
	}

	@Override
	public int compareTo(Tuple_WithEqualityConstraints o) {
		// TODO Auto-generated method stub
		return 1;
	}
}
