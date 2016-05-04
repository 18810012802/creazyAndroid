package com.example.sysactionofmessage;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	final int PICK_CONTACT=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bn=(Button)findViewById(R.id.bn);
        bn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setAction(Intent.ACTION_GET_CONTENT);
				intent.setType("vnd.android.cursor.item/phone");
				startActivityForResult(intent,PICK_CONTACT);
				
			}
        	
        });
        		
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode){
    	case (PICK_CONTACT):
    		if(requestCode==Activity.RESULT_OK){
    			Uri contactData=data.getData();
    			CursorLoader cursorLoader=new CursorLoader(this,contactData,null,null,null,null);
    			Cursor cursor=cursorLoader.loadInBackground();
    			if(cursor.moveToFirst()){
    				String contactId=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
    				String name=cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
    				String phoneNumber="����ϵ����δ����绰����";
    				Cursor phones=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+contactId, null, null);
    				if(phones.moveToFirst()){
    					phoneNumber=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    					
    				}
    				phones.close();
    				EditText show=(EditText)findViewById(R.id.show);
    				show.setText(name);
    				EditText phone=(EditText)findViewById(R.id.phone);
    				phone.setText(phoneNumber);
    			}
    			cursor.close();
    		}
    	break;
    	}
    	
    }

}