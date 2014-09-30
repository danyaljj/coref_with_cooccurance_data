package edu.illinois.cs.cogcomp.cooccurancedata;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.NarrativeSchemaInstance;
import edu.illinois.cs.cogcomp.cooccurancedata.datastructures.WinogradCorefInstance2;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.NarrativeSchemaReader;
import edu.illinois.cs.cogcomp.cooccurancedata.readers.PronounDisambiguationDataReader;

import jnisvmlight.LabeledFeatureVector;
import jnisvmlight.SVMLightModel;
import jnisvmlight.SVMLightInterface;
import jnisvmlight.TrainingParameters;

public class JNI_SVMLight_Test {
 
	public static int N = 0;

	public static int M = 36932727; //18468506

	public static void main(String[] args) throws Exception {
	  PronounDisambiguationDataReader pr = new PronounDisambiguationDataReader(); 
	  pr.deserializeData2();
	  
	  NarrativeSchemaReader nsreader=new NarrativeSchemaReader();
	  ArrayList<NarrativeSchemaInstance> allInstances = nsreader.readSchema(6); // 6,8,10,12
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
		  if (ins.test_or_train==1) N++;
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
	  fe.setNarrativeSchema(allInstances);
	  int p=0;
	  for (WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations) {
		  if (ins.test_or_train==1) {
		      traindata[p] = getFeatureVector(ins,fe,train_num);
		      p++;
		  }
		  train_num++;
	  }

	  // Initialize a new TrainingParamteres object with the default SVM-light values
	  TrainingParameters tp = new TrainingParameters();
	  // Switch on some debugging output
	  tp.getLearningParameters().verbosity = 1;
	  // tp.getLearningParameters().RANKING = 1;
	  
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
      p=0;
      for (WinogradCorefInstance2 ins : pr.allInstances_withAntecedentAnnotations) {
		  if (ins.test_or_train==0) {
			  LabeledFeatureVector test_data=getFeatureVector(ins,fe,test_num);
			  double a = model.classify(test_data);
			  fe.setInstance(ins); 
			  int b = fe.getLabel();
			  if ((a < 0 && b < 0) || (a > 0 && b > 0)) {
				  precision++;
			  }
			  p++;
		  }
		  test_num++;
      }
      System.out.println("\n" + ((double) precision / p) + " PRECISION on testing data");
	}

    //TODO Verb normalization for Narrative Schema
	
	public static LabeledFeatureVector getFeatureVector(WinogradCorefInstance2 ins, FeatureExtractor fe, int instance_num) throws Exception {
	  fe.setInstance(ins); 
	  fe.setInstanceNumber(instance_num); 
	  fe.setTheVerbIndices(); 
	  fe.extractHeadNoun();  
	  fe.extractConnective();
	  fe.extractNarrativeSchema(1);
	  fe.extractNarrativeSchema(2);
	  double[] featureVector = fe.Extract();
	  
	  //System.out.println(featureVector.length);
	  
	  int dim=0;
	  for (int i=0;i<featureVector.length;i++) {
		  if (featureVector[i]!=0) dim++;
	  }
      int[] dims = new int[dim+1];
      double[] values = new double[dim+1];
      int p=0;
	  for (int i=0;i<featureVector.length;i++) {
		  if (featureVector[i]!=0) {
			  dims[p]=i+1;
			  values[p]=featureVector[i];
			  p++;
		  }
	  }
	  
	  // Set for dimension change when test
	  dims[dim]=M;values[dim]=1.0;
	  
	  LabeledFeatureVector vec = new LabeledFeatureVector(fe.getLabel(),dims,values);
      // Need to normalize?
	  vec.normalizeL2();
	  //System.out.println(instance_num+" "+dim);
      return vec;
	}
}