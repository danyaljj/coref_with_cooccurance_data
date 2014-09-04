package edu.illinois.cs.cogcomp.cooccurancedata.datastructures;

import java.io.Serializable;
import java.util.Vector;

public class NarrativeSchemaRoles implements Serializable {
	public Vector<String> roles;
	public Vector<String> roleTypes;
	public int roleSize;
	public Vector<String> heads;
	public Vector<Double> headScores;
	public int headSize;
	
	public NarrativeSchemaRoles() {
		roles=new Vector<String>();
		roleTypes=new Vector<String>();
		heads=new Vector<String>();
		headScores=new Vector<Double>();
	}
}