package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.appvirality.android.AppviralityAPI;


public class Registration extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);


		findViewById(R.id.Register).setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				final ProgressDialog progressDialog = new ProgressDialog(Registration.this);
				progressDialog.setMessage("Please wait...");
				progressDialog.setCancelable(true);
				progressDialog.show();
				AppviralityAPI.claimRewardOnSignUp(getApplicationContext(), new AppviralityAPI.RewardClaimed() {    
					@Override
					public void OnResponse(boolean isRewarded, String message) {
						try {
							if(progressDialog != null && progressDialog.isShowing())
								progressDialog.dismiss();
							if(isRewarded)
							{
								Toast.makeText(getApplicationContext(), message , Toast.LENGTH_LONG).show();
							}
							else
							{
								Toast.makeText(getApplicationContext(), "Sorry..! Reward is only first time app users, But you can still earn by referring your friends" , Toast.LENGTH_LONG).show();
							}
						}
						catch(Exception e) {

						}
					}
				});
			}
		});
	}

}
