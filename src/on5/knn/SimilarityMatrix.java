package on5.knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import on5.common.Constants;



public class SimilarityMatrix {
	
	private int minRatingOverlap = 5;
	
	private int[][] userMovieMatrix = null;
	
	public SimilarityMatrix(int[][] userMovieMatrix){
		this.userMovieMatrix = userMovieMatrix;
	}
	
	public Map<Integer, Double> getMovieAvgRating(){
		Map<Integer, Double> movieAvgRating = new HashMap<Integer, Double>();
		int[] total = new int[Constants.NO_OF_MOVIES];
		int[] count = new int[Constants.NO_OF_MOVIES];
		for(int i=0;i<userMovieMatrix.length;i++){
			for(int j=0;j<userMovieMatrix[i].length;j++){
				if(userMovieMatrix[i][j] != 0){
					total[j] += userMovieMatrix[i][j];
					count[j]++;
				}
			}
		}
		for(int i=0;i<Constants.NO_OF_MOVIES;i++){
			movieAvgRating.put(i, (double) total[i] / count[i]);
		}
		return movieAvgRating;
	}
	
	public double[] getUsersAverageRating(){
		double[] avgRating = new double[Constants.NO_OF_USERS];
		for(int i=0;i<Constants.NO_OF_USERS;i++){
			int total = 0;
			int noOfRatedMovies = 0;
			for(int j=0;j<Constants.NO_OF_MOVIES;j++){
				if(userMovieMatrix[i][j] != 0){
					total += userMovieMatrix[i][j];
					noOfRatedMovies++;
				}				
			}
			avgRating[i] = (double) total / noOfRatedMovies;
		}
		return avgRating;
	}
	
	public double[][] getSimilarityMatrix(){		
		//calculate each user's avg rating
		double[] avgRating = this.getUsersAverageRating();
		
		double[][] retVal = new double[Constants.NO_OF_MOVIES][Constants.NO_OF_MOVIES];
		
		for(int i=0;i<Constants.NO_OF_MOVIES;i++){
			for(int j=0;j<Constants.NO_OF_MOVIES;j++){
				retVal[i][j] = 0.0;
			}
		}
		
		for(int i=0;i<Constants.NO_OF_MOVIES;i++){
			for(int j=i+1;j<Constants.NO_OF_MOVIES;j++){
				if(retVal[i][j] != 0.0){ //this value is already set
					continue;
				}
				retVal[i][j] = 0.0;
				// go through every user to check if
				// the user has rated both the movies
				double n = 0;
				double d1 = 0, d2 = 0;
				int ratingOverlapCount = 0;
				for(int k=0;k<Constants.NO_OF_USERS;k++){
					if(userMovieMatrix[k][i] != 0 && userMovieMatrix[k][j] != 0){
						n += ((userMovieMatrix[k][i] - avgRating[k]) * (userMovieMatrix[k][j] - avgRating[k]));
						d1 += Math.pow(userMovieMatrix[k][i] - avgRating[k], 2);
						d2 += Math.pow(userMovieMatrix[k][j] - avgRating[k], 2);
						ratingOverlapCount++;
					}
				}
				if(ratingOverlapCount >= this.minRatingOverlap &&
						n != 0 && d1 != 0 && d2 != 0){
					retVal[i][j] = n / (Math.sqrt(d1) * Math.sqrt(d2));
					retVal[j][i] = retVal[i][j];
				}
			}
		}
		
		return retVal;
		
	}
	
	public void saveSimilarityMatrix(){
		double[][] simMatrix = this.getSimilarityMatrix();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(Constants.SIMILARITY_MATRIX_FILE_NAME));
			for(int i=0;i<simMatrix.length;i++){
				StringBuilder sb = new StringBuilder();
				for(int j=0;j<simMatrix[i].length;j++){
					if(j > 0){
						sb.append(' ');
					}
					sb.append(simMatrix[i][j]);
				}
				pw.println(sb.toString());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			pw.close();
		}
	}
	
	public double[][]  getSimilarityMatrix(boolean readFromFile){
		if(readFromFile){
			double[][] retVal = new double[Constants.NO_OF_MOVIES][Constants.NO_OF_MOVIES];
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(new File(Constants.SIMILARITY_MATRIX_FILE_NAME)));
				String line = null;
				int lineNumber = 0;
				while((line = br.readLine()) != null){
					String[] vals = line.split(" ");
					for(int i=0;i<vals.length;i++){
						retVal[lineNumber][i] = Double.parseDouble(vals[i]);
					}
					lineNumber++;
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
			return retVal;
		}else{
			return this.getSimilarityMatrix();
		}
	}

}
