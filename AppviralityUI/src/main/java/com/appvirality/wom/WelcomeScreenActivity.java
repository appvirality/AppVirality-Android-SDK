package com.appvirality.wom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appvirality.CampaignHandler;
import com.appvirality.R;
import com.appvirality.android.AppviralityAPI;

public class WelcomeScreenActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.appvirality_welcomescreen);
		try {
			final ReferrerDetails referrerDetails = CampaignHandler.getReferrerDetails();
			TextView txtReferrerDesc = (TextView) findViewById(R.id.appvirality_reward_details);			
			final EditText userEmail = (EditText) findViewById(R.id.appvirality_edittext_email);
			Button btnClaim = (Button) findViewById(R.id.appvirality_btnclaim);
			TextView txtSkipReferrer = (TextView) findViewById(R.id.appvirality_skip_welcome);
			ImageView imgProfile = (ImageView) findViewById(R.id.appvirality_user_profile);
			Button btnSignUp = (Button) findViewById(R.id.appvirality_btnsignup);

			txtReferrerDesc.setText(referrerDetails.WelcomeMessage);
			if(!TextUtils.isEmpty(referrerDetails.OfferTitleColor))
				txtReferrerDesc.setTextColor(Color.parseColor(referrerDetails.OfferTitleColor));
			if(referrerDetails.ProfileImage != null) {
				imgProfile.setImageBitmap(referrerDetails.ProfileImage);
			}
			if(!referrerDetails.FriendRewardEvent.equalsIgnoreCase("Install")) {				
				btnClaim.setVisibility(View.INVISIBLE);
				userEmail.setVisibility(View.INVISIBLE);
				txtSkipReferrer.setText("Close");				
			}
			if(referrerDetails.FriendRewardEvent.equalsIgnoreCase("Signup"))
				btnSignUp.setVisibility(View.VISIBLE);
			if(referrerDetails.isEmailExists)
				userEmail.setVisibility(View.GONE);
			else
				if(AppviralityAPI.hasGetAccountsPermission(getApplicationContext()))
					userEmail.setText(AppviralityAPI.getEmailIdFromAccounts(WelcomeScreenActivity.this));
			if(!TextUtils.isEmpty(referrerDetails.CampaignBGColor))
				findViewById(R.id.appvirality_popup).setBackgroundColor(Color.parseColor(referrerDetails.CampaignBGColor));
			btnClaim.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {				
					if (referrerDetails.isEmailExists ? true : Patterns.EMAIL_ADDRESS.matcher(userEmail.getText().toString().trim()).matches()) {
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

						setFriendRewardListner(userEmail.getText().toString().trim());
					}
					else {
						Toast.makeText(WelcomeScreenActivity.this, "please enter valid email address", Toast.LENGTH_SHORT).show();
					}
				}
			});

			txtSkipReferrer.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});	

			btnSignUp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {					
					// add code to redirecting user to register activity
					//AppviralityAPI.saveConversionEvent("Signup", null, null);
					
					AppviralityAPI.claimRewardOnSignUp(getApplicationContext(), new AppviralityAPI.RewardClaimed() {    
					     @Override
					     public void OnResponse(boolean isRewarded, String message) {
					      try {
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
					
					finish();
				}
			});
		}
		catch(Exception e) {
			finish();
		}
	}	

	private void setFriendRewardListner(String email)
	{
		try {
			final ProgressDialog progressDialog = new ProgressDialog(WelcomeScreenActivity.this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setCancelable(true);
			progressDialog.show();

			AppviralityAPI.claimRewardOnInstall(getApplicationContext(), email, new AppviralityAPI.RewardClaimed() {				
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
						
						finish();
					}
					catch(Exception e) {
						
					}
				}
			});
		}
		catch(Exception e) {			
			finish();
		}
	}
}
