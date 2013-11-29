package on5.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class PropertyUtil {
	
	private static Properties props = new Properties();
	
	static{
		FileInputStream fis = null;
		try {
			String currentDirectory = new File("").getAbsolutePath();
			fis = new FileInputStream(currentDirectory+ "/conf/On5.properties");
			props.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getPropertyValueAsString(String pName){
		String val = props.getProperty(pName); 
		return (val == null) ? null : val.trim();
	}
	
	public static int getPropertyValueAsNumber(String pName){
		String val = props.getProperty(pName); 
		return (val == null) ? -1 : Integer.parseInt(val.trim());
	}
	
	
	

}
