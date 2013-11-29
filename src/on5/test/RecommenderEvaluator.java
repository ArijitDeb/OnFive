package on5.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.Recommender;


public class RecommenderEvaluator {
	
	private String testFileName = "/Users/arijitdeb/Documents/Data Mining/Project/ml-100k/ua.test";
	
	private Recommender rec;
	
	private int totalRMSEError = 0, totalAbsError = 0, totalCount = 0;
	
	private Recommender defaultRecommender = null;
	
	public RecommenderEvaluator(Recommender rec){
		this(rec, null);	
	}
	
	public RecommenderEvaluator(Recommender rec, Recommender dRec){
		this.defaultRecommender = dRec;
		this.rec = rec;
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(testFileName)));
			String line = null;
			while((line = br.readLine()) != null){
				String[] vals = line.split("\t");
				int userId = Integer.parseInt(vals[0]);
				int movieId = Integer.parseInt(vals[1]);
				int actualRating = Integer.parseInt(vals[2]);
				double pRating = 0;
				try{
					pRating = this.rec.estimatePreference(userId, movieId);
					if(pRating == 0){
						pRating = this.defaultRecommender.estimatePreference(userId, movieId);
					}
				}catch(TasteException e){
					e.printStackTrace();
				}
				int predictedRating =  (int) Math.round(pRating);
				totalRMSEError += ((actualRating - predictedRating) * (actualRating - predictedRating));
				totalAbsError += Math.abs(actualRating - predictedRating);
				totalCount++;
//				System.out.println("User: "+(userId+1) +" Movie: "+(movieId+1)+
//						" Actual: "+actualRating+" Predicted: "+predictedRating);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Calculates test RMSE
	 * @return
	 */
	public double calculateRMSE(){
		return Math.sqrt((double) totalRMSEError / totalCount);
	}



	/**
	 * Calculates test MAE
	 * @return
	 */
	public double calculateMAE(){		
		return (double) totalAbsError / totalCount;
	}
	

}
