package com.appvirality.wom;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appvirality.R;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.AppviralityAPI.CampaignTermsListner;

public class Terms extends Activity
{
	private WebView campaignTermsWebView;
	private ProgressBar progressTerms;
	private Button btnRetry;
	private TextView txtCheckNetwork;
	private String campaignId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);      
		setContentView(R.layout.appvirality_terms);

		try {
			campaignTermsWebView = (WebView) findViewById(R.id.appvirality_wom_terms);
			progressTerms = (ProgressBar) findViewById(R.id.appvirality_progress);
			btnRetry = (Button) findViewById(R.id.appvirality_settings_reload);
			txtCheckNetwork = (TextView) findViewById(R.id.appvirality_no_network);
			final ImageView btnBack = (ImageView) findViewById(R.id.appvirality_prev);

			campaignId = getIntent().getStringExtra("campaignid");
			String termsBGColor = getIntent().getStringExtra("bgcolor");
			if(!TextUtils.isEmpty(termsBGColor))
				findViewById(R.id.appvirality_terms_title).setBackgroundColor(Color.parseColor(termsBGColor));
												
			btnRetry.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					getCampaignTerms();				
				}
			});
			
			btnBack.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					finish();					
				}
			});
			
			getCampaignTerms();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			finish();
		}
	}

	private void getCampaignTerms()
	{
		progressTerms.setVisibility(View.VISIBLE);
		btnRetry.setVisibility(View.GONE);
		txtCheckNetwork.setVisibility(View.GONE);
		AppviralityAPI.getCampaignTerms(campaignId, new CampaignTermsListner() {
			@Override
			public void onSuccess(boolean isSuccess, String campaignTerms) {
				try {	
					if(isSuccess) {
						campaignTerms = (campaignTerms == null) ? "No terms specified" : campaignTerms;
						campaignTermsWebView.loadDataWithBaseURL(null, "<html><body>" + campaignTerms.replaceAll("\\n", "<br/>") + "</body></html>", "text/html", "UTF-8", null);
						campaignTermsWebView.setVisibility(View.VISIBLE);
					}
					else {
						btnRetry.setVisibility(View.VISIBLE);
						txtCheckNetwork.setVisibility(View.VISIBLE);
					}

					progressTerms.setVisibility(View.GONE);
				}
				catch(Exception e) {										
				}
			}
		});

	}	
}
