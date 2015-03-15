package com.appvirality;

import android.app.Activity;
import android.content.Context;

import com.appvirality.android.AVEnums;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.AppviralityAPI.CampaignReadyListner;
import com.appvirality.android.CampaignDetails;

public class AppviralityUI extends AVEnums
{	
	public static void showLaunchBar(final Activity activity, AppviralityUI.GH growthType)
	{
		AppviralityAPI.setCampaignHandler(activity, growthType, new CampaignReadyListner() {			
			@Override
			public void onCampaignReady(CampaignDetails campaignDetails) {
				CampaignHandler.showLaunchBar(activity, campaignDetails);
			}
		});     
	}
	
	public static void showLaunchPopup(final Activity activity, AppviralityUI.GH growthType)
	{
		AppviralityAPI.setCampaignHandler(activity, growthType, new CampaignReadyListner() {			
			@Override
			public void onCampaignReady(CampaignDetails campaignDetails) {
				CampaignHandler.showLaunchPopup(activity, campaignDetails);
			}
		});
	}
	
	public static void showGrowthHack(final Activity activity, final AppviralityUI.GH growthHackType)
	{
		CampaignHandler.showGrowthHack(activity, growthHackType, null);
	}
	
	public static void showWelcomeScreen(Context context)
	{
		CampaignHandler.showWelcomeScreen(context);
	}
}
