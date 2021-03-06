package com.example.launcheractivity;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableListActivityTest extends ExpandableListActivity{
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ExpandableListAdapter adapter=new BaseExpandableListAdapter(){
			int[] logos=new int[]{
					R.drawable.aa,
					R.drawable.aa,
					R.drawable.aa
					
			};
			private String[] armTypes=new String[]{
					"�������","�������","�������"
			};
			private String[][] arms=new String[][]{
					{"��սʿ","����ʿ","�ڰ�ʥ��","���"},
					{"С��","����","����","�Ա��ɻ�"},
					{"��ǹ��","��ʿMM","����"}
			};
			@Override
			public Object getChild(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return arms[arg0][arg1];
			}

			@Override
			public long getChildId(int arg0, int arg1) {
				// TODO Auto-generated method stub
				return arg1;
			}
			
			private TextView getTextView(){
				AbsListView.LayoutParams lp=new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,64);
				TextView textView=new TextView(ExpandableListActivityTest.this);
				textView.setLayoutParams(lp);
				textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
				textView.setPadding(36,0,0,0);
				textView.setTextSize(20);
				return textView;
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
				return arms[arg0].length;
			}

			@Override
			public Object getGroup(int arg0) {
				// TODO Auto-generated method stub
				return armTypes[arg0];
			}

			@Override
			public int getGroupCount() {
				// TODO Auto-generated method stub
				return armTypes.length;
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
				LinearLayout ll=new LinearLayout(ExpandableListActivityTest.this);
				ll.setOrientation(0);
				ImageView logo=new ImageView(ExpandableListActivityTest.this);
				logo.setImageResource(logos[arg0]);
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
	}
}
