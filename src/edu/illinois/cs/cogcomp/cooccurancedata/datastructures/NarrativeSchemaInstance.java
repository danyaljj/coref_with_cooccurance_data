package edu.illinois.cs.cogcomp.cooccurancedata.datastructures;

import java.io.Serializable;
import java.util.Vector;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;

public class NarrativeSchemaInstance implements Serializable {
	public int length;
	public double generalScore;
	public String[] events;
	public double[] eventScores; 
	
	public Vector<NarrativeSchemaRoles> roleVecs;
	
	public NarrativeSchemaInstance() {
		roleVecs=new Vector<NarrativeSchemaRoles>();
	}
	
	public Pair<Integer, Integer> checkPredicatepair(String verb1, String verb2) {
		// Only one direction, situation where verb1 == verb2 is not dealt with
		// NarrativeSchemaRoles.roles are not the same order with events
		// events contain "_"
		boolean flag=false;
		Pair<Integer, Integer> index=new Pair<Integer, Integer>(0, 0);
		for (int i=0;i<length;i++) {
			if (events[i].equalsIgnoreCase(verb1)) {
				for (int j=i+1;j<length;j++) {
					if (events[j].equalsIgnoreCase(verb2)) {
						flag=true;
						index.setFirst(i);
						index.setSecond(j);
						break;
					}
				}
			}
			if (flag) break;
		}
		if (flag) return index; 
		else return null;
	}
}


