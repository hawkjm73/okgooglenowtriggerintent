package com.hawkjm.okgooglenowtriggerintent;

import android.content.*;
import android.preference.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.*;
import de.robv.android.xposed.IXposedHookZygoteInit.*;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.XSharedPreferences;

public class AudioTrigger implements IXposedHookLoadPackage
{
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("com.motorola.audiomonitor")){
			findAndHookMethod("com.motorola.audiomonitor.service.AudioDspControl", lpparam.classLoader, "handleKeyPhraseRecognized", new XC_MethodHook(){
				protected void beforeHookedMethod(MethodHookParam param){
					//Check the preferences.
					XSharedPreferences prefs = new XSharedPreferences("com.hawkjm.okgooglenowtriggerintent");
					boolean canIntent = prefs.getBoolean("pref_intent", true);
					boolean canOverride = prefs.getBoolean("pref_override", true);
					if (canIntent){
						//Grab a context and send the intent indicating a keyword recognition.
						Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
						Context context = (Context)XposedHelpers.callMethod(activityThread, "getSystemContext");
						Intent i = new Intent();
						i.setAction("com.hawkjm.okgooglenowtriggerintent.AUDIO_TRIGGER");
						context.sendBroadcast(i);
					}
					if (canOverride){
						// Stopping this method stops all recognition activity afterwords, but it also stops it from working again. The afterHooked will reset the service.
						param.setResult(null);
					}
				}
				
					protected void afterHookedMethod(MethodHookParam param){
						//Check the preferences.
						XSharedPreferences prefs = new XSharedPreferences("com.hawkjm.okgooglenowtriggerintent");
						boolean canOverride = prefs.getBoolean("pref_override", true);
						if (canOverride){
							//Grab a context, stop and restart the server, so future recognition can happen.
							Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
							Context context = (Context)XposedHelpers.callMethod(activityThread, "getSystemContext");
							Intent servIntent = new Intent();
							servIntent.setClassName("com.motorola.audiomonitor", "com.motorola.audiomonitor.MonitorService");
							context.stopService(servIntent);
							context.startService(servIntent);
						}
					}
			});
		}
	}
}
	


