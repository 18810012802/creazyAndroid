package com.example.async;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.example.async.MainActivity.DownTask;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	private TextView show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            show=(TextView)findViewById(R.id.show);
            
            return rootView;
        }
    }
    public void download(View source) throws Exception{
		DownTask task=new DownTask(this);
		task.execute(new URL("http://www.crazyit.org/ethos.php"));
		
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
				while((line=br.readLine())!=null){
					sb.append(line+"\n");
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
