package com.example.helloworld;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
	static final String UPPER_NUM="upper";
	EditText etNum;
	Button btn;
	CalThread calThread;
	class CalThread extends Thread{
		public Handler mHandler;
		public void run(){
			Looper.prepare();
			mHandler=new Handler(){
				@Override
				public void handleMessage(Message msg){
					if(msg.what==0x123){
						int upper=msg.getData().getInt(UPPER_NUM);
						List<Integer> nums=new ArrayList<Integer>();
						outer:
							for(int i=2;i<=upper;i++){
								for(int j=2;j<=Math.sqrt(i);j++){
									if(i!=2&&i%j==0){
										continue outer;
									}
								}
								nums.add(i);
							}
						Toast.makeText(MainActivity.this, nums.toString(),Toast.LENGTH_LONG).show();
					}
				}
			};
			Looper.loop();
		}
		
	}
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNum=(EditText)findViewById(R.id.show);
        calThread=new CalThread();
        btn=(Button)findViewById(R.id.bn);
        btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cal(arg0);
			}
        	
        });
        calThread.start();
	}
	public void cal(View source){
		Message msg=new Message();
		msg.what=0x123;
		Bundle bundle=new Bundle();
		bundle.putInt(UPPER_NUM,Integer.parseInt(etNum.getText().toString()));
		msg.setData(bundle);
		calThread.mHandler.sendMessage(msg);
		
	}
	
}
