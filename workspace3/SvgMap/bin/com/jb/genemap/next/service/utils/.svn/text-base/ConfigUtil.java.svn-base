package com.jb.genemap.next.service.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
	public String getValue(String fileName,String key){
		String result = "";
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream("/db.properties");
			prop.load(in);
			if(prop.containsKey(key)){
				result = prop.getProperty(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
