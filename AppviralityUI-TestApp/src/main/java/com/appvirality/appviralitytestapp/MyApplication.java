package com.appvirality.appviralitytestapp;

import org.json.JSONObject;

import android.app.Application;
import android.util.Log;

import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.ConversionEventListner;

public class MyApplication extends Application {

	private static MyApplication singleton;

	@Override
	public void onCreate() {
		super.onCreate();

		singleton = this;
		
		//Check for FriendReward, Listner will get called if there is any Friend Reward.
		AppviralityAPI.checkFriendReward(getApplicationContext(), new AppviralityAPI.FriendRewardListner() {			
			@Override
			public void onFriendReward(JSONObject friendRewardListner) {
				try {
					//Use the following code block 
					//to mark the friend reward as Distributed if the reward type is in-store credits
					//to approve the dynamic coupon if the reward type is Dynamic Coupon.
					//Example: You will call this method after rewarding the user with In-store credits(wallet balance).
					/*JSONObject acceptedList = new JSONObject();
					acceptedList.put("rewardid", friendRewardListner.getString("rewardid"));
					acceptedList.put("reward_type", friendRewardListner.getString("reward_type"));	
					acceptedList.put("status", "Distribute");				
					// add this call after accepting the reward to confirm.
					AppviralityAPI.acceptReward(new JSONObject().put("rewards", new JSONArray().put(acceptedList)));*/
					Log.d("AppviralityAPI", "Friend Reward Details : " + friendRewardListner);
				}
				catch(Exception e) {
					e.printStackTrace();
				}					
			}
		});

		AppviralityAPI.onSuccessfullConversion(getApplicationContext(), new ConversionEventListner() {			
			@Override
			public void onConversionEventSuccess(JSONObject conversionDetails) {
				Log.d("AppviralityAPI", "user SaveConversionEvent result : " + conversionDetails);				
			}
		});
	}

	public static MyApplication getInstance() {
		return singleton;
	}



}