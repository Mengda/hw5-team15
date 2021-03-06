package edu.cmu.lti.deiis.hw5.answer_selection;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.qalab.types.Answer;
import edu.cmu.lti.qalab.types.CandidateAnswer;
import edu.cmu.lti.qalab.types.CandidateSentence;
import edu.cmu.lti.qalab.types.Question;
import edu.cmu.lti.qalab.types.QuestionAnswerSet;
import edu.cmu.lti.qalab.types.TestDocument;
import edu.cmu.lti.qalab.utils.Utils;

public class AnswerSelectionByKCandVoting extends JCasAnnotator_ImplBase {

  int K_CANDIDATES = 5;

  @Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
    K_CANDIDATES = (Integer) context.getConfigParameterValue("K_CANDIDATES");
	}

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    TestDocument testDoc = Utils.getTestDocumentFromCAS(aJCas);
    //ying
    String docId = testDoc.getId();
    ArrayList<QuestionAnswerSet> qaSet = Utils.fromFSListToCollection(testDoc.getQaList(),
            QuestionAnswerSet.class);
    int matched = 0;
    int total = 0;
    int unanswered = 0;
    //ying
 
    PrintWriter ScoreWriter=null;
    String outf=docId+"_score.txt";
    try {
      ScoreWriter = new PrintWriter(outf, "UTF-8");
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (UnsupportedEncodingException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
   
    //ying
    
    for (int i = 0; i < qaSet.size(); i++) {

      Question question = qaSet.get(i).getQuestion();
      System.out.println("Question: " + question.getText());
      ArrayList<Answer> choiceList = Utils.fromFSListToCollection(qaSet.get(i).getAnswerList(),
              Answer.class);
      ArrayList<CandidateSentence> candSentList = Utils.fromFSListToCollection(qaSet.get(i)
              .getCandidateSentenceList(), CandidateSentence.class);

      int topK = Math.min(K_CANDIDATES, candSentList.size());
      String correct = "";

      for (int j = 0; j < choiceList.size(); j++) {
        Answer answer = choiceList.get(j);
        if (answer.getIsCorrect()) {
          correct = answer.getText();
          break;
        }
      }
      
      HashMap<String, Double> hshAnswer = new HashMap<String, Double>();

      for (int c = 0; c < topK; c++) {

        CandidateSentence candSent = candSentList.get(c);
        
        ArrayList<CandidateAnswer> candAnswerList = Utils.fromFSListToCollection(
                candSent.getCandAnswerList(), CandidateAnswer.class);
        String selectedAnswer = "";
        double maxScore = Double.NEGATIVE_INFINITY;
        
        
        for (int j = 0; j < candAnswerList.size(); j++) {

          CandidateAnswer candAns = candAnswerList.get(j);
          String answer = candAns.getText();
          
  
          
          double totalScore = candAns.getSimilarityScore() + candAns.getSynonymScore()
                  + candAns.getPMIScore();
          
          //ying
          String summary=new String();
          if (candAns.getText().equals(correct)) {
            summary+="1 ";
          } else {summary+="0 ";}
          summary+=candAns.getSimilarityScore();
          summary+=" ";
          summary+=candAns.getSynonymScore();
          summary+=" ";
          summary+=candAns.getPMIScore();
          ScoreWriter.println(summary);
          //ying
        
          if (totalScore > maxScore) {
            maxScore = totalScore;
            selectedAnswer = answer;
          }
        }
        Double existingVal = hshAnswer.get(selectedAnswer);
        if (existingVal == null) {
          existingVal = new Double(0.0);
        }
        hshAnswer.put(selectedAnswer, existingVal + 1.0);
      }

      String bestChoice = null;
      try {
        bestChoice = findBestChoice(hshAnswer);

      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("Correct Choice: " + "\t" + correct);
      System.out.println("Best Choice: " + "\t" + bestChoice);
      
      if (bestChoice == null) {
        unanswered++;
      }
      if (bestChoice != null && correct.equals(bestChoice)) {
        matched++;
      }
      //YING START
      if (bestChoice !=null) {
        for (int j = 0; j < choiceList.size(); j++) {
          Answer answer = choiceList.get(j);
          if (answer.getText().equals(bestChoice)) {
            answer.setIsSelected(true);
            break;
          }
        }
      }
      //YING END
      total++;
      System.out.println("================================================");

    }

    System.out.println("Correct: " + matched + "/" + total + "=" + ((matched * 100.0) / total)
            + "%");
    // TO DO: Reader of this pipe line should read from xmi generated by
    // SimpleRunCPE
    double cAt1 = (((double) matched) / ((double) total) * unanswered + (double) matched)
            * (1.0 / total);
    System.out.println("c@1 score:" + cAt1);
    ScoreWriter.close();

  }

  public String findBestChoice(HashMap<String, Double> hshAnswer) throws Exception {

    Iterator<String> it = hshAnswer.keySet().iterator();
    String bestAns = null;
    double maxScore = 0;
    System.out.println("Aggregated counts; ");
    while (it.hasNext()) {
      String key = it.next();
      Double val = hshAnswer.get(key);
      System.out.println(key + "\t" + key + "\t" + val);
      if (val > maxScore) {
        maxScore = val;
        bestAns = key;
      }

    }

    return bestAns;
  }

}
