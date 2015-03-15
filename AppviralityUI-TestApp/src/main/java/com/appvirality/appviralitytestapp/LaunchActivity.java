package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.os.Bundle;

import com.appvirality.AppviralityUI;
import com.appvirality.android.AppviralityAPI;

public class LaunchActivity extends Activity {

	private final String LAUNCHCODE = "appvirality.sampleapp.launchmode";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		boolean isLaunchBar = getIntent().getBooleanExtra(LAUNCHCODE, true);

		if(isLaunchBar) {
			// show Launch Bar (i.e Growth Hack will be launched from a Mini notification)
			AppviralityUI.showLaunchBar(this, AppviralityUI.GH.Word_of_Mouth);
		}
		else {
			// show Launch Popup (i.e Growth Hack will be launched from a Popup notification)
			AppviralityUI.showLaunchPopup(this, AppviralityUI.GH.Word_of_Mouth);
		}
	}

	@Override
	public void onStop() {
	    super.onStop();
	    AppviralityAPI.onStop();
	}
}
