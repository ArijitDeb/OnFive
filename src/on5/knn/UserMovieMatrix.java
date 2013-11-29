package on5.knn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import on5.common.Constants;


public class UserMovieMatrix {
	
	
	public int[][] getUserMovieMatrix(){
		
		int[][] userMovieMatrix = new int[Constants.NO_OF_USERS][Constants.NO_OF_MOVIES];

		for(int i=0;i<Constants.NO_OF_USERS;i++){
			for(int j=0;j<Constants.NO_OF_MOVIES;j++){
				userMovieMatrix[i][j] = 0;
			}
		}		

		//load file
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(Constants.RAW_INPUT_FILE_NAME)));
			String line = null;
			while((line = br.readLine()) != null){
				String[] vals = line.split("\t");
				int userId = Integer.parseInt(vals[0]) - 1;
				int movieId = Integer.parseInt(vals[1]) - 1;
				int rating = Integer.parseInt(vals[2]);
				userMovieMatrix[userId][movieId] = rating;
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

		return userMovieMatrix;
	}

}
