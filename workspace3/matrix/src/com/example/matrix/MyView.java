package com.example.matrix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyView extends View{
	private Matrix matrix=new Matrix();
	private Bitmap bitmap;
	private float sx=0.0f;
	private int width,height;
	private float scale=1.0f;
	private boolean isScale=false;
	private Button bn;
	private EditText et;
	public MyView(Context context,AttributeSet set){
		super(context,set);
		bitmap=((BitmapDrawable)context.getResources().getDrawable(R.drawable.rr)).getBitmap();
		width=bitmap.getWidth();
		height=bitmap.getHeight();
		this.setFocusable(true);
	}
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		matrix.reset();
		if(!isScale){
			matrix.setSkew(sx, 0);
			
		}else{
			matrix.setScale(scale,scale);
			
		}
		Bitmap bitmap2=Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);
		canvas.drawBitmap(bitmap2, matrix, null);
	}
	public void onKey(int keyCode){
		switch(keyCode){
		case 1:
			isScale=false;
			sx+=0.1;
			postInvalidate();
			break;
		case 2:
			isScale=false;
			sx-=0.1;
			postInvalidate();
			break;
		case 3:
			isScale=true;
			if(scale<2.0){
				scale+=0.1;
			}
			postInvalidate();
			break;
		case 4:
			isScale=true;
			if(scale>0.5){
				scale-=0.1;
			}
			postInvalidate();
			break;
		}
	}
}
