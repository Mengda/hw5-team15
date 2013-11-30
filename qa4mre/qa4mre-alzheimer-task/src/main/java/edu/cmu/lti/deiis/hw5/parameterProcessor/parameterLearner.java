package edu.cmu.lti.deiis.hw5.parameterProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class parameterLearner {

  @SuppressWarnings("resource")
  static public void main(String[] args) throws Exception {
    File file = new File("parameterInstance.txt");
    BufferedReader br = new BufferedReader(new FileReader(file));
    ArrayList<Instance> instances = new ArrayList<Instance>();
    String line = br.readLine();

    while (line != null) {
      instances.add(new Instance(line));
      line = br.readLine();
    }
    ArrayList<Double> result = Train(instances);
    for(Double d : result){
      System.out.println(d);
    }
  }

  public static ArrayList<Double> Train(ArrayList<Instance> instances) {
    ArrayList<Double> result = new ArrayList<Double>(instances.get(0).x);
    for (int i = 0; i < result.size(); ++i) {
      result.set(i, 0.);
    }
    Random r = new Random();
    Integer instanceSize = instances.size();
    for (int epoch = 0; epoch < 10000; ++epoch) {
      Integer index = r.nextInt() % instanceSize;
      Double p = (1 + Math.exp(-InnerProduct(result, instances.get(index).x)));
      Add(result,instances.get(index).x,(instances.get(index).y-p)/epoch);
    }
    return result;
  }
  
  public static void Add(ArrayList<Double> v1, ArrayList<Double> v2, Double step){
    for(int i = 0; i < v1.size(); ++i){
      v1.set(i, v1.get(i)+v2.get(i)*step);
    }
  }

  public static Double InnerProduct(ArrayList<Double> v1, ArrayList<Double> v2) {
    Double result = 0.;
    for(int i = 0; i < v1.size(); ++i){
      result += v1.get(i) * v2.get(i);
    }
    return result; 
  }
}
