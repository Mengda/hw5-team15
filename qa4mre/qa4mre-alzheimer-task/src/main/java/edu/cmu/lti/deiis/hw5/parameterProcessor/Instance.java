package edu.cmu.lti.deiis.hw5.parameterProcessor;

import java.util.ArrayList;

public class Instance {
	public Double y;
	public ArrayList<Double> x;

	public Instance(String input) {
		String[] numbers = input.split(" ");
		y = Double.parseDouble(numbers[0]);
		x = new ArrayList<Double>();
		for (int i = 1; i < numbers.length; ++i) {
			Double value = Double.parseDouble(numbers[i]);
			// TODO: Dirty hack for the missing parameter.
			if (i != 2)
				x.add(value);
		}

		int basicFeatureCount = x.size();

		for (int i = 0; i < basicFeatureCount; ++i)
			for (int j = 0; j < basicFeatureCount; ++j)
				x.add(x.get(i) * x.get(j));

		for (int i = 0; i < basicFeatureCount; ++i)
			for (int j = 0; j < basicFeatureCount; ++j)
				for (int k = 0; k < basicFeatureCount; ++k)
					x.add(x.get(i) * x.get(j) * x.get(k));

		for (int i = 0; i < basicFeatureCount; ++i)
			for (int j = 0; j < basicFeatureCount; ++j)
				for (int k = 0; k < basicFeatureCount; ++k)
					for (int l = 0; l < basicFeatureCount; ++l)
						x.add(x.get(i) * x.get(j) * x.get(k) * x.get(l));

	}
}
