package com.example.drawable;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.ClipDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	MediaPlayer mediaPlayer1=null;
	MediaPlayer mediaPlayer2=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView=(ImageView)findViewById(R.id.image);
        final ImageView imageView1=(ImageView)findViewById(R.id.image1);
        final Animation anim=AnimationUtils.loadAnimation(this,R.anim.my_anim);
        anim.setFillAfter(true);
        Button bn=(Button)findViewById(R.id.bn);
        LinearLayout container=(LinearLayout)findViewById(R.id.container);
        container.addView(new MyAnimationView(this));
       
        bn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				imageView1.startAnimation(anim);
			}
        	}
        );
        final ClipDrawable drawable=(ClipDrawable)imageView.getDrawable();
        final Handler handler=new Handler(){
        	@Override
        	public void handleMessage(Message msg){
        		if(msg.what==0x1233){
        			drawable.setLevel(drawable.getLevel()+200);
        			
        		}
        	}
        };
        final Timer timer=new Timer();
        timer.schedule(new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg=new Message();
				msg.what=0x1233;
				handler.sendMessage(msg);
				if(drawable.getLevel()>=10000){
					timer.cancel();
				}
			}
        	
        }, 0,300);
        
        mediaPlayer1=MediaPlayer.create(this,R.raw.luvletter);
        
        
        AssetManager am=getAssets();
        try{
        	AssetFileDescriptor afd=am.openFd("skycity.mp3");
        	mediaPlayer2=new MediaPlayer();
        	mediaPlayer2.setDataSource(afd.getFileDescriptor());
        	mediaPlayer2.prepare();
        }catch(IOException e){
        	e.printStackTrace();
        }
        
        Button playRaw=(Button)findViewById(R.id.raw);
        Button playAsset=(Button)findViewById(R.id.asset);
        playRaw.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mediaPlayer2.isPlaying()){
					mediaPlayer2.stop();
				}else{
					mediaPlayer2.start();
				}
				
			}
        	
        });
        playAsset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mediaPlayer1.isPlaying()){
					mediaPlayer1.stop();
				}else{
					mediaPlayer1.start();
				}
			}
        	
        });
    }

    
    	
	@SuppressLint("NewApi") class MyAnimationView extends View{
		public MyAnimationView(Context context){
			super(context);
			try{
				ObjectAnimator colorAnim=(ObjectAnimator)AnimatorInflater.loadAnimator(MainActivity.this,R.anim.color_anim);
				colorAnim.setEvaluator(new ArgbEvaluator());
				colorAnim.setTarget(this);
				colorAnim.start();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}

}
