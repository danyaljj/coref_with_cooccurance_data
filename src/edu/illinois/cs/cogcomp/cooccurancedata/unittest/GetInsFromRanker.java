package edu.illinois.cs.cogcomp.cooccurancedata.unittest;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Vector;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.IOManager;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

public class GetInsFromRanker {
	public static void main(String[] args) throws Exception{
/*
		PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
		pr.deserializeData2();
		
		ArrayList<String> preds = IOManager.readLines("results/ranker_no_schema/svm_predictions_train");
		ArrayList<String> golds = IOManager.readLines("results/ranker_no_schema/train.txt");
		
		Vector<WinogradCorefInstance2> vec=new Vector<WinogradCorefInstance2>();
		for (WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations) {
			if (ins.test_or_train==1) {
				vec.add(ins);
			}
		}
		
		for (int i=0;i<preds.size();i=i+2) {
			int a=0,b=0;
			if (Double.parseDouble(preds.get(i))>Double.parseDouble(preds.get(i+1))) {
				a=2;
				b=1;
			}
			else {
				a=1;
				b=2;
			}
			int c=Integer.parseInt(golds.get(i).split(" ")[0]);
			int d=Integer.parseInt(golds.get(i+1).split(" ")[0]);
			if (a==c && b==d) continue;
			System.out.println(vec.get(i).sentence);
			System.out.println(vec.get(i+1).sentence);
			System.out.println();
		}
*/
		ArrayList<String> lines = IOManager.readLines("results/illinois_coref/test.c.txt");
		
		int p=1;
		BufferedWriter bw=null;
		for (int i=0;i<lines.size();i++) {
			if (i%10==0) {
				bw=IOManager.openWriter("results/illinois_coref/test_"+p+".txt");
				bw.write(lines.get(i)+"\n");
			}
			if ((i+5)%10==0) {
				bw.write(lines.get(i)+"\n");
				bw.close();
				p++;
			}
		}
	} 
}
