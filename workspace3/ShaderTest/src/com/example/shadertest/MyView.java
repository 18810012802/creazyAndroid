package com.example.shadertest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MyView extends View{
	public static Paint paint=new Paint();
	private Bitmap bitmap;
	private Matrix matrix;
	public RectF rect;
	public MyView(Context context,AttributeSet set){
		super(context,set);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.tt);
		matrix=new Matrix();
		rect=new RectF(10,130,410,430);
		paint.setStyle(Style.FILL);
	}
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		System.out.println("========================"+paint.getShader());
		//canvas.drawBitmap(bitmap, matrix, paint);
		canvas.drawColor(Color.BLACK);
		canvas.drawRect(rect, paint);
	}
}
