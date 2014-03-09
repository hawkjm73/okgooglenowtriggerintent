package com.hawkjm.okgooglenowtriggerintent;

import android.app.Fragment;
import android.content.*;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.*;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener
{
	Context appContext;
	SharedPreferences sharedPref;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//appContext = MainActivity.getAppContext();
		//Make the settings world readable. Without this, the hooks, running in audiomonitor's context, can't read them.
		getPreferenceManager().setSharedPreferencesMode(1);
		addPreferencesFromResource(R.xml.preferences);
		
		//Setup a listener so the ui can be refreshed if an external intent changes the settings
		sharedPref = getPreferenceScreen().getSharedPreferences();
		sharedPref.registerOnSharedPreferenceChangeListener(this);
		
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		sharedPref.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		//Log.w("OkGoogleNowTriggerIntent", "Shared Pref Change");
		this.onCreate(null);
		//addPreferencesFromResource(R.xml.preferences);
	}
}
