package com.example.matrix;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private Button bn;
	private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MyView mv=(MyView)findViewById(R.id.matrix);
        
        bn=(Button)findViewById(R.id.bn);
		et=(EditText)findViewById(R.id.et);
		bn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mv.onKey(Integer.parseInt(et.getText().toString()));
			}
			
		});
    }

}
