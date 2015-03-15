package com.appvirality.wom;

import java.lang.reflect.Field;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.appvirality.R;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.AppviralityAPI.CampaignTermsListner;
import com.appvirality.android.ReferredUser;
import com.appvirality.android.UserRewardDetails;
@SuppressWarnings("deprecation")
public class Settings extends Activity
{
	private String customLinkSaved, ShareUrl;
	private UserRewardDetails rewardDetails;
	private boolean campaignTermsVisible, isRewardExists;
	private String campaignId;
	private TableRow trTotalText;
	private Button btnRetry;
	private EditText edUserParms;
	private WebView campaignTermsWebView;
	private GradientDrawable grdLinkBG, grdRefBGColor, grdTerm;
	private TextView txtUserEarnings, txtPending, txtCustomParms;
	private ProgressBar progress, progressBarTerms;
	private ImageView ddCustomLink, ddReferredUsers, ddTerms;
	private TableLayout tblRewarded, tblUserRewards;
	private TextView txtPendingUserClaimed, txtPendingUserEarnings, txtCheckNetwork;
	private LinearLayout linkLayout, saveLink, referrersDetails, link, termsAndConditions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);     
		setContentView(R.layout.appvirality_wom_user_settings);
		txtUserEarnings = (TextView) findViewById(R.id.appvirality_earnings);
		txtCustomParms = (TextView) findViewById(R.id.appvirality_custom_link);
		txtPending = (TextView) findViewById(R.id.appvirality_text);
		edUserParms = (EditText) findViewById(R.id.appvirality_custom_param);
		linkLayout = (LinearLayout) findViewById(R.id.appvirality_custom_link_layout);
		saveLink = (LinearLayout) findViewById(R.id.appvirality_custom_link_btn_layout);
		referrersDetails = (LinearLayout) findViewById(R.id.appvirality_settings_friends);
		termsAndConditions = (LinearLayout) findViewById(R.id.appvirality_layout_terms);
		txtPendingUserEarnings = (TextView) findViewById(R.id.appvirality_earnings_on_hold);
		txtPendingUserClaimed = (TextView) findViewById(R.id.appvirality_txtclaimed);
		txtCheckNetwork = (TextView) findViewById(R.id.appvirality_no_network);
		campaignTermsWebView = (WebView) findViewById(R.id.appvirality_wom_terms);
		link = (LinearLayout) findViewById(R.id.appvirality_custom_link_top);
		progress = (ProgressBar) findViewById(R.id.appvirality_progressbar);
		progressBarTerms = (ProgressBar) findViewById(R.id.appvirality_progressbar_terms);
		ddCustomLink = (ImageView) findViewById(R.id.appvirality_dropdown1);
		ddReferredUsers = (ImageView) findViewById(R.id.appvirality_dropdown2);
		ddTerms = (ImageView) findViewById(R.id.appvirality_drop_terms);
		tblRewarded = (TableLayout) findViewById(R.id.appvirality_tblrewarded);
		tblUserRewards = (TableLayout) findViewById(R.id.appvirality_user_earnings);
		trTotalText = (TableRow) findViewById(R.id.appvirality_row_text);
		btnRetry = (Button) findViewById(R.id.appvirality_settings_reload);

		try {
			Bundle bundle = getIntent().getExtras();
			ShareUrl = bundle.getString("shareurl");
			isRewardExists = bundle.getBoolean("isrewardexists", false);
			campaignId = bundle.getString("campaignid");
			customLinkSaved = bundle.getString("customlink");
			txtCustomParms.setText(ShareUrl + "/");

			findViewById(R.id.appvirality_savelink).setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					progress.setVisibility(View.VISIBLE);
					AppviralityAPI.setCustomLink(edUserParms.getText().toString().trim(), new AppviralityAPI.CustomLinkListner() {						
						@Override
						public void onCustomLinkSaved(boolean isSaved) {
							try {
								if(!Settings.this.isFinishing()) {
									if(isSaved) {
										Toast.makeText(getApplicationContext(), "Link saved", Toast.LENGTH_SHORT).show();
										customLinkSaved = edUserParms.getText().toString();
									}
									else
										Toast.makeText(getApplicationContext(), "problem in saving custom link, try again later.", Toast.LENGTH_SHORT).show();

									progress.setVisibility(View.INVISIBLE);
									InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(edUserParms.getWindowToken(), 0);	
								}
							}
							catch(Exception e) {
							}
						}
					});
				}
			});

			edUserParms.addTextChangedListener(new TextWatcher() {			
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					txtCustomParms.setText(ShareUrl + "/" + s);
				}
			});

			grdLinkBG = new GradientDrawable();		
			grdLinkBG.setColor(Color.parseColor("#242424"));
			grdLinkBG.setCornerRadius(10);	
			ddCustomLink.setBackgroundResource(R.drawable.appvirality_down);
			link.setOnClickListener(new View.OnClickListener() {						
				@Override
				public void onClick(View v) {
					collapseTabs(1);
					if(linkLayout.getVisibility() == View.GONE) {
						grdLinkBG.setCornerRadii(new float [] { 10, 10, 10, 10, 0, 0, 0, 0});
						ddCustomLink.setBackgroundResource(R.drawable.appvirality_up);
						linkLayout.setVisibility(View.VISIBLE);
						saveLink.setVisibility(View.VISIBLE);
						setBackground(v, grdLinkBG);
					}
					else {		
						collapseTabs(0);
					}
				}
			});

			grdRefBGColor = new GradientDrawable();		
			grdRefBGColor.setColor(Color.parseColor("#242424"));
			grdRefBGColor.setCornerRadius(10);

			setBackground(link, grdLinkBG);
			setBackground(referrersDetails, grdRefBGColor);
					
			ddReferredUsers.setBackgroundResource(R.drawable.appvirality_down);
			referrersDetails.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					collapseTabs(2);
					if(tblRewarded.getVisibility() == View.GONE) {
						grdRefBGColor.setCornerRadii(new float [] { 10, 10, 10, 10, 0, 0, 0, 0});
						tblRewarded.setVisibility(View.VISIBLE);
						ddReferredUsers.setBackgroundResource(R.drawable.appvirality_up);
						if(tblRewarded.getChildCount() == 0)
							if(rewardDetails.ReferredUsers != null && rewardDetails.ReferredUsers.size() > 0)
								setUserRewardedDetails(rewardDetails.ReferredUsers, Settings.this);
						setBackground(v, grdRefBGColor);
					}
					else {		
						collapseTabs(0);
					}
				}
			});

			ddTerms.setBackgroundResource(R.drawable.appvirality_down);
			grdTerm = new GradientDrawable();		
			grdTerm.setColor(Color.parseColor("#242424"));
			grdTerm.setCornerRadius(10);

			if (android.os.Build.VERSION.SDK_INT >= 16) {
				termsAndConditions.setBackground(grdTerm);
			}
			else {
				termsAndConditions.setBackgroundDrawable(grdTerm);
			}			
			campaignTermsWebView.setVisibility(View.GONE);
			termsAndConditions.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					collapseTabs(3);
					if(!campaignTermsVisible) {
						campaignTermsVisible = true;
						grdTerm.setCornerRadii(new float [] { 10, 10, 10, 10, 0, 0, 0, 0});
						ddTerms.setBackgroundResource(R.drawable.appvirality_up);
						getCampaignTerms(campaignId);
						setBackground(v, grdTerm);
					}
					else {		
						collapseTabs(0);
					}
				}
			});

			btnRetry.setOnClickListener(new View.OnClickListener() {	
				@Override
				public void onClick(View v) {	
					if(AppviralityAPI.isNetworkAvailable(getApplicationContext()))
					{
						getCampaignTerms(campaignId);
						txtCheckNetwork.setVisibility(View.GONE);
						v.setVisibility(View.GONE);
					}
				}
			});

			if(customLinkSaved != null)
				edUserParms.setText(customLinkSaved);

			if(!isRewardExists) {
				tblUserRewards.setVisibility(View.GONE);
				grdLinkBG.setCornerRadii(new float [] { 10, 10, 10, 10, 0, 0, 0, 0});
				ddCustomLink.setBackgroundResource(R.drawable.appvirality_up);
				linkLayout.setVisibility(View.VISIBLE);
				saveLink.setVisibility(View.VISIBLE);
			}
			AppviralityAPI.GetUserRewardDetails(new AppviralityAPI.RewardedDetailsListner() {				
				@Override
				public void setUserReawardDetails(UserRewardDetails userRewardDetails) {
					try {
						rewardDetails = userRewardDetails;
						if(isRewardExists)
						{
							txtUserEarnings.setText(userRewardDetails.TotalRewarded + " " + userRewardDetails.RewardType);
							txtPendingUserEarnings.setText(userRewardDetails.TotalPending + " " + userRewardDetails.RewardType);
							txtPendingUserClaimed.setText(userRewardDetails.TotalClaimed + " " + userRewardDetails.RewardType);
							findViewById(R.id.appvirality_progress).setVisibility(View.GONE);
							findViewById(R.id.appvirality_total_earinings).setVisibility(View.VISIBLE);
							findViewById(R.id.appvirality_claimed).setVisibility(View.VISIBLE);
							findViewById(R.id.appvirality_hold).setVisibility(View.VISIBLE);
							tblUserRewards.setVisibility(View.VISIBLE);
							if(rewardDetails.PendingToRedeem != null) {
								txtPending.setText("You need " + userRewardDetails.PendingToRedeem + " "
										+ userRewardDetails.RewardType + " more credits to redeem your earnings");		
								trTotalText.setVisibility(View.VISIBLE);
							}
						}
						if(rewardDetails.ReferredUsers != null && rewardDetails.ReferredUsers.size() > 0)
							referrersDetails.setVisibility(View.VISIBLE);
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}, campaignId);

			referrersDetails.setVisibility(View.GONE);
			tblRewarded.setVisibility(View.GONE);
			findViewById(R.id.appvirality_progress).setVisibility(View.VISIBLE);

			try {
				if(android.os.Build.VERSION.SDK_INT >= 11) {
					Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
					f.setAccessible(true);
					f.set(edUserParms, R.drawable.appvirality_cursor_color);
				}
			} catch (Exception e) {
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	private void setUserRewardedDetails(List<ReferredUser> referredUsersList, Activity activity)
	{
		try {
			Bitmap userImage = BitmapFactory.decodeResource(activity.getResources(),R.drawable.appvirality_user_image);
			int imageWidth = userImage.getWidth();
			int imageHeight = userImage.getHeight();
			userImage = Bitmap.createScaledBitmap(userImage, (int) (imageWidth * 0.6), (int) (imageHeight * 0.6), true);
			for(int i = 0; i < referredUsersList.size(); i++) {
				ReferredUser referredUserData = referredUsersList.get(i);
				TableRow tblRow = new TableRow(activity);
				LinearLayout profile = new LinearLayout(activity);
				profile.setMinimumWidth(imageWidth);
				profile.setMinimumHeight(imageHeight);
				profile.setGravity(Gravity.CENTER);
				RoundedImageView imgUserProfile = new RoundedImageView(activity);
				imgUserProfile.setImageBitmap(userImage);
				profile.addView(imgUserProfile);
				TextView txtUserName = new TextView(activity);
				String user = TextUtils.isEmpty(referredUserData.UserName) ? referredUserData.UserEmailID : referredUserData.UserName;
				txtUserName.setText(TextUtils.isEmpty(user) ? "Unknown friend" : user);
				txtUserName.setTextColor(Color.BLACK);

				TableRow.LayoutParams rowParams = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				rowParams.gravity = Gravity.CENTER_VERTICAL;
				TextView txtRegisterDate = new TextView(activity);
				txtRegisterDate.setTextColor(Color.BLACK);
				txtRegisterDate.setText(referredUserData.RegDate);
				txtRegisterDate.setGravity(Gravity.CENTER_VERTICAL);
				txtRegisterDate.setPadding(0, 0, 10, 0);
				tblRow.addView(profile);
				tblRow.addView(txtUserName, rowParams);
				tblRow.addView(txtRegisterDate, rowParams);
				tblRewarded.addView(tblRow);
				View lineSeparator = new View(activity);
				lineSeparator.setBackgroundColor(Color.parseColor("#bababa"));
				lineSeparator.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 1));
				tblRewarded.addView(lineSeparator);
			}			
		}
		catch(Exception e)
		{			
		}
	}

	private void getCampaignTerms(String campaignId)
	{
		progressBarTerms.setVisibility(View.VISIBLE);
		AppviralityAPI.getCampaignTerms(campaignId, new CampaignTermsListner() {
			@Override
			public void onSuccess(boolean isSuccess, String campaignTerms) {
				try {					
					if(progressBarTerms.getVisibility() == View.VISIBLE)
					{
						if(isSuccess) {
							campaignTerms = (campaignTerms == null) ? "No terms specified" : campaignTerms;
							campaignTermsWebView.loadDataWithBaseURL(null, "<html><body>" + campaignTerms.replaceAll("\\n", "<br/>") + "</body></html>", "text/html", "UTF-8", null);
							campaignTermsWebView.setVisibility(View.VISIBLE);
						}
						else {
							btnRetry.setVisibility(View.VISIBLE);
							txtCheckNetwork.setVisibility(View.VISIBLE);
						}

						progressBarTerms.setVisibility(View.GONE);
					}
				}
				catch(Exception e) {										
				}
			}
		});
	}

	private void setBackground(View v, GradientDrawable grdrawable)
	{
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			v.setBackground(grdrawable); 
		} else {
			v.setBackgroundDrawable(grdrawable); 
		}
	}

	private void collapseTabs(int tabId)
	{
		if(tabId != 1)
		{
			grdLinkBG.setCornerRadius(10);
			ddCustomLink.setBackgroundResource(R.drawable.appvirality_down);
			linkLayout.setVisibility(View.GONE);
			saveLink.setVisibility(View.GONE);
			setBackground(link, grdLinkBG);	
		}

		if(tabId != 2)
		{
			grdRefBGColor.setCornerRadius(10);
			tblRewarded.setVisibility(View.GONE);
			ddReferredUsers.setBackgroundResource(R.drawable.appvirality_down);
			setBackground(referrersDetails, grdRefBGColor);
		}

		if(tabId != 3)
		{
			grdTerm.setCornerRadius(10);
			progressBarTerms.setVisibility(View.GONE);
			campaignTermsWebView.setVisibility(View.GONE);
			campaignTermsVisible = false;
			ddTerms.setBackgroundResource(R.drawable.appvirality_down);
			btnRetry.setVisibility(View.GONE);
			txtCheckNetwork.setVisibility(View.GONE);
			setBackground(termsAndConditions, grdTerm);
		}
	}

	@Override
	public void onBackPressed()
	{
		setResult(Activity.RESULT_OK, new Intent().putExtra("customlink", customLinkSaved));
		super.onBackPressed();
	}	
}
