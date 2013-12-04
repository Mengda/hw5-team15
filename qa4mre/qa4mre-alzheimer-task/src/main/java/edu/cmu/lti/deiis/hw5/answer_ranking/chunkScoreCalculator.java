package edu.cmu.lti.deiis.hw5.answer_ranking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunctionException;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;
import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.morpha.MorphaStemmer;

public class chunkScoreCalculator {

  public static void main(String arg[]) {
    String s1 = "Some clusterin single nucleotide polymorphism has been linked to a reduction in the risk for developing Alzheimer's disease";
    String s2 = "Introduction Top Clusterin (CLU, APOJ) has been implicated in diseases ranging from cancer to Alzheimer's disease (AD) (reviewed in [1], [2], [3], [4]).";
    String s3 = "Find this article online (1997) Consensus recommendations for the postmortem diagnosis of Alzheimer's disease.";
    String s4 = "Find this article online Aksenov MY, Tucker HM, Nair P, Aksenova MV, Butterfield DA, et al. (1999) The expression of several mitochondrial and nuclear genes encoding the subunits of electron transport chain enzyme complexes, cytochrome c oxidase, and NADH dehydrogenase, in different brain regions in Alzheimer's disease";
    String s5 = "Abstract Top The minor allele of rs11136000 within CLU is strongly associated with reduced Alzheimer's disease (AD) risk.";
    String s6 = "Furthermore, since CLU expression is increased in AD without reversing the disease, we speculate that enhanced CLU expression reduces AD risk only if CLU expression is increased well before AD onset, mimicking the likely SNP effects.";
    // 0.1666
    // 0
    // 0

    ChunkSimilarity(s1, s5);
  }

  public static double ChunkSimilarity(String str1, String str2) {
	  
    ArrayList<ArrayList<String>> question = CBExtractor(str1);
    ArrayList<ArrayList<String>> answer = CBExtractor(str2);
    double maxScore = calculateChunkScore(question, answer) / 1.5;
    //System.out.println("max score is " + maxScore);
    return Math.max(maxScore , 0.01d);
  }

  private static ArrayList<ArrayList<String>> CBExtractor(String str) {
    String sentStr = str;
    // Looks on the classpath for the default model files.
    OpenNlpSentenceChunker chunker = null;
    try {
      chunker = new OpenNlpSentenceChunker();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    ChunkedSentence sent = chunker.chunkSentence(sentStr);

    // Prints out the (token, tag, chunk-tag) for the sentence
//    System.out.println(str);
//     for (int ii = 0; ii < sent.getLength(); ii++) {
//     String token = sent.getToken(ii);
//     String posTag = sent.getPosTag(ii);
//     String chunkTag = sent.getChunkTag(ii);
//     System.out.println(token + " " + posTag + " " + chunkTag);
//     }

    // Prints out extractions from the sentence.
    ReVerbExtractor reverb = new ReVerbExtractor();
    ConfidenceFunction confFunc = null;
    try {
      confFunc = new ReVerbOpenNlpConfFunction();
    } catch (ConfidenceFunctionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
    for (ChunkedBinaryExtraction extr : reverb.extract(sent)) {
      result.add(new ArrayList<String>());
      result.get(result.size() - 1).add(extr.getArgument1().toString());
      result.get(result.size() - 1).add(extr.getRelation().toString());
      result.get(result.size() - 1).add(extr.getArgument2().toString());

      double conf = confFunc.getConf(extr);
      //System.out.println("Arg1=" + extr.getArgument1());
      //System.out.println("Rel=" + extr.getRelation());
      //System.out.println("Arg2=" + extr.getArgument2());
      //System.out.println("Conf=" + conf);
    }
    return result;
  }

  public static double calculateChunkScore(ArrayList<ArrayList<String>> Question,
          ArrayList<ArrayList<String>> Answer) {
    double maxScore = 0;
    double currentScore = 0;
    for (ArrayList<String> q : Question) {
      for (ArrayList<String> a : Answer) {

        currentScore = Math.max(
                calculateTokenScore(q.get(0), a.get(0)) + calculateTokenScore(q.get(2), a.get(2)),
                calculateTokenScore(q.get(0), a.get(2)) + calculateTokenScore(q.get(2), a.get(0)))
                + calculateTokenScore(q.get(1), a.get(1));

        if (currentScore > maxScore)
          maxScore = currentScore;

      }
    }

    return maxScore;
  }

  public static double calculateTokenScore(String str1, String str2) {
    String[] tokens1 = str1.split(" ");
    String[] tokens2 = str2.split(" ");
    double count = 0;
    for (String s1 : tokens1) {
      for (String s2 : tokens2) {
        s1 = MorphaStemmer.stemToken(s1);
        s2 = MorphaStemmer.stemToken(s2);
        //System.out.println("word similarity between " + s1 + " and " + s2 + " "
        //        + wordSimilarity(s1, s2));
        if (wordSimilarity(s1, s2) > 0.9 || isSimilar(s1, s2)) {
          count++;
          break;
        }
      }
    }

    return count / (Math.min(tokens1.length, tokens2.length));
  }

  private static boolean isSimilar(String s1, String s2) {
    if (s1.length() < 4 || s2.length() < 4)
      return false;
    if (s1.substring(0, 4).equals(s2.substring(0, 4)))
      return true;
    return false;
  }

  public static double wordSimilarity(String word1, String word2) {
    ILexicalDatabase db = new NictWordNet();
    RelatednessCalculator[] rcs = { new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),
        new WuPalmer(db), new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };

    WS4JConfiguration.getInstance().setMFS(true);
    RelatednessCalculator rc = new Lin(db);
    double s = rc.calcRelatednessOfWords(word1, word2);
    return s;

  }

  public static String questionReform(String question)
  {
question = question.replace("Which", "some");
    return question = question.replace("What", "some");
  }
  


}
