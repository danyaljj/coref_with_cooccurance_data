package edu.illinois.cs.cogcomp.cooccurancedata.printing;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaRoles;

public class printingNarrativeSchemaInstance {
	public static void print(NarrativeSchemaInstance ins) {
		System.out.println(ins.length); 
		System.out.println(ins.generalScore); 
		for (int i=0;i<ins.events.length;i++)
			System.out.print(ins.events[i]+" ");
		System.out.println();
		for (int i=0;i<ins.eventScores.length;i++)
			System.out.print(ins.eventScores[i]+" ");
		System.out.println();
		for (int i=0;i<ins.roleVecs.size();i++) {
			NarrativeSchemaRoles roles=ins.roleVecs.get(i);
			for (int j=0;j<roles.roleSize;j++)
				System.out.print(roles.roles.get(j)+"-"+roles.roleTypes.get(j)+" ");
			for (int j=0;j<roles.headSize;j++)
				System.out.print(roles.heads.get(j)+" "+roles.headScores.get(j)+" ");
			System.out.println();
		}
	}
}