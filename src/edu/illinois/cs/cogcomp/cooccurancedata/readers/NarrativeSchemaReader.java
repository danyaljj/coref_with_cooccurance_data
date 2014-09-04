package edu.illinois.cs.cogcomp.cooccurancedata.readers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaRoles;
import edu.illinois.cs.cogcomp.core.datastructures.Pair;

public class NarrativeSchemaReader {
	
	static String schema_file_06 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size6"; 
	static String schema_file_08 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size8";
	static String schema_file_10 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size10";
	static String schema_file_12 = "/shared/experiments/hpeng7/data/NarrativeSchema/schemas-size12";
	static String verb_ordering_file = "/shared/experiments/hpeng7/data/NarrativeSchema/ordering.lemmas.closed.nones0.5.stats";
	static String cache_file_schemas = "/shared/experiments/hpeng7/data/NarrativeSchema/cached_data_schemas.bin";
	static String cache_file_verb_order = "/shared/experiments/hpeng7/data/NarrativeSchema/cached_data_verb_order.bin";
	
	public ArrayList<NarrativeSchemaInstance> readSchema(int len) throws Exception {
		String file="";
		if (len==6) file=schema_file_06;
		if (len==8) file=schema_file_08;
		if (len==10) file=schema_file_10;
		if (len==12) file=schema_file_12;
		if (file.equals("")) {
			System.out.println("NarrativeSchemaReader Error: Wrong parameter!");
			return null;
		}
		
		int sum=0;
		ArrayList<NarrativeSchemaInstance> allInstances=new ArrayList<NarrativeSchemaInstance>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line=br.readLine();
		NarrativeSchemaInstance ins=null;
		while (line != null) {
			if (line.startsWith("*")) {
				ins=new NarrativeSchemaInstance();
			}
			if (line.startsWith("score=")) {
				ins.length=len;
				String str=line.substring(6);
				while (str.charAt(str.length()-1)=='.') str=str.substring(0,str.length()-1);
				ins.generalScore=Double.parseDouble(str);
			}
			if (line.startsWith("Events:")) {
				String[] strs=line.substring(8).split(" ");
				if (strs.length!=len) System.out.println("NarrativeSchemaReader Error: Events length not match!");
				ins.events=strs;
			}
			if (line.startsWith("Scores:")) {
				String[] strs=line.substring(8).split(" ");
				if (strs.length!=len) System.out.println("NarrativeSchemaReader Error: EventScores length not match!");
				ins.eventScores=new double[len];
				for (int i=0;i<len;i++) {
					ins.eventScores[i]=Double.parseDouble(strs[i]);
				}
			}
			if (line.startsWith("[")) {
				NarrativeSchemaRoles schema=new NarrativeSchemaRoles();
				String roleLine=line.substring(line.indexOf('[')+1, line.indexOf(']'));roleLine=roleLine.trim();
				String[] rolePair=roleLine.split(" ");
				for (int i=0;i<rolePair.length;i++) {
					String str=rolePair[i];
					int p=str.indexOf('-');
					schema.roles.add(str.substring(0,p));
					schema.roleTypes.add(str.substring(p+1));
				}
				schema.roleSize=schema.roles.size();
				String headLine=line.substring(line.indexOf('(')+1, line.indexOf(')'));headLine=headLine.trim();
				if (!headLine.equals("")) {
					String[] headPair=headLine.split(" ");
					if (headPair.length%2!=0) System.out.println("NarrativeSchemaReader Error: HeadScores length not match!");
					for (int i=0;i<headPair.length;i=i+2) {
						schema.heads.add(headPair[i]);
						schema.headScores.add(Double.parseDouble(headPair[i+1]));
					}
				}
				schema.headSize=schema.heads.size();
				ins.roleVecs.add(schema);
			}
			if (line.length()==0) {
				allInstances.add(ins);
				sum++;
			}
			line=br.readLine();
		}
		System.out.println("NarrativeSchema Size:"+sum);
		return allInstances;
	}
	
	public HashMap<Pair<String,String>,Integer> readVerbOrdering() throws Exception {
		HashMap<Pair<String,String>,Integer> verbOrderMap=new HashMap<Pair<String,String>,Integer>();
		BufferedReader br = new BufferedReader(new FileReader(verb_ordering_file));
		String line=br.readLine();
		while (line != null) {
			String[] strs=line.split("\t");
			verbOrderMap.put(new Pair<String, String>(strs[0],strs[1]), Integer.parseInt(strs[2]));
			line=br.readLine();
		}
		System.out.println("VerbOrderMap Size:"+verbOrderMap.size());
		return verbOrderMap;
	}
	
	// saving on disk 
	public void serializeSchemaData(ArrayList<NarrativeSchemaInstance> allInstances) throws Exception { 
		System.out.printf("Starting serialization! "); 
		FileOutputStream fileOut = new FileOutputStream(cache_file_schemas);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(allInstances);
		out.close();
		fileOut.close();
		System.out.printf("Serialized data is saved in " + cache_file_schemas);
	}

	/// reading from disk 
	public ArrayList<NarrativeSchemaInstance> deserializeSchemaData() throws Exception {
		ArrayList<NarrativeSchemaInstance> allInstances;
		System.out.printf("Starting deserialization! ");
		FileInputStream fileIn = new FileInputStream(cache_file_schemas);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		allInstances = (ArrayList<NarrativeSchemaInstance>) in.readObject();
		System.out.printf("Done reading the data from " + cache_file_schemas);
		in.close();
		fileIn.close();
		return allInstances;
	}
	
	// saving on disk 
	public void serializeVerbOrderData(HashMap<Pair<String,String>,Integer> verbOrderMap) throws Exception { 
		System.out.printf("Starting serialization! "); 
		FileOutputStream fileOut = new FileOutputStream(cache_file_verb_order);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(verbOrderMap);
		out.close();
		fileOut.close();
		System.out.printf("Serialized data is saved in " + cache_file_verb_order);
	}

	/// reading from disk 
	public HashMap<Pair<String,String>,Integer> deserializeVerbOrderData() throws Exception {
		HashMap<Pair<String,String>,Integer> verbOrderMap;
		System.out.printf("Starting deserialization! ");
		FileInputStream fileIn = new FileInputStream(cache_file_verb_order);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		verbOrderMap = (HashMap<Pair<String,String>,Integer>) in.readObject();
		System.out.printf("Done reading the data from " + cache_file_verb_order);
		in.close();
		fileIn.close();
		return verbOrderMap;
	}
}
