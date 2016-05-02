package com.example.lifecycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SecondActivity extends Activity{
	final String TAG="--second--";
	Button finish,startActivity;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        Log.d(TAG,"-------onCreate-------");
	        finish=(Button)findViewById(R.id.finish);
	        startActivity=(Button)findViewById(R.id.startActivity);
	        startActivity.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(SecondActivity.this,MainActivity.class);
					startActivity(intent);
				}
	        	
	        });
	        finish.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					SecondActivity.this.finish();
				}
	        	
	        });
	    }
}
