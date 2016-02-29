package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.appvirality.android.AppviralityAPI;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// your campaigns will be ready in the background by the time your app gets loads, without effecting the user experience
		AppviralityAPI.init(getApplicationContext());

		final Intent myIntent = new Intent(this, MainActivity.class);
		
		Thread logoTimer = new Thread(){
			public void run(){
				try{
					int logoTimer = 0;
					while (logoTimer<3000){
						sleep(100);
						logoTimer=logoTimer+200;
					}
					startActivity(myIntent);
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
				finally{
					finish();
				}
			}
		};
		logoTimer.start();
	}

}
