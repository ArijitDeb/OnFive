package on5.sgd;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import on5.common.Constants;
import on5.knn.UserMovieMatrix;
import on5.util.On5Util;

import Jama.Matrix;


public class SGD implements Recommender{
	private static double START_VALUE = 0.5;
	private Matrix userMovieMatrix;
	private static final int COUNTER = 1000;
	private static final double LEARNING_RATE = 0.0005;
	private static final int NUM_FEATURES = 10;
	private static final double K = 0.02;
	
	private double[][] updatedUserMovieMatrix = null;

	public SGD() {
		this.userMovieMatrix = generateMatrix();
		this.calculateSVD();
	}

	private Matrix generateMatrix() {
		int[][] userMovieMatrix = new UserMovieMatrix().getUserMovieMatrix();
		double[][] dUserMovieMatrix = new double[Constants.NO_OF_USERS][Constants.NO_OF_MOVIES];
		
		//convert the int[][] to a double[][], so that we can create a matrix
		for(int i=0;i<userMovieMatrix.length;i++){
			for(int j=0;j<userMovieMatrix[i].length;j++){
				dUserMovieMatrix[i][j] = (double) userMovieMatrix[i][j];
			}
		}
		
		return new Matrix(dUserMovieMatrix);
	}

	public void calculateSVD() {
		userMovieMatrix = userMovieMatrix.transpose();

		Matrix result = calculatePredictedMatrix(null, null, null); //
		result = result.transpose();
		
		//at this point the result matrix is updated with predicted values
		this.updatedUserMovieMatrix = result.getArray();
	}
	
	private Matrix calculatePredictedMatrix(Matrix u, Matrix v,
			double[] singularValues) {

		Matrix result = null;
		int counter = COUNTER;
		u = new Matrix(Constants.NO_OF_MOVIES, NUM_FEATURES, START_VALUE);
		v = new Matrix(Constants.NO_OF_USERS, NUM_FEATURES, START_VALUE);
		while (counter != 0) {
			result = u.times(v.transpose());
			update(u, v, result);
			counter--;
		}
		return result;
	}
	private void update(Matrix u, Matrix v, Matrix result) {
		for (int k = 0; k < NUM_FEATURES; k++) {
			for (int i = 0; i < Constants.NO_OF_MOVIES; i++) {
				for (int j = 0; j < Constants.NO_OF_USERS; j++) {
					double rValue = userMovieMatrix.get(i, j);
					// work only on known values
					if (rValue == 0d) {
						continue;
					}
					double rAppValue = result.get(i, j);
					double err = rValue - rAppValue;
					double pikOld = u.get(i, k);
					double fjkOld = v.get(j, k);
					double pikNew = pikOld + LEARNING_RATE
							* (err * fjkOld - K * pikOld);
					double fjkNew = fjkOld + LEARNING_RATE
							* (err * pikOld - K * fjkOld);
					
					u.set(i, k, pikNew);
					v.set(j, k, fjkNew);
				}
			}
		}
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {}

	@Override
	public List<RecommendedItem> recommend(long userId, int howMany)
			throws TasteException {
		return On5Util.recommendMovieBasedOnUpdatedUsermovieMatrix((int) userId, howMany, 
				this);
	}

	@Override
	public int estimatePreference(long userID, long itemID)
			throws TasteException {
		if(this.updatedUserMovieMatrix != null){
			return On5Util.getValidRating(this.updatedUserMovieMatrix[(int) userID - 1][(int) itemID - 1]);
		}
		return 0;
	}

}