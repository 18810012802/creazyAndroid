package com.example.lifecycle;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
final String TAG="--main--";
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
				Intent intent=new Intent(MainActivity.this,SecondActivity.class);
				startActivity(intent);
			}
        	
        });
        finish.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MainActivity.this.finish();
			}
        	
        });
    }
    @Override
    public void onStart(){
    	super.onStart();
    	Log.d(TAG,"-------onStart-------");
    }
    @Override
    public void onRestart(){
    	super.onRestart();
    	Log.d(TAG,"-------onRestart-------");
    }
    @Override
    public void onResume(){
    	super.onResume();
    	Log.d(TAG,"-------onResume-------");
    }
    @Override
    public void onPause(){
    	super.onPause();
    	Log.d(TAG,"-------onPause-------");
    }
    @Override
    public void onStop(){
    	super.onStop();
    	Log.d(TAG,"-------onStop-------");
    }
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Log.d(TAG,"-------onDestroy-------");
    }

}
