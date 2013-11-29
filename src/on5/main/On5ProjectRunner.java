package on5.main;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import on5.common.Constants;
import on5.knn.KNearestNeighborRecommender;
import on5.sgd.SGD;
import on5.test.RecommenderEvaluator;
import on5.util.PropertyUtil;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class On5ProjectRunner {
  
   
  public static void main(String... args) throws IOException, TasteException {
	  
	  long startTime = new Date().getTime();
	  //get a user id for predicting recommendation
	  String pUserId = PropertyUtil.getPropertyValueAsString(Constants.RECOMMEND_FOR_USER_PROPERTY_KEY);
	  int noOfRecMovies = PropertyUtil.getPropertyValueAsNumber(Constants.NO_OF_RECOMMEND_MOVIES_PROPERTY_KEY);
    
	  //check if the files exist
	  File trainingFile = new File(Constants.RAW_INPUT_FILE_NAME);
	  File testFile = new File(Constants.RAW_INPUT_FILE_NAME);
	  
	  if(!trainingFile.exists() || !testFile.exists()){
		  System.out.println("File does not exist");
		  return;
	  }//TODO 
	  
	  DataModel model = new FileDataModel(trainingFile);
	  Recommender slopeOneRec = new SlopeOneRecommender(model);
	  
	  System.out.println("============ KNN ============");
	  Recommender knnRec = new KNearestNeighborRecommender();
	  RecommenderEvaluator evaluator = new RecommenderEvaluator(knnRec, slopeOneRec);
	  System.out.println("MAE: "+evaluator.calculateMAE());
	  System.out.println("RMSE: "+evaluator.calculateRMSE());
	  System.out.println("Recommendation for user: "+pUserId);
	  List<RecommendedItem> rItems = knnRec.recommend(Long.valueOf(pUserId), noOfRecMovies);
	  StringBuilder sb = new StringBuilder();
	  for(RecommendedItem ri : rItems){
		  sb.append(ri.toString()).append(" | ");
	  }
	  System.out.println("[ "+sb.toString()+"]");
	  System.out.println("=============================\n");
	  
	  System.out.println("============ SGD ============");
	  Recommender sgdRec = new SGD();
	  evaluator = new RecommenderEvaluator(sgdRec);
	  System.out.println("MAE: "+evaluator.calculateMAE());
	  System.out.println("RMSE: "+evaluator.calculateRMSE());
	  System.out.println("Recommendation for user: "+pUserId);
	  rItems = sgdRec.recommend(Long.valueOf(pUserId), noOfRecMovies);
	  sb = new StringBuilder();
	  for(RecommendedItem ri : rItems){
		  sb.append(ri.toString()).append(" | ");
	  }
	  System.out.println("[ "+sb.toString()+"]");
	  System.out.println("=============================\n");
	  
	  System.out.println("============ SlopeOne ============");
	  evaluator = new RecommenderEvaluator(slopeOneRec);
	  System.out.println("MAE: "+evaluator.calculateMAE());
	  System.out.println("RMSE: "+evaluator.calculateRMSE());
	  System.out.println("Recommendation for user: "+pUserId);
	  rItems = slopeOneRec.recommend(Long.valueOf(pUserId), noOfRecMovies);
	  sb = new StringBuilder();
	  for(RecommendedItem ri : rItems){
		  sb.append(ri.toString()).append(" | ");
	  }
	  System.out.println("[ "+sb.toString()+"]");
	  System.out.println("==================================\n");
	  
	  System.out.println("============ SDV++ ============");
	  Recommender sdvRec = new SVDRecommender(model, new SVDPlusPlusFactorizer(model, 55, 50));
	  evaluator = new RecommenderEvaluator(sdvRec);
	  System.out.println("MAE: "+evaluator.calculateMAE());
	  System.out.println("RMSE: "+evaluator.calculateRMSE());
	  System.out.println("Recommendation for user: "+pUserId);
	  rItems = sdvRec.recommend(Long.valueOf(pUserId), noOfRecMovies);
	  sb = new StringBuilder();
	  for(RecommendedItem ri : rItems){
		  sb.append(ri.toString()).append(" | ");
	  }
	  System.out.println("[ "+sb.toString()+"]");
	  System.out.println("===============================\n");
	  
	  long timeTaken = new Date().getTime() - startTime;
	  System.out.println("Total time taken: "+timeTaken / 1000+"s");

  }
  
}
