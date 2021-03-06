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
	static{
		addItem(new Book(1,"Android从入门到精通","25小时视频讲解"));
		addItem(new Book(2,"疯狂Android讲义","正在看的"));
	}
	private static void addItem(Book book){
		ITEMS.add(book);
		ITEM_MAP.put(book.id,book);
	}
}
