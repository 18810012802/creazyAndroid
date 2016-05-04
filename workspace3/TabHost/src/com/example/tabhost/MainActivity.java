package com.example.tabhost;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.os.Build;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabHost tabHost=getTabHost();
        TabSpec tab1=tabHost.newTabSpec("tab01").setIndicator("已接电话").setContent(R.id.tab01);
        tabHost.addTab(tab1);
        TabSpec tab2=tabHost.newTabSpec("tab02").setIndicator("呼出电话",getResources().getDrawable(R.drawable.ic_launcher)).setContent(R.id.tab02);
        tabHost.addTab(tab2);
        TabSpec tab3=tabHost.newTabSpec("tab03").setIndicator("未接电话").setContent(R.id.tab03);
        tabHost.addTab(tab3);
        
        
    }
}
