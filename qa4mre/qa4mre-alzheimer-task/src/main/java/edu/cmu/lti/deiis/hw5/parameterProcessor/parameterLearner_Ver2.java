package edu.cmu.lti.deiis.hw5.parameterProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class parameterLearner_Ver2 {
	
	static public void main(String[] args) throws Exception{
		File file = new File("parameterInstance.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		ArrayList<Instance> instances = new ArrayList<Instance>();
		String line = br.readLine();
		
		File outFile = new File("Data.arff");
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		bw.append("@relation Random");
		bw.newLine();
		bw.newLine();
		bw.append("@attribute a01 numeric\n@attribute a02 numeric\n@attribute a03 numeric\n@attribute class {1,0}\n\n@data\n\n");
		while (line != null) {
			String[] parts = line.split(" ");
			for(int i = 1; i < parts.length; ++i){
				bw.append(parts[i] + ",");
			}
			bw.append(parts[0]);
			bw.newLine();

			line = br.readLine();
		}
		bw.flush();
		bw.close();
		br.close();

	}
}