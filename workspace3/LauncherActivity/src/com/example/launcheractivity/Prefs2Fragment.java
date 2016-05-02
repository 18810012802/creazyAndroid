package com.example.launcheractivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Toast;


@SuppressLint("NewApi") public class Prefs2Fragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.display_prefs);
		String website=getArguments().getString("website");
		Toast.makeText(getActivity(), "ÍøÕ¾ÓòÃûÊÇ"+website,Toast.LENGTH_LONG).show();;
	}
}
