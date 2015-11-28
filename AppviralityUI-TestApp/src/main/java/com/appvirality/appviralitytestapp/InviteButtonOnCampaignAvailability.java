package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.appvirality.android.AppviralityAPI;
import com.appvirality.CampaignHandler;
import com.appvirality.android.CampaignDetails;
import com.appvirality.android.AppviralityAPI.CampaignReadyListner;

public class InviteButtonOnCampaignAvailability extends Activity {
Button btnShowGrowthHack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_button_on_campaign_availability);

        btnShowGrowthHack = (Button)findViewById(R.id.btnShowGH_IfCampaignExists);
        btnShowGrowthHack.setVisibility(View.INVISIBLE);

        //Load campaign Detail and enable buttons
        AppviralityAPI.setCampaignHandler(InviteButtonOnCampaignAvailability.this, AppviralityAPI.GH.Word_of_Mouth, new AppviralityAPI.CampaignReadyListner() {
            @Override
            public void onCampaignReady(CampaignDetails campaignDetails) {
                if (campaignDetails != null) {
                    Log.i("AVLOG", "Campaign Details Ready now");
                    //Set Campaign Details
                    CampaignHandler.setCampaignDetails(campaignDetails);
                    // make the Invite button visible as the campaign details are available
                    btnShowGrowthHack.setVisibility(View.VISIBLE);
                }

            }
        });

        btnShowGrowthHack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Referral Growth Hack
                CampaignHandler.showGrowthHack(InviteButtonOnCampaignAvailability.this, CampaignHandler.getCampiagnDetails());

            }
        });
    }
}
