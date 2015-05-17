package com.appvirality.wom;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appvirality.CampaignHandler;
import com.appvirality.R;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.CampaignDetails;

public class ShowGrowthHack extends Activity
{
	private ExpandableHeightGridView gridView;
	private GridViewAdapter customGridAdapter;
	private String customLinkSaved;
	ArrayList<com.appvirality.wom.Items> socialActions;
	private TextView txtShareLink;
	private String shareLink;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		try {
			setContentView(R.layout.appvirality_wom_growthhackscreen);
			final CampaignDetails campaignDetails = CampaignHandler.getCampiagnDetails();
			if(campaignDetails != null) {
				// accessing layoutresources
				final TextView txtOfferTitle = (TextView) findViewById(R.id.appvirality_title);
				final TextView txtOfferDescription = (TextView) findViewById(R.id.appvirality_desc);
				final TextView txtTotalEarnings = (TextView) findViewById(R.id.appvirality_earnings);
				final TextView txtReferrals = (TextView) findViewById(R.id.appvirality_growth_title);				
				final TextView txtShowMore = (TextView) findViewById(R.id.appvirality_show_more);
				final TextView txtNoSocialInstalled = (TextView) findViewById(R.id.appvirality_no_social_installed);
				final TextView txtTerms = (TextView) findViewById(R.id.appvirality_txtuserterms);
				final LinearLayout campaignImage = (LinearLayout) findViewById(R.id.appvirality_campaignimage);
				final LinearLayout referralShareUrl = (LinearLayout) findViewById(R.id.appvirality_custom_share_link);
				txtShareLink = (TextView) findViewById(R.id.appvirality_share_link);

				// displaying campaign details
				txtOfferTitle.setText(Html.fromHtml("<b>" + campaignDetails.OfferTitle + "</b>"));
				txtOfferDescription.setText(Html.fromHtml(campaignDetails.OfferDescription));
				gridView = (ExpandableHeightGridView) findViewById(R.id.appvirality_gridView);
				if(campaignDetails.NoSocialActionsFound) {
					txtNoSocialInstalled.setText(campaignDetails.NoSocialActionsMessage);
					txtNoSocialInstalled.setVisibility(View.VISIBLE);
				}
				else {
					setSocialActions(campaignDetails, false, campaignDetails.isCustomTemplete, ShowGrowthHack.this);
					if(campaignDetails.AllSocialActions.size() <= 6)
						txtShowMore.setVisibility(View.GONE);
					else
						txtShowMore.setVisibility(View.VISIBLE);
				}		
				referralShareUrl.setOnClickListener(new View.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(View view) {
						if(android.os.Build.VERSION.SDK_INT >= 11) {
							try {
								ClipboardManager myClipboard = (ClipboardManager) ShowGrowthHack.this.getSystemService(Context.CLIPBOARD_SERVICE);
								ClipData myClip = ClipData.newPlainText("Share Url", campaignDetails.ShareUrl + (!TextUtils.isEmpty(customLinkSaved) ? "/" + customLinkSaved : ""));
								myClipboard.setPrimaryClip(myClip);

								Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
								AppviralityAPI.copiedToClipBoard(getApplicationContext());
							}
							catch(Exception e) {
							}
						}
					}
				});

				shareLink = campaignDetails.ShareUrl;
				if(customLinkSaved == null)
					customLinkSaved = campaignDetails.UserCustomLink;
				if(!campaignDetails.showCustomLink) {
					referralShareUrl.setVisibility(View.GONE);
					findViewById(R.id.appvirality_referral_link_title).setVisibility(View.GONE);
				}
				else
					txtShareLink.setText(campaignDetails.ShareUrl + (!TextUtils.isEmpty(customLinkSaved) ? "/" + customLinkSaved : ""));

				gridView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View v, int index, long id) {
						if(socialActions.get(index).appname.equals("Invite Contacts")) {
							try {
								Intent inviteContacts = new Intent(getApplicationContext(), InviteContacts.class);
								startActivity(inviteContacts);
							}
							catch(ActivityNotFoundException e) {
								Log.e("AppviralitySDK", "please add InviteContacts activity in your manifest");
							}
							return;
						}
						AppviralityAPI.startActvity(socialActions.get(index).packagename, new ComponentName(socialActions.get(index).packagename, socialActions.get(index).classname), ShowGrowthHack.this);
					}
				});

				txtShowMore.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View v) {
						try {
							setSocialActions(campaignDetails, true, campaignDetails.isCustomTemplete, ShowGrowthHack.this);
							v.setVisibility(View.GONE);
						}
						catch(Exception e) {
						}
					}
				});

				findViewById(R.id.appvirality_settings).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try
						{
							Intent intent = new Intent(getApplicationContext(), Settings.class);
							Bundle bundle = new Bundle();
							bundle.putString("shareurl", campaignDetails.ShareUrl);
							bundle.putString("campaignid", campaignDetails.CampaignId);
							bundle.putString("customlink", customLinkSaved);
							bundle.putBoolean("isrewardexists", campaignDetails.isRewardExists);							
							intent.putExtras(bundle);

							startActivityForResult(intent, 1000);
						}
						catch(ActivityNotFoundException e) {
							Log.e("AppviralitySDK", "please add Settings activity in your manifest");
						}
						catch(Exception e) {
							Log.e("AppviralitySDK", "problem in showing Settings");
							return;
						}
					}
				});

				txtTerms.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(), Terms.class);
						intent.putExtra("campaignid", campaignDetails.CampaignId);
						if(!campaignDetails.isCustomTemplete)
							intent.putExtra("bgcolor", campaignDetails.CampaignBGColor);
						startActivity(intent);
					}
				});

				if(!campaignDetails.isCustomTemplete) {
					txtOfferTitle.setTextColor(Color.parseColor(campaignDetails.OfferTitleColor));
					int offerDescriptionColor = Color.parseColor(campaignDetails.OfferDescriptionColor);
					txtOfferDescription.setTextColor(offerDescriptionColor);
					txtNoSocialInstalled.setTextColor(offerDescriptionColor);
					((TextView) findViewById(R.id.appvirality_referral_link_title)).setTextColor(Color.parseColor(campaignDetails.OfferTitleColor));
					txtShowMore.setTextColor(Color.parseColor(campaignDetails.OfferTitleColor));
					txtShareLink.setTextColor(offerDescriptionColor);
					GradientDrawable shareLinkColor =  new GradientDrawable();
					shareLinkColor.setCornerRadius(6);
					shareLinkColor.setColor(Color.TRANSPARENT);

					if(!TextUtils.isEmpty(campaignDetails.CampaignBGColor) ? Color.parseColor(campaignDetails.CampaignBGColor) == Color.WHITE : false)
						shareLinkColor.setStroke(1, Color.parseColor("#CD5151"));
					else
						shareLinkColor.setStroke(1, Color.WHITE);
					if (android.os.Build.VERSION.SDK_INT >= 16) {
						referralShareUrl.setBackground(shareLinkColor);
						if(campaignDetails.CampaignImage != null)						
							campaignImage.setBackground(new BitmapDrawable(getResources(), campaignDetails.CampaignImage));
						if(campaignDetails.CampaignBGImage != null)
							findViewById(R.id.appvirality_wom_bg).setBackground(new BitmapDrawable(getResources(), campaignDetails.CampaignBGImage));
					}
					else {
						referralShareUrl.setBackgroundDrawable(shareLinkColor);
						if(campaignDetails.CampaignImage != null)						
							campaignImage.setBackgroundDrawable(new BitmapDrawable(campaignDetails.CampaignImage));
						if(campaignDetails.CampaignBGImage != null)
							findViewById(R.id.appvirality_wom_bg).setBackgroundDrawable(new BitmapDrawable(campaignDetails.CampaignBGImage));
					}

					if(campaignDetails.CampaignBGImage == null && !TextUtils.isEmpty(campaignDetails.CampaignBGColor))	{
						findViewById(R.id.appvirality_wom_bg).setBackgroundColor(Color.parseColor(campaignDetails.CampaignBGColor));							
					}
				}

				AppviralityAPI.getUserEarnings(campaignDetails, new AppviralityAPI.UserEarningsListner() {				
					@Override
					public void showUserEarnings(String earningTotal) {
						if(!ShowGrowthHack.this.isFinishing()) {
							if(campaignDetails.isRewardExists ? true : !earningTotal.equals("0")) {
								txtReferrals.setText("Earnings :");
								txtTotalEarnings.setText(earningTotal);	
								campaignDetails.isRewardExists = true;
							}
						}
					}
				});	
			}
			else {
				finish();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			finish();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 1000 && resultCode == RESULT_OK) {
			try {
				customLinkSaved = data.getStringExtra("customlink");	
				if(!TextUtils.isEmpty(shareLink))
					txtShareLink.setText(TextUtils.isEmpty(customLinkSaved) ? shareLink + "/" : shareLink + "/" + customLinkSaved);
			}
			catch(Exception e) {			
			}
		}
	}

	private void setSocialActions(CampaignDetails campaignDetails, boolean showAll, boolean isCustomTemplete, Activity activity)
	{
		socialActions = showAll ? campaignDetails.AllSocialActions : campaignDetails.TopSocialActions;			
		customGridAdapter = new GridViewAdapter(activity, R.layout.appvirality_rows_grid, socialActions, isCustomTemplete);
		gridView.setAdapter(customGridAdapter);
	}

}
