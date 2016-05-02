package com.example.activityforresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	Button bn;
	EditText city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bn=(Button)findViewById(R.id.bn);
        city=(EditText)findViewById(R.id.city);
        bn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,SelectCityActivity.class);
				startActivityForResult(intent,0);
				
			}
        	
        });
     
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
  	  if(requestCode==0&&resultCode==0){
  		  Bundle data=intent.getExtras();
  		  String resultCity=data.getString("city");
  		  city.setText(resultCity);
  	  }
    }



}
