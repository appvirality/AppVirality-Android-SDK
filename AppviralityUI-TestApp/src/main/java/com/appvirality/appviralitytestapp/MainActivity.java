package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appvirality.AppviralityUI;
import com.appvirality.CampaignHandler;
import com.appvirality.android.AppviralityAPI;
import com.appvirality.wom.ReferrerDetails;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends Activity {

    private final String LAUNCHCODE = "appvirality.sampleapp.launchmode";
    private final int REQUEST_CODE = 5000;
    private static final int WRITE_EXT_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        checkPermission(Manifest.permission.READ_PHONE_STATE, WRITE_EXT_REQ_CODE);
//        checkPermission(WRITE_EXTERNAL_STORAGE, WRITE_EXT_REQ_CODE);

        //Show personalized welcome screen for new users
        if (!AppviralityAPI.isWelcomeScreenShown(getApplicationContext()))
            AppviralityUI.showWelcomeScreen(MainActivity.this, REQUEST_CODE);

        // Get GCM Registration key to enable push notifications.
//        GCMRegistration.registerGCM(getApplicationContext());

        //Option:1 - Launch from custom button i.e from "Invite Friends" or "Refer & Earn" button on your App menu
        findViewById(R.id.growthhack).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AppviralityUI.showGrowthHack(MainActivity.this, AppviralityUI.GH.Word_of_Mouth);
                //Use LogOut if your App has Login & Logout functionality
                //AppviralityAPI.LogOut(getApplicationContext());
            }
        });

        //Option:1_2 - show custom button if campaign Exists i.e from "Invite Friends" or "Refer & Earn" button on your App menu if campaign exists
        findViewById(R.id.btnGH_IfCampaignExists).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InviteButtonOnCampaignAvailability.class);
                intent.putExtra(LAUNCHCODE, true);
                startActivity(intent);
            }
        });


        //Option - 2 : Launch Bar (i.e Growth Hack will be launched from a Mini notification)
        findViewById(R.id.launchbar).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
                intent.putExtra(LAUNCHCODE, true);
                startActivity(intent);
            }
        });

        //Option - 3 : Launch Popup (i.e Growth Hack will be launched from a Popup notification)
        findViewById(R.id.launchpopup).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LaunchActivity.class);
                intent.putExtra(LAUNCHCODE, false);
                startActivity(intent);
            }
        });

        //Registration page
        findViewById(R.id.btnRegister).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);
            }
        });

        //Registration page
        findViewById(R.id.btnInitSDK).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InitWithSignup.class);
                startActivity(intent);
            }
        });

        //Registration page
        findViewById(R.id.btnLogout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CampaignHandler.setCampaignDetails(null);
                CampaignHandler.setReferrerDetails(null);
                AppviralityAPI.LogOut(getApplicationContext());
                Toast.makeText(MainActivity.this, "Finished logout",
                        Toast.LENGTH_LONG).show();
            }
        });

        //Update user Info
        findViewById(R.id.btnUpdateUserDetails).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UpdateUserDetails.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_make_transaction).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog popUp = new Dialog(MainActivity.this);
                popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popUp.setCancelable(true);
                popUp.setContentView(R.layout.dialog_transaction);
                final EditText editText = (EditText) popUp.findViewById(R.id.edit_transaction_amount);
                popUp.findViewById(R.id.btn_make_transaction).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String transactionAmount = editText.getText().toString().trim();
                        if (transactionAmount.equals("")) {
                            editText.setError("Required");
                        } else {
                            AppviralityAPI.saveConversionEvent("Transaction", transactionAmount, null);
                            popUp.dismiss();
                        }
                    }
                });
                popUp.show();
            }
        });

        findViewById(R.id.btn_check_attribution).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog popUp = new Dialog(MainActivity.this);
                popUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popUp.setCancelable(true);
                popUp.setContentView(R.layout.dialog_transaction);
                TextView tvTitle = (TextView) popUp.findViewById(R.id.title);
                tvTitle.setText("Check Attribution");
                final EditText editText = (EditText) popUp.findViewById(R.id.edit_transaction_amount);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint("Referral Code(Optional)");
                Button btn = (Button) popUp.findViewById(R.id.btn_make_transaction);
                btn.setText("Check Attribution");
                popUp.findViewById(R.id.btn_make_transaction).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String refCode = editText.getText().toString().trim();
                        AppviralityAPI.CheckAttribution(getApplicationContext(), refCode, new AppviralityAPI.CheckAttributionListener() {
                            @Override
                            public void onResponse(ReferrerDetails referrerDetails) {
                                if (referrerDetails != null) {
                                    Log.i("AppViralityTestApp", "" + "IsExistingUser : " + referrerDetails.isExistingUser
                                            + "\nHasReferrer : " + referrerDetails.hasReferrer
                                            + "\nReferrerName : " + referrerDetails.ReferrerName
                                            + "\nReferrerCode : " + referrerDetails.ReferrerCode
                                            + "\nIsReferrerConfirmed : " + referrerDetails.isReferrerConfirmed);
                                }
                            }
                        });
                        popUp.dismiss();
                    }
                });
                popUp.show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == REQUEST_CODE) && (resultCode == -1)) {
            Intent intent = new Intent(MainActivity.this, Registration.class);
            startActivity(intent);

        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        //Little Clean up : use this when you are invoking Growth Hack using
        // "AppviralityUI.showGrowthHack(MainActivity.this, AppviralityUI.GH.Word_of_Mouth);" call
        AppviralityUI.onStop();
    }



}
