package on5.knn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import on5.common.Constants;
import on5.util.On5Util;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class KNearestNeighborRecommender implements Recommender{

	private final int neighborSize = 20;
	private final double minSimValue = 0.0;


	private SimilarityMatrix similarityMatrixObj = null;
	private int[][] userMovieMatrix = new int[Constants.NO_OF_USERS][Constants.NO_OF_MOVIES];
	private HashMap<Integer, ArrayList<SimilarMovie>> topSimilarMovieMap = null;
	private double[][] similarityMatrix = new double[Constants.NO_OF_MOVIES][Constants.NO_OF_MOVIES];
//	private double[] userAvg = null;
	private Map<Integer, Double> movieAvgRating = null;

	public KNearestNeighborRecommender(){
		this.topSimilarMovieMap = new HashMap<Integer, ArrayList<SimilarMovie>>();
		this.userMovieMatrix = new UserMovieMatrix().getUserMovieMatrix();
		this.similarityMatrixObj = new SimilarityMatrix(this.userMovieMatrix);
		this.similarityMatrix = similarityMatrixObj.getSimilarityMatrix();
//		this.userAvg = similarityMatrixObj.getUsersAverageRating();
		this.movieAvgRating = similarityMatrixObj.getMovieAvgRating();

		//update the topSimilarMovieMap
		for(int i=0;i<Constants.NO_OF_MOVIES;i++){
			ArrayList<SimilarMovie> similarMovieList = new ArrayList<SimilarMovie>();
			for(int j=0;j<similarityMatrix[i].length;j++){
				double val = similarityMatrix[i][j];
				if(i != j || val >= minSimValue){
					similarMovieList.add(new SimilarMovie(j, similarityMatrix[i][j]));
				}
			}

			//sort all the similar movies
			Collections.sort(similarMovieList, new Comparator<SimilarMovie>() {
				public int compare(SimilarMovie o1, SimilarMovie o2) {
					double diff = (o2.similarityValue - o1.similarityValue);
					return (diff == 0) ? 0 : ((diff < 0) ? -1 : 1);
				}
			});			

			topSimilarMovieMap.put(i, similarMovieList);
		}

		userMovieMatrix = this.getAllUpdatedRating();
	}
	
//	public double[] getUserAverage(){
//		return this.userAvg;
//	}
	
	public int[][] getAllUpdatedRating(){
		
		
		int[][] userMovieMatrixClone = userMovieMatrix.clone();
		
		for(int i=0;i<Constants.NO_OF_USERS;i++){
			for(int j=0;j<Constants.NO_OF_MOVIES;j++){
				if(userMovieMatrix[i][j] == 0){
					//predict the rating
					//get top similar movies for this jth movie
					ArrayList<SimilarMovie> topSimilarMovies = topSimilarMovieMap.get(j);
					
//					double n = 0, d = 0; 
					double totalBias = 0;
					double totalSimilarity = 0;
					int countedNeighbor = 0;
					for(SimilarMovie sm : topSimilarMovies){
						if(userMovieMatrix[i][sm.index] != 0){//the user has seen the similar movie
//							continue;
//						}
//						n += sm.similarityValue * userMovieMatrix[i][sm.index];
//						d += Math.abs(sm.similarityValue);
//						}
						double nBias = userMovieMatrix[i][sm.index] - movieAvgRating.get(sm.index);
						totalBias += (nBias * sm.similarityValue);
						totalSimilarity += Math.abs(sm.similarityValue);
//						}
						if(++countedNeighbor == neighborSize){
							break;
						}
						}
					}
//					if(countedNeighbor == neighborSize){
						userMovieMatrixClone[i][j] = (int) Math.round(movieAvgRating.get(j) + 
							(double) totalBias / totalSimilarity);
//					}
				}
			}
		}
		
		
		return userMovieMatrixClone;
	}


	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {	}

	@Override
	public List<RecommendedItem> recommend(long userId, int howMany) throws TasteException {
		return On5Util.recommendMovieBasedOnUpdatedUsermovieMatrix((int) userId, howMany, this);
	}

	@Override
	public int estimatePreference(long userID, long movieId) throws TasteException {
		return this.userMovieMatrix[(int)userID - 1][(int)movieId - 1];
	}



}

/**
 * Data Structure to hold the movie index (0 based) 
 * and it's similarity value
 * @author arijitdeb
 *
 */
class SimilarMovie{
	int index;
	double similarityValue;
	public SimilarMovie(int index, double similarityValue) {
		this.index = index;
		this.similarityValue = similarityValue;
	}
	public String toString(){
		return this.index +" "+this.similarityValue;
	}
}
