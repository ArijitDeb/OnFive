package on5.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.GenericRecommendedItem;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import on5.knn.UserMovieMatrix;

public class On5Util {
		
	private static int[][] origUserMovieMatrix = new UserMovieMatrix().getUserMovieMatrix();
	
	public static List<RecommendedItem> recommendMovieBasedOnUpdatedUsermovieMatrix(int userId, int howMany, 
			Recommender rec){
		int[] origRating = origUserMovieMatrix[userId - 1];		
		Map<Integer, Float> movieRatingMap = new HashMap<Integer, Float>(); 
		for(int j=1;j<=origRating.length;j++){
			//if the user hasn't seen it then consider for recommendation
			if(origRating[j-1] == 0){				
				try {
					float pValue = rec.estimatePreference(userId, j);
					if(!Float.isNaN(pValue) && pValue != 0 && pValue <= 5.0){
						movieRatingMap.put(j, pValue);
					}
				} catch (TasteException e) {
					e.printStackTrace();
				}
			}			
		}
		//sort it
		Map<Integer, Float> sortedMap = sortByComparator(movieRatingMap, false);
		List<RecommendedItem> retList = new ArrayList<RecommendedItem>();
		for(Map.Entry<Integer, Float> entry : sortedMap.entrySet()){
			//if the movie is not seen by the user, then recommend it
			retList.add(new GenericRecommendedItem(entry.getKey(), entry.getValue()));

			if(retList.size() == howMany){
				break;
			}
		}
		return retList;
	}
	/*
	public static List<RecommendedItem> recommendMovieBasedOnUpdatedUsermovieMatrix(int userId, int howMany, int[][] usm){
		//get the original user-movie matrix
		int[][] origUserMovieMatrix = UserMovieMatrix.getUserMovieMatrix();
		int[] origRating = origUserMovieMatrix[userId];
		int[] updatedRating = usm[userId];
		Map<Integer, Integer> movieRatingMap = new HashMap<Integer, Integer>(); 
		for(int j=0;j<updatedRating.length;j++){
			movieRatingMap.put(j, updatedRating[j]);
		}
		
		//sort the movieRatingMap in descending order
		movieRatingMap = sortByComparator(movieRatingMap, false);
		List<RecommendedItem> retList = new ArrayList<RecommendedItem>();
		for(Map.Entry<Integer, Integer> entry : movieRatingMap.entrySet()){
			//if the movie is not seen by the user, then recommend it
			if(origRating[entry.getKey()] == 0){
				retList.add(new GenericRecommendedItem(entry.getKey(), entry.getValue()));
			}
			
			if(retList.size() == howMany){
				break;
			}
		}
		
		return retList;
	}
	
	public static List<RecommendedItem> recommendMovieBasedOnUpdatedUsermovieMatrix(int userId, int howMany, double[][] usm){
		//get the original user-movie matrix
		int[][] origUserMovieMatrix = UserMovieMatrix.getUserMovieMatrix();
		int[] origRating = origUserMovieMatrix[userId];
		double[] updatedRating = usm[userId];
		Map<Integer, Double> movieRatingMap = new HashMap<Integer, Double>(); 
		for(int j=0;j<updatedRating.length;j++){
			movieRatingMap.put(j, updatedRating[j]);
		}
		
		//sort the movieRatingMap in descending order
		movieRatingMap = sortByComparator(movieRatingMap, false);
		List<RecommendedItem> retList = new ArrayList<RecommendedItem>();
		for(Map.Entry<Integer, Double> entry : movieRatingMap.entrySet()){
			//if the movie is not seen by the user, then recommend it
			if(origRating[entry.getKey()] == 0){
				double val = entry.getValue();
				retList.add(new GenericRecommendedItem(entry.getKey(), (float) val));
			}
			
			if(retList.size() == howMany){
				break;
			}
		}
		
		return retList;
	}*/
	
	private static <K, V extends Comparable<V>> Map<K, V> sortByComparator(Map<K, V> unsortMap, final boolean isAscOrder){

        List<Entry<K, V>> list = new LinkedList<Entry<K, V>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<K, V>>()
        {
            public int compare(Entry<K, V> o1, Entry<K, V> o2)
            {
                if(isAscOrder){
                    return o1.getValue().compareTo(o2.getValue());
                }else{
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (Entry<K, V> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public static int getValidRating(double d){
		if(Double.isNaN(d) || d <= 0){
			return 0;
		}else if(d >= 5){
			return 5;
		}else{
			return (int) Math.round(d);
		}
	}

}
