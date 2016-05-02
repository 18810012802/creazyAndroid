package com.example.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookContent {
	public static class Book{
		public Integer id;
		public String title;
		public String desc;
		public Book(Integer id,String title,String desc){
			this.id=id;
			this.title=title;
			this.desc=desc;
		}
		@Override
		public String toString(){
			return title;
		}
	}
	public static List<Book> ITEMS=new ArrayList<Book>();
	public static Map<Integer,Book> ITEM_MAP=new HashMap<Integer,Book>();
	
}
