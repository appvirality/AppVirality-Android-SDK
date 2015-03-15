package com.appvirality;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.CampaignDetails;

public class AVLaunchBar {
	private Activity activity;
	private static PopupWindow launchBarPopup;
	private static Dialog LaunchBarDialog;

	public AVLaunchBar(Activity activity) {
		this.activity = activity; 
	}

	public void showLaunchBanner(CampaignDetails campaignDetails) {
		showLaunchPopupWindow(campaignDetails, true);
	}

	public void showLaunchPopup(CampaignDetails campaignDetails) {
		showLaunchPopupWindow(campaignDetails, false);
	}	

	@SuppressWarnings("deprecation")
	public void showLaunchPopupWindow(final CampaignDetails campaignDetails, boolean isLaunchBar) {
		try {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			View launchbarLayout;
			if(isLaunchBar)
				launchbarLayout = inflater.inflate(R.layout.appvirality_lauchbar, null);
			else
				launchbarLayout = inflater.inflate(R.layout.appvirality_launch_popup, null);
			Button btnLaunchPopup = (Button) launchbarLayout.findViewById(R.id.appvirality_btnlaunchbar);
			Button btnRememberLater = (Button) launchbarLayout.findViewById(R.id.appvirality_btnremindlater);
			ImageView launchIcon = (ImageView) launchbarLayout.findViewById(R.id.appvirality_launchimage);
			launchIcon.setImageResource(getLaunchIcon(campaignDetails.LaunchIconId));
			TextView txtLaunchMessage = (TextView) launchbarLayout.findViewById(R.id.appvirality_txtlaunchmessage);
			txtLaunchMessage.setText(Html.fromHtml(TextUtils.isEmpty(campaignDetails.LaunchMessage) ? campaignDetails.OfferTitle : campaignDetails.LaunchMessage));
			if(campaignDetails.LaunchMsgColor != null)
				txtLaunchMessage.setTextColor(Color.parseColor(campaignDetails.LaunchMsgColor));
			if(!TextUtils.isEmpty(campaignDetails.RemindButtonText))
				btnRememberLater.setText(campaignDetails.RemindButtonText);
			if(!TextUtils.isEmpty(campaignDetails.LaunchButtonText))
				btnLaunchPopup.setText(campaignDetails.LaunchButtonText);
			GradientDrawable shape =  new GradientDrawable();
			shape.setCornerRadii(new float [] { 1, 1, 0, 0, 1, 1, 0, 0});			
			shape.setStroke(1, Color.parseColor("#FFFFFF"));
			shape.setBounds(0, 1, 1, 0);
			if(campaignDetails.LaunchButtonTextColor != null) {
				btnLaunchPopup.setTextColor(Color.parseColor(campaignDetails.LaunchButtonTextColor));
				btnRememberLater.setTextColor(Color.parseColor(campaignDetails.LaunchButtonTextColor));
			}
			if(campaignDetails.LaunchButtonBGColor != null) {		
				shape.setColor(Color.parseColor(campaignDetails.LaunchButtonBGColor));

				if (android.os.Build.VERSION.SDK_INT >= 16) {
					btnLaunchPopup.setBackground(shape);
					btnRememberLater.setBackground(shape);
				}
				else {
					btnLaunchPopup.setBackgroundDrawable(shape);
					btnRememberLater.setBackgroundDrawable(shape);
				}
			}

			launchbarLayout.setBackgroundColor(Color.parseColor(campaignDetails.LaunchBGColor));
						
			if(isLaunchBar) {
				DisplayMetrics metrics = new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int popupWidth = metrics.widthPixels;
				int popupHeight = AppviralityAPI.getScreenOrientation(activity) == 1 ? (int) (metrics.heightPixels * 0.2) : (int) (metrics.widthPixels * 0.2);
				if(launchBarPopup == null ? true : !launchBarPopup.isShowing()) {
					launchBarPopup = new PopupWindow(launchbarLayout, popupWidth, popupHeight, false); 
					launchBarPopup.setAnimationStyle(R.style.appvirality_slide_activity);
					launchBarPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
					launchBarPopup.showAtLocation(launchbarLayout, Gravity.BOTTOM, 0, 0);
					AppviralityAPI.setLaunchBarPopup(launchBarPopup);
				}
			}
			else {
				if(LaunchBarDialog == null ? true : !LaunchBarDialog.isShowing()) {
					LaunchBarDialog = new Dialog(activity);
					LaunchBarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);				
					LaunchBarDialog.setCancelable(true);
					LaunchBarDialog.setContentView(launchbarLayout);
					LaunchBarDialog.show();
					AppviralityAPI.setLaunchBarDialog(LaunchBarDialog);
				}					
			}

			btnLaunchPopup.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					try {
						CampaignHandler.showGrowthHack(activity, campaignDetails.growthHackType, campaignDetails);
						dismissLaunchBar();
					}
					catch(Exception e)
					{							
					}
				}
			});
			btnRememberLater.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					try {
						AppviralityAPI.remindLater(campaignDetails);
						dismissLaunchBar();	
					}
					catch(Exception e)
					{							
					}
				}
			});

			AppviralityAPI.ViewedCampaign(campaignDetails.CampaignId);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}	

	private void dismissLaunchBar() {
		try {
			if(launchBarPopup != null && launchBarPopup.isShowing())
				launchBarPopup.dismiss();

			if(LaunchBarDialog != null && LaunchBarDialog.isShowing())
				LaunchBarDialog.dismiss();
		}
		catch(Exception e) {

		}
	}

	private int getLaunchIcon(int launchId) {
		switch(launchId) {
		case 1 : return R.drawable.appvirality_megaphone;
		case 2 : return R.drawable.appvirality_alert;
		case 3 : return R.drawable.appvirality_bell;
		case 4 : return R.drawable.appvirality_flag;
		case 5 : return R.drawable.appvirality_trofy;
		default : return R.drawable.appvirality_megaphone;
		}				
	}

}
