package com.example.asynctask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AsyncTaskTest extends Activity{
	ProgressDialog pdialog2;
	private TextView show;
	private Button bn;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		show=(TextView)findViewById(R.id.show);
		bn=(Button)findViewById(R.id.bn);
		bn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				DownTask task=new DownTask(AsyncTaskTest.this);
				
				try {
					//task.execute(new URL("http://www.crazyit.org/ethos.php"));
					task.execute(new URL("http://www.baidu.com.cn"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
			}
			
		});
		
	}
	class DownTask extends AsyncTask<URL,Integer,String>{
		ProgressDialog pdialog;
		int hasRead=0;
		Context mContext;
		public DownTask(Context ctx){
			mContext=ctx;
		}
		@Override
		protected String doInBackground(URL... params) {
			// TODO Auto-generated method stub
			StringBuilder sb=new StringBuilder();
			try{
				URLConnection conn=params[0].openConnection();
				BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String line=null;
				Timer t=new Timer();
				while((line=br.readLine())!=null){
					sb.append(line+"\n");
					//new Timer().wait(10);
					Thread.sleep(100);
					System.out.println("又过了一秒");
					hasRead++;
					publishProgress(hasRead);
				}
				return sb.toString();
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result){
			show.setText(result);
			pdialog.dismiss();
			
		}
		@Override
		protected void onPreExecute(){
			pdialog=new ProgressDialog(mContext);
			pdialog.setTitle("任务正在执行中");
			pdialog.setMessage("任务正在执行中，敬请等待...");
			pdialog.setCancelable(false);
			pdialog.setMax(202);
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pdialog.setIndeterminate(false);
			pdialog.show();
		}
		@Override
		protected void onProgressUpdate(Integer...values){
			show.setText("已经读取了"+values[0]+"行!");
			pdialog.setProgress(values[0]);
		}
		
	}
	
}
