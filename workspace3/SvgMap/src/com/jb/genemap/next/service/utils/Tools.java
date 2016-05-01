package com.jb.genemap.next.service.utils;

import java.io.File;
import java.io.Serializable;



public class Tools implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 
     * 判断文件的编码格式 
     *  
     * @param File:file 
     * @return 文件编码格式 
     * @throws Exception 
     */
    public static String getFileEncode(File file) throws Exception { 
    	 String code = "UTF8";
    	 EncodingDetect encodingDetect = new EncodingDetect();
         int flagNum = encodingDetect.detectEncoding(file);
         switch(flagNum){
	         case 0 :
	        	 code = "GBK";
	        	 break;
	         case 1 :
	        	 code = "GBK";
	        	 break;	 
	         case 2 :
	        	 code = "HZ";
	        	 break;	 
	         case 3 :
	        	 code = "BIG5";
	        	 break;	 
	         case 4 :
	        	 code = "EUC_TW";
	        	 break;	 
	         case 5 :
	        	 code = "ISO_2022_CN";
	        	 break;	 
	         case 6 :
	        	 code = "UTF8";
	        	 break;	 
	         case 7 :
	        	 code = "UNICODE";
	        	 break;	 
	         case 8 :
	        	 code = "ASCII";
	        	 break;	 
	         case 9 :
	        	 code = "OTHER";
	        	 break;	 
	         case 10:
	        	 code = "TOTAL_ENCODINGS";
	        	 break;	 
         }
         return code;
    } 	 
    
    /**
	 * 如果param为null 或者 是空字符串则返回true
	 * @return
	 */
	public static boolean isNull(String param){
		return param==null||"".equals(param.trim());
	}
}
