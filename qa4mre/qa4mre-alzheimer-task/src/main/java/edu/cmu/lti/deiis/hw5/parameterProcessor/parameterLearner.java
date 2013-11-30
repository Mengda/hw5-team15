package edu.cmu.lti.deiis.hw5.parameterProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class parameterLearner {

	@SuppressWarnings("resource")
	static public void main(String[] args) throws Exception {
		Double maxScore = 0.;
		for (int trycount = 0; trycount < 10; ++trycount) {
			File file = new File("parameterInstance.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			ArrayList<Instance> instances = new ArrayList<Instance>();
			String line = br.readLine();

			while (line != null) {
				instances.add(new Instance(line));
				line = br.readLine();
			}

			ArrayList<Double> result = Train(instances);
			
			Double curScore = Test(result, instances);
			
			if(curScore > maxScore){
				maxScore = curScore;
				for (Double d : result) {
					System.out.println(d);
				}
			}
		}
	}

	public static Double Test(ArrayList<Double> w, ArrayList<Instance> instances) {
		int correct = 0, incorrect = 0;
		int totalInstance = instances.size();
		for (int i = 0; i < totalInstance / 5; ++i) {
			Double trueScore = 0.;
			Double maxScore = 0.;
			for (int j = 0; j < 5; ++j) {
				int index = i * 5 + j;
				Double p = 1. / (1 + Math.exp(-InnerProduct(w,
						instances.get(index).x)));
				if (instances.get(index).y == 1)
					trueScore = p;
				if (p > maxScore)
					maxScore = p;
			}
			if (Math.abs(trueScore - maxScore) < 0.0001)
				++correct;
			else
				++incorrect;
		}
		System.out.format("correct=%d incorrect=%d\n", correct, incorrect);
		return correct / new Double(correct + incorrect);
	}

	public static ArrayList<Double> Train(ArrayList<Instance> instances) {
		ArrayList<Double> result = new ArrayList<Double>(instances.get(0).x);
		for (int i = 0; i < result.size(); ++i) {
			result.set(i, 0.);
		}
		Random r = new Random();
		Integer instanceSize = instances.size();
		for (int epoch = 1; epoch < 10000000; ++epoch) {
			Integer index = Math.abs(r.nextInt()) % instanceSize;
			Double p = 1. / (1 + Math.exp(-InnerProduct(result,
					instances.get(index).x)));
			/*
			 * for (Double d : result) { System.out.format("%f ", d); } for
			 * (Double d : instances.get(index).x) { System.out.format("%f ",
			 * d); }
			 */
			Double compensation = 1.;
			if (instances.get(index).y == 1)
				compensation = 4.00;

			Add(result, instances.get(index).x,
					compensation * (instances.get(index).y - p) / 100000);

			// System.out.println();
		}
		return result;
	}

	public static void Add(ArrayList<Double> v1, ArrayList<Double> v2,
			Double step) {
		// System.out.println(step);
		for (int i = 0; i < v1.size(); ++i) {
			v1.set(i, v1.get(i) + v2.get(i) * step);
		}
	}

	public static Double InnerProduct(ArrayList<Double> v1, ArrayList<Double> v2) {
		Double result = 0.;
		for (int i = 0; i < v1.size(); ++i) {
			result += v1.get(i) * v2.get(i);
		}
		return result;
	}
}
