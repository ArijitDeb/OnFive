package on5.common;

import on5.util.PropertyUtil;

public class Constants {
	
	public static final int NO_OF_USERS = 943;
	public static final int NO_OF_MOVIES = 1682;
	
	public static final String RAW_INPUT_FILE_NAME = 
			PropertyUtil.getPropertyValueAsString("inputFileName");
	public static final String RAW_TEST_FILE_NAME = 
			PropertyUtil.getPropertyValueAsString("testFileName");
	
	public static final String SIMILARITY_MATRIX_FILE_NAME = "ua.similarity.txt";

	/* Property values */
	public static final String RECOMMEND_FOR_USER_PROPERTY_KEY = "recommendMovieFor";
	public static final String NO_OF_RECOMMEND_MOVIES_PROPERTY_KEY = "numberOfRecommendedMovies";
	
}
