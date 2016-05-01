package com.jb.genemap.next.service.utils;

import java.io.File;
import java.io.Serializable;



public class Tools implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 
     * �ж��ļ��ı����ʽ 
     *  
     * @param File:file 
     * @return �ļ������ʽ 
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
	 * ���paramΪnull ���� �ǿ��ַ����򷵻�true
	 * @return
	 */
	public static boolean isNull(String param){
		return param==null||"".equals(param.trim());
	}
}
