package com.hawkjm.okgooglenowtriggerintent;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.support.v4.app.*;
import android.content.*;

public class MainActivity extends Activity{

	public static Context appContext;
	
	public static Context getAppContext(){
		return appContext;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {         

        super.onCreate(savedInstanceState);   
		appContext = getApplicationContext();
        setContentView(R.layout.main);
    }
}
