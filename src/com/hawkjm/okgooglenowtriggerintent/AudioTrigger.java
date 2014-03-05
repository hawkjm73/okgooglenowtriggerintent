package com.hawkjm.okgooglenowtriggerintent;

import android.content.*;
import android.net.Uri;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.*;
import de.robv.android.xposed.IXposedHookZygoteInit.*;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import java.lang.reflect.*;
import java.lang.Runnable;

public class AudioTrigger implements IXposedHookLoadPackage, IXposedHookZygoteInit
{
	public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable
	{
		findAndHookMethod("android.app.Instrumentation", null, "execStartActivity", "android.content.Context", "android.os.IBinder", "android.os.IBinder", "android.app.Activity", "android.content.Intent", "int", "android.os.Bundle", new XC_MethodHook(){
			protected void beforeHookedMethod(MethodHookParam param){
				Context context = (Context)param.args[0];
				if (context.toString().startsWith("com.motorola.audiomonitor")){
					//Touchless controls can launch different activities when triggered. Here, we look for and block them from starting.
					Intent intent = (Intent)param.args[4];
					//XposedBridge.log("*** Start Intent ***");
					if (intent.getAction() != null && intent.getAction().equals("com.google.android.googlequicksearchbox.VOICE_SEARCH_RECORDED_AUDIO")){
						//XposedBridge.log("Quick search box blocked");
						param.setResult(null);
					}
					if (intent.getComponent() != null && intent.getComponent().toString().equals("ComponentInfo{com.motorola.audiomonitor/com.motorola.audiomonitor.uis.AOVActivity}")){
						//XposedBridge.log("AOVActivity blocked");
						param.setResult(null);
					}
					if (intent.getComponent() != null && intent.getComponent().toString().equals("ComponentInfo{com.motorola.audiomonitor/com.motorola.audiomonitor.uis.AOVErrorActivity}")){
						//XposedBridge.log("AOVErrorActivity blocked");
						param.setResult(null);
					}
				}
			}
			
		});
		
		findAndHookMethod("android.media.MediaPlayer", null, "create", Context.class, Uri.class, new XC_MethodHook(){
			protected void beforeHookedMethod(MethodHookParam param){
				Context context = (Context)param.args[0];
				Uri uri = (Uri)param.args[1];
				//if (context.toString().contains("motorola")){
					XposedBridge.log("New Media Player: " + context.toString());
					XposedBridge.log("uri: " + uri.toString());
				//}
			}
		});
	}

	
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("com.motorola.audiomonitor")){
			//Handle different classes for Jelly Bean version
			String className = "com.motorola.audiomonitor.bc";
			String paramClassName = "com.motorola.audiomonitor.t";
			if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Jelly_Bean_MR1 || Build.VERSION.SDK_INT == Build.VERSION_CODES.Jelly_Bean_MR2){
				className = "com.motorola.audiomonitor.bb";
				paramClassName = "com.motorola.audiomonitor.s";	
			}
			//com.motorola.audiomonitor.bc.a(t) cancels a trigger event. Get the Method and parameter objects for it here.
			final Class mClass = findClass(className, lpparam.classLoader);
			Method[] mMethods = mClass.getDeclaredMethods();
			Method bcMethod = null;
			Object bcParaTypes = null;
			for (Method mMethod : mMethods){
				if (mMethod.getName().equals("a")&&getParametersString(mMethod.getParameterTypes()).equals("(" + paramClassName + ")")){
					bcMethod = mMethod;
					bcParaTypes = mMethod.getParameterTypes();
				}
			}
			final Method aMethod = bcMethod;
			final Object aParaTypes = bcParaTypes;
			
			findAndHookMethod(className, lpparam.classLoader, "a", "float", paramClassName, new XC_MethodHook(){
					protected void afterHookedMethod(MethodHookParam param){	
						//This method is always called when the key phrase is recognized.
						//Grab a context and send the intent.
						Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
						Context context = (Context)XposedHelpers.callMethod(activityThread, "getSystemContext");
						Intent i = new Intent();
						i.setAction("com.hawkjm.okgooglenowtriggerintent.AUDIO_TRIGGER");
						context.sendBroadcast(i);
						//Now, invoke the a(t) method to stop the recognizer from hogging the mic.
						if (aMethod != null && aParaTypes != null){
							try {
								aMethod.invoke(param.thisObject, param.args[1]);
							} catch(java.lang.reflect.InvocationTargetException|java.lang.IllegalAccessException ex){}
						}
					}
			});
		}
	}
	
	private static String getParametersString(Class<?>... clazzes) {
		//this is a helper method for determining the parameters for various methods in a class
		StringBuilder sb = new StringBuilder("(");
		boolean first = true;
		for (Class<?> clazz : clazzes) {
			if (first)
				first = false;
			else
				sb.append(",");

			if (clazz != null)
				sb.append(clazz.getCanonicalName());
			else
				sb.append("null");
		}
		sb.append(")");
		return sb.toString();
	}
}
	


