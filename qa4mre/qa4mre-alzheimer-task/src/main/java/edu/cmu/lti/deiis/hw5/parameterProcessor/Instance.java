package edu.cmu.lti.deiis.hw5.parameterProcessor;

import java.util.ArrayList;

public class Instance {
	public Double y;
	public ArrayList<Double> x;

	public Instance(String input) {
		String[] numbers = input.split(" ");
		y = Double.parseDouble(numbers[0]);
		x = new ArrayList<Double>();
		Double total = new Double(0.);
		for (int i = 1; i < numbers.length; ++i) {
			Double value = Double.parseDouble(numbers[i]);
			x.add(value);
			total += value;
		}

		int basicFeatureCount = x.size();
/*
		for (int i = 0; i < basicFeatureCount; ++i)
			for (int j = 0; j < basicFeatureCount; ++j)
				x.add(x.get(i) * x.get(j));
/*
		if (total != 0)
			for (int i = 1; i < x.size(); ++i) {
				x.set(i, x.get(i) / total);
			}
*/
	}
}
