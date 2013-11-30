package edu.cmu.lti.deiis.hw5.parameterProcessor;

import java.util.ArrayList;

public class Instance {
  public Double y;
  public ArrayList<Double> x;
  public Instance(String input){
    String[] numbers = input.split(" ");
    y = Double.parseDouble(numbers[0]);
    x = new ArrayList<Double>();
    for(int i = 1; i < numbers.length; ++i){
      x.add(Double.parseDouble(numbers[i]));
    }
  }
}
