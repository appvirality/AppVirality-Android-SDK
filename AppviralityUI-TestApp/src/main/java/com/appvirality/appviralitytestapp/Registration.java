package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.appvirality.android.AppviralityAPI;


public class Registration extends Activity {

	ProgressDialog progressDialog;
	EditText editTextrefcode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		editTextrefcode = (EditText)findViewById(R.id.editTextReferralCode);
		if(!TextUtils.isEmpty(AppviralityAPI.getFriendReferralCode())) {
			editTextrefcode.setText(AppviralityAPI.getFriendReferralCode().toUpperCase());
		}

		findViewById(R.id.Register).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressDialog = new ProgressDialog(Registration.this);
				progressDialog.setMessage("Please wait...");
				progressDialog.setCancelable(true);
				progressDialog.show();
				String refcode = editTextrefcode.getText().toString();
				if(!TextUtils.isEmpty(refcode)) {

					AppviralityAPI.SubmitReferralCode(refcode, new AppviralityAPI.SubmitReferralCodeListner() {
						@Override
						public void onResponse(boolean isSuccess) {
							if (isSuccess) {
								Log.i("AppViralitySDK : ", "Referral Code applied Successfully");
							}
							else
							{
								Toast.makeText(Registration.this, "Failed to apply referral code", Toast.LENGTH_SHORT).show();
							}
							submitSignupConversionEvent();
						}

					});
				}else
				{
					submitSignupConversionEvent();
				}


			}
		});


	}

	protected void submitSignupConversionEvent()
	{
		//Send Signup Conversion Event to claim the reward.
		AppviralityAPI.claimRewardOnSignUp(getApplicationContext(), new AppviralityAPI.RewardClaimed() {
			@Override
			public void OnResponse(boolean isRewarded, String message) {
				try {
					if(progressDialog != null && progressDialog.isShowing())
					{
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (isRewarded) {
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), "Sorry..! Reward is only first time app users, But you can still earn by referring your friends", Toast.LENGTH_LONG).show();
					}
				} catch (Exception e) {

				}
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(progressDialog != null && progressDialog.isShowing())
		{
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

}
