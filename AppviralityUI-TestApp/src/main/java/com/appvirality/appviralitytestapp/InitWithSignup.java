package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.appvirality.CampaignHandler;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.CampaignDetails;

import org.json.JSONException;
import org.json.JSONObject;


public class InitWithSignup extends Activity {

    ProgressDialog progressDialog;
    EditText editTextrefcode, editTextEmailID, editTextCountry, editTextStoreid;
    CheckBox chkExistingUser;
    private final String AV_Key = "004d37de619e41888eb7a4f800715468";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initwithsignup);
        editTextrefcode = (EditText) findViewById(R.id.editTextReferralCode);
        editTextEmailID = (EditText) findViewById(R.id.editTextEmailID);
        chkExistingUser = (CheckBox) findViewById(R.id.chkisExistingUser);
        editTextCountry = (EditText) findViewById(R.id.editTextCountry);
        editTextStoreid = (EditText) findViewById(R.id.editTextStoreID);

        String friendrefCode = AppviralityAPI.referralCode_From_InstallReferrer;
        if (!TextUtils.isEmpty(friendrefCode)) {
            editTextrefcode.setText(friendrefCode.toUpperCase());
        }

        chkExistingUser.setChecked(AppviralityAPI.isExistingUser());

        findViewById(R.id.Register).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(InitWithSignup.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                String refcode = editTextrefcode.getText().toString();

                JSONObject userDetails = new JSONObject();
                try {
                    userDetails.put("emailid", editTextEmailID.getText().toString());
                    userDetails.put("referrercode", editTextrefcode.getText().toString());
                    userDetails.put("isexistinguser", chkExistingUser.isChecked());
                    userDetails.put("country", editTextCountry.getText().toString());
                    userDetails.put("storeid", editTextStoreid.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AppviralityAPI.initWithAppKey(getApplicationContext(), AV_Key, userDetails, new AppviralityAPI.InitListner() {
                    @Override
                    public void onInit(boolean isSuccess, JSONObject userDetails) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        if (isSuccess) {
                            AppviralityAPI.initListner = null;
                            AppviralityAPI.setCampaignHandler(InitWithSignup.this, AppviralityAPI.GH.Word_of_Mouth,
                                    new AppviralityAPI.CampaignReadyListner() {
                                        @Override
                                        public void onCampaignReady(CampaignDetails campaignDetails) {
                                            if (campaignDetails != null) {
                                                String myReferralCode = campaignDetails.Shortcode;
                                                CampaignHandler.setCampaignDetails(campaignDetails);
                                            }
                                        }
                                    });
                        }
                        Toast.makeText(InitWithSignup.this, "Init Status: " + isSuccess,
                                Toast.LENGTH_LONG).show();
                        Log.i("AppVirality: ", "InitwithAppKey Status " + isSuccess);
                        if (userDetails != null)
                            Log.i("AppVirality: ", "userDetails " + userDetails.toString());
                    }
                });
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
