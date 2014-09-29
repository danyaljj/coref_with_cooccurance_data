package edu.illinois.cs.cogcomp.cooccurancedata;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightModel;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.TrainingParameters;

public class JNI_SVMLight_Test {
 
	public static int N = 0;

	public static int M = 0;

	public static void main(String[] args) throws Exception {
	  PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
	  pr.deserializeData2();
	  // feature pre-processor 
	  FeaturePreprocessor fp = new FeaturePreprocessor( pr.allInstances_withAntecedentAnnotations );
	  fp.Process(); 
	  // the size of the verbs must match the number of the instances 
	  System.out.println( "pr.allInstances_withAntecedentAnnotations.size() = " + pr.allInstances_withAntecedentAnnotations.size() ); 
	  System.out.println( "fp.antVerbIndex.size() = " + fp.antVerbIndex.size() ); 
	  System.out.println( "fp.pronounVerbIndex.size() = " + fp.pronounVerbIndex.size() ); 
	  assert(pr.allInstances_withAntecedentAnnotations.size() == fp.antVerbIndex.size()); 
	  assert(pr.allInstances_withAntecedentAnnotations.size() == fp.pronounVerbIndex.size()); 
	  
	  for (WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations) {
		  if (ins.test_or_train==0) N++;
	  }
	  
	  SVMLightInterface trainer = new SVMLightInterface();
	  // The training data
	  LabeledFeatureVector[] traindata = new LabeledFeatureVector[N];
      // Sort all feature vectors in ascedending order of feature dimensions
      // before training the model
      SVMLightInterface.SORT_INPUT_VECTORS = true;
      
	  int train_num = 0;
	  FeatureExtractor fe = new FeatureExtractor();
	  fe.setPreprocessor(fp); 
	  fe.setTheVerbIndices(); 
	  for (WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations) {
		  if (ins.test_or_train==0) {
		      traindata[train_num] = getFeatureVector(ins,fe,train_num);
		      train_num++;
		  }
	  }

	  // Initialize a new TrainingParamteres object with the default SVM-light values
	  TrainingParameters tp = new TrainingParameters();
	  // Switch on some debugging output
	  tp.getLearningParameters().verbosity = 1;
	  
	  System.out.println("\nTRAINING SVM-light MODEL ..");
	  SVMLightModel model = trainer.trainModel(traindata, tp);
	  System.out.println(" DONE.");

	  // Use this to store a model to a file or read a model from a URL.
	  //model.writeModelToFile("jni_model.dat");
	  //model = SVMLightModel.readSVMLightModelFromURL(new java.io.File("jni_model.dat").toURL());
    
	  int precision = 0;
	  for (int i = 0; i < N; i++) {
		  double d = model.classify(traindata[i]);
		  if ((traindata[i].getLabel() < 0 && d < 0) || (traindata[i].getLabel() > 0 && d > 0)) {
			  precision++;
		  }
	  }
	  System.out.println("\n" + ((double) precision / N) + " PRECISION on training data");
	  
      precision = 0;
      int test_num=0;
      for (WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations) {
		  if (ins.test_or_train==1) {
			  LabeledFeatureVector test_data=getFeatureVector(ins,fe,test_num);
			  double a = model.classify(test_data);
			  int b = ins.getLabel();
			  test_num++;
		  }
      }
      System.out.println("\n" + ((double) precision / test_num) + " PRECISION on training data");
	}

    //TODO Verb normalization for Narrative Schema
	
	public static LabeledFeatureVector getFeatureVector(WinogradCorefInstance2 ins, FeatureExtractor fe, int instance_num) throws Exception {
	  fe.setInstance(ins); 
	  fe.setInstanceNumber(instance_num); 
	  fe.extractHeadNoun();
	  fe.extractConnective(); 
	  int[] featureVector = fe.Extract();
	  
	  int dim=0;
	  for (int i=0;i<featureVector.length;i++) {
		  if (featureVector[i]!=0) dim++;
	  }
      int[] dims = new int[dim];
      double[] values = new double[dim];
      int p=0;
	  for (int i=0;i<featureVector.length;i++) {
		  if (featureVector[i]!=0) {
			  dims[p]=i;
			  values[p]=featureVector[i];
			  p++;
		  }
	  }
	  LabeledFeatureVector vec = new LabeledFeatureVector(ins.getLabel(),dims,values);
      // Need to normalize?
	  vec.normalizeL2();
	  System.out.println(instance_num+" "+dim);
      return vec;
	}
}