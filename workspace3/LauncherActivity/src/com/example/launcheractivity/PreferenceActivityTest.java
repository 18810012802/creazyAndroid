package com.example.launcheractivity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.widget.Button;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB) 
public class PreferenceActivityTest extends PreferenceActivity {
	private static List<String> fragments=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(hasHeaders()){
			Button button=new Button(this);
			button.setText("���ò���");
			setListFooter(button);
		}
	}
	@Override
	public void onBuildHeaders(List<Header> target){
		loadHeadersFromResource(R.layout.preference_headers,target);
		fragments.clear();
		for(Header header:target){
			fragments.add(header.fragment);
		}
	}
	public static class Prefs1Fragment extends PreferenceFragment{
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
		}
	}
	public static class Prefs2Fragment extends PreferenceFragment{
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.display_prefs);
			String website=getArguments().getString("website");
			Toast.makeText(getActivity(), "��վ������"+website,Toast.LENGTH_LONG).show();;
		}
	}
	@Override
	public boolean isValidFragment(String fragmentName){
		return fragments.contains(fragmentName);
	}
}
