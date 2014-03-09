package com.hawkjm.okgooglenowtriggerintent;

import android.app.IntentService;
import android.content.*;
import android.preference.PreferenceManager;
import android.preference.*;

public class SettingsIntentService extends IntentService
{
	public SettingsIntentService(){
		super("SettingsIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Context appContext = getApplicationContext();
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
		boolean conEnabled = sharedPref.getBoolean("pref_allowcontrol", false);
		String act = intent.getStringExtra("SETTING");
		if (conEnabled && act != null && (act.equals("ENABLE_INTENT") || act.equals("ENABLE_BOTH"))) {
			sharedPref.edit().putBoolean("pref_intent", true).commit();
		}
		if (conEnabled && act != null && (act.equals("ENABLE_OVERRIDE") || act.equals("ENABLE_BOTH"))) {
			sharedPref.edit().putBoolean("pref_override", true).commit();
		}
		if (conEnabled && act != null && (act.equals("DISABLE_INTENT") || act.equals("DISABLE_BOTH"))) {
			sharedPref.edit().putBoolean("pref_intent", false).commit();
		}
		if (conEnabled && act != null && (act.equals("DISABLE_OVERRIDE") || act.equals("DISABLE_BOTH"))) {
			sharedPref.edit().putBoolean("pref_override", false).commit();
		}
	}
}
