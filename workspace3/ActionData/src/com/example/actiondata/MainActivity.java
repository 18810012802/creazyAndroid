package com.example.actiondata;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bn=(Button)findViewById(R.id.bn);
        bn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				String data="http://www.baidu.com";
				Uri uri=Uri.parse(data);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				startActivity(intent);
			}
        	
        });
        
        Button edit=(Button)findViewById(R.id.edit);
        edit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				String data=""
						+ "://com.android.contacts/contacts/1";
				Uri uri=Uri.parse(data);
				intent.setAction(Intent.ACTION_EDIT);
				intent.setData(uri);
				startActivity(intent);
			}
        	
        });
        
        Button call=(Button)findViewById(R.id.call);
        call.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				String data="tel:18354268361";
				Uri uri=Uri.parse(data);
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(uri);
				startActivity(intent);
			}
        	
        });
    }

}
