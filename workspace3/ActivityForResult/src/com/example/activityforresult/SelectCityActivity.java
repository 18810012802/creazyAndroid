package com.example.activityforresult;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectCityActivity extends ExpandableListActivity {
	private String[] provinces=new String[]{
		"广东","广西","湖南"	
	};
	
	private String[][] cities=new String[][]{
			{"广州","深圳","珠海","中山"},
			{"桂林","柳州","南宁","北海"},
			{"长沙","岳阳","衡阳","株洲"}
	};
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ExpandableListAdapter adapter=new BaseExpandableListAdapter(){

			@Override
			public Object getChild(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return cities[arg0][arg1];
			}

			@Override
			public long getChildId(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return arg1;
			}

			@Override
			public View getChildView(int arg0, int arg1, boolean arg2,
					View arg3, ViewGroup arg4) {
				// TODO Auto-generated method stub
				TextView textView=getTextView();
				textView.setText(getChild(arg0,arg1).toString());
				
				return textView;
			}

			@Override
			public int getChildrenCount(int arg0) {
				// TODO Auto-generated method stub
				return cities[arg0].length;
			}
			
			private TextView getTextView(){
				AbsListView.LayoutParams lp=new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,64);
				TextView textView=new TextView(SelectCityActivity.this);
				textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
				textView.setPadding(36, 0, 0,0);
				textView.setTextSize(20);
				return textView;
			}
			
			@Override
			public Object getGroup(int arg0) {
				// TODO Auto-generated method stub
				return provinces[arg0];
			}

			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return provinces.length;
			}

			@Override
			public long getGroupId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public View getGroupView(int arg0, boolean arg1, View arg2,
					ViewGroup arg3) {
				// TODO Auto-generated method stub
				LinearLayout ll=new LinearLayout(SelectCityActivity.this);
				ll.setOrientation(0);
				ImageView logo=new ImageView(SelectCityActivity.this);
				ll.addView(logo);
				TextView textView=getTextView();
				textView.setText(getGroup(arg0).toString());
				ll.addView(textView);
				return ll;
			}

			@Override
			public boolean hasStableIds() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean isChildSelectable(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return true;
			}
			
		};
		
		setListAdapter(adapter);
		
		getExpandableListView().setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView parent, View source,
					int arg2, int arg3, long id) {
				// TODO Auto-generated method stub
				Intent intent=getIntent();
				intent.putExtra("city",cities[arg2][arg3]);
				SelectCityActivity.this.setResult(0,intent);
				SelectCityActivity.this.finish();
				return false;
			}
			
		});
	}
}
