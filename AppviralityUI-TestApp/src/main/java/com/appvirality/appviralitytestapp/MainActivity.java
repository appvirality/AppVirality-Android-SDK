package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.appvirality.AppviralityUI;

public class MainActivity extends Activity {

	private final String LAUNCHCODE = "appvirality.sampleapp.launchmode";
	private final int REQUEST_CODE = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Show personalized welcome screen for new users
		AppviralityUI.showWelcomeScreen(MainActivity.this, REQUEST_CODE);

		//Option:1 - Launch from custom button i.e from "Invite Friends" or "Refer & Earn" button on your App menu
		findViewById(R.id.growthhack).setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				AppviralityUI.showGrowthHack(MainActivity.this, AppviralityUI.GH.Word_of_Mouth);
			}
		});

		//Option - 2 : Launch Bar (i.e Growth Hack will be launched from a Mini notification)
		findViewById(R.id.launchbar).setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
				intent.putExtra(LAUNCHCODE, true);
				startActivity(intent);
			}
		});

		//Option - 3 : Launch Popup (i.e Growth Hack will be launched from a Popup notification)
		findViewById(R.id.launchpopup).setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
				intent.putExtra(LAUNCHCODE, false);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == REQUEST_CODE) && (resultCode == -1))
		{
			Intent intent = new Intent(MainActivity.this, Registration.class);
			startActivity(intent);

		}


	}


}
