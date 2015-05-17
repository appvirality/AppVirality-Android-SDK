/*
package com.appvirality;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.appvirality.android.AppviralityAPI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegistration 
{
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";

	static String SENDER_ID = "YOUR-SENDER-ID";

	*/
/**
	 * Tag used on log messages.
	 *//*

	static final String TAG = "GCM-AppVirality";
	private static GoogleCloudMessaging gcm;
	private static Context context;
	private static String regid;

	public static void registerGCM(Context mContext)
	{
		if(mContext == null)
			return;
		context = mContext;
		if(checkPlayServices()) {
			if(!checkGCMConfiguration(context))
				return;
				
			gcm = GoogleCloudMessaging.getInstance(context);
			regid = getRegistrationId(context);
			if(TextUtils.isEmpty(regid)) {
				registerInBackground();
			}
			else
			{
				setPushRegID();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	*/
/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 *//*

	private static boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS)
			return false;
		return true;
	}

	*/
/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 *//*

	private static void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	*/
/**
	 * Gets the current registration ID for application on GCM service, if there is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 *//*

	private static String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.equals("")) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	*/
/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 *//*

	private static void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {            	
				// set appvirality gcm register id using userdetails update method.
				if(!TextUtils.isEmpty(regid))
					setPushRegID();
				Log.i(TAG, msg);
			}
		}.execute(null, null, null);
	}

	*/
/**
	 * @return Application's version code from the {@code PackageManager}.
	 *//*

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	*/
/**
	 * @return Application's {@code SharedPreferences}.
	 *//*

	private static SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return context.getSharedPreferences(GCMRegistration.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private static void setPushRegID()
	{
		AppviralityAPI.UserDetails.setInstance(context)
		.setPushRegID(regid)
		.Update();
		
		Log.d("", "user push reg id is " + regid); 
	}

	public static boolean checkGCMConfiguration(Context context) {
        if (Build.VERSION.SDK_INT < 8) {
            Log.i(TAG, "push notifications not supported in SDK " + Build.VERSION.SDK_INT);
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();
        final String packageName = context.getPackageName();
        final String permissionName = packageName + ".permission.C2D_MESSAGE";
        
        try {
            packageManager.getPermissionInfo(permissionName, PackageManager.GET_PERMISSIONS);
        } catch (final NameNotFoundException e) {
            Log.w(TAG, "Application does not define permission " + permissionName);
            Log.i(TAG, "You will need to add the following lines to your application manifest:\n" +
                    "<permission android:name=\"" + packageName + ".permission.C2D_MESSAGE\" android:protectionLevel=\"signature\" />\n" +
                    "<uses-permission android:name=\"" + packageName + ".permission.C2D_MESSAGE\" />");
            return false;
        }
       
        if (PackageManager.PERMISSION_GRANTED != packageManager.checkPermission("com.google.android.c2dm.permission.RECEIVE", packageName)) {
            Log.w(TAG, "Package does not have permission com.google.android.c2dm.permission.RECEIVE");
            Log.i(TAG, "You can fix this by adding the following to your AndroidManifest.xml file:\n" +
                    "<uses-permission android:name=\"com.google.android.c2dm.permission.RECEIVE\" />");
            return false;
        }

        if (PackageManager.PERMISSION_GRANTED != packageManager.checkPermission("android.permission.INTERNET", packageName)) {
            Log.w(TAG, "Package does not have permission android.permission.INTERNET");
            Log.i(TAG, "You can fix this by adding the following to your AndroidManifest.xml file:\n" +
                    "<uses-permission android:name=\"android.permission.INTERNET\" />");
            return false;
        }

        if (PackageManager.PERMISSION_GRANTED != packageManager.checkPermission("android.permission.WAKE_LOCK", packageName)) {
            Log.w(TAG, "Package does not have permission android.permission.WAKE_LOCK");
            Log.i(TAG, "You can fix this by adding the following to your AndroidManifest.xml file:\n" +
                    "<uses-permission android:name=\"android.permission.WAKE_LOCK\" />");
            return false;
        }

        if (PackageManager.PERMISSION_GRANTED != packageManager.checkPermission("android.permission.GET_ACCOUNTS", packageName)) {
            Log.i(TAG, "Package does not have permission android.permission.GET_ACCOUNTS");
            Log.i(TAG, "You can fix this by adding the following to your AndroidManifest.xml file:\n" + 
            "<uses-permission android:name=\"android.permission.GET_ACCOUNTS\" />");
        }

        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
        } catch (final NameNotFoundException e) {
            Log.w(TAG, "Could not get receivers for package " + packageName);
            return false;
        }

        return true;        
    }
	
}
*/
