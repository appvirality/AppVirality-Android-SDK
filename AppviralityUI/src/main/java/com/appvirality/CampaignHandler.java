package com.appvirality;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.appvirality.android.AppviralityAPI;
import com.appvirality.android.AppviralityAPI.CampaignReadyListner;
import com.appvirality.android.CampaignDetails;
import com.appvirality.wom.ReferrerDetails;
import com.appvirality.wom.WelcomeScreenActivity;

public class CampaignHandler {

    private static ProgressDialog progressDialog;

    private static CampaignDetails userCampiagnDetails;
    private static ReferrerDetails referrerDetails;

    public static void showLaunchBar(final Activity activity, final CampaignDetails campaignDetails) {
        if (campaignDetails != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (activity != null && !activity.isFinishing()) {
                            if (campaignDetails.EnableLaunchBar) {
                                new AVLaunchBar(activity).showLaunchBanner(campaignDetails);
                            } else {
                                Log.d("AppviralitySDK", "you have disabled launch bar");
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }, AppviralityAPI.Campaign_Launch_Delay);
        } else {
            Log.d("AppviralitySDK", "you don't have any active campaigns at this time.");
        }
    }

    public static void showLaunchPopup(final Activity activity, final CampaignDetails campaignDetails) {
        if (campaignDetails != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (activity != null && !activity.isFinishing()) {
                            if (campaignDetails.EnablePopup) {
                                new AVLaunchBar(activity).showLaunchPopup(campaignDetails);
                            } else {
                                Log.d("AppviralitySDK", "you have disabled launch popup");
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }, AppviralityAPI.Campaign_Launch_Delay);
        } else {
            Log.d("AppviralitySDK", "you don't have any active campaigns at this time.");
        }
    }

    public static void onStop() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void showGrowthHack(final Activity activity, CampaignDetails campaignDetails) {
        if (campaignDetails != null) {
            showGrowthHack(activity, campaignDetails.growthHackType, campaignDetails);
        }
    }

    public static void showGrowthHack(final Activity activity, final AppviralityAPI.GH growthType, CampaignDetails campaignDetails) {
        if (campaignDetails != null) {
            try {
                setCampaignDetails(campaignDetails);
                Intent growthhack = new Intent(activity, com.appvirality.wom.ShowGrowthHack.class);
                growthhack.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(growthhack);
                AppviralityAPI.ClickedCampaign(campaignDetails.CampaignId);
            } catch (ActivityNotFoundException e) {
                Log.e("AppviralitySDK", "please add ShowGrowthHack activity in your manifest");
            } catch (Exception e) {
                Log.e("AppviralitySDK", "problem in showing growthhack");
                return;
            }
        } else {
            if (progressDialog == null ? true : !progressDialog.isShowing()) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(true);
                progressDialog.show();
            }

            AppviralityAPI.setGrowthhackHandler(activity, growthType, new CampaignReadyListner() {

                @Override
                public void onCampaignReady(CampaignDetails campaignDetails) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    if (campaignDetails != null) {
                        CampaignHandler.showGrowthHack(activity, growthType, campaignDetails);
                    } else {
                        showCampaignsNotAvailable(activity);
                    }
                }
            });
        }
    }

    public static void setCampaignDetails(CampaignDetails campaignDetails) {
        userCampiagnDetails = campaignDetails;
    }

    public static CampaignDetails getCampiagnDetails() {
        return userCampiagnDetails;
    }

    public static void setReferrerDetails(ReferrerDetails details) {
        referrerDetails = details;
    }

    public static ReferrerDetails getReferrerDetails() {
        return referrerDetails;
    }

    protected static void showCampaignsNotAvailable(Activity activity) {
        try {
            if (activity != null && !activity.isFinishing()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle("Alert");
                alert.setMessage("Sorry, no active referrals at this time, please try again later.");
                alert.setPositiveButton("OK", null);
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void showWelcomeScreen(final Activity activity) {
        AppviralityAPI.CheckAttribution(activity, null, new AppviralityAPI.CheckAttributionListener() {
            @Override
            public void onResponse(ReferrerDetails referrerDetails) {
                try {
                    setReferrerDetails(referrerDetails);
                    if (activity != null && referrerDetails != null && !referrerDetails.isExistingUser && referrerDetails.hasReferrer && !AppviralityAPI.isWelcomeScreenShown(activity)) {
                        Intent intent = new Intent(activity, WelcomeScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        AppviralityAPI.setWelcomeScreenShown(activity);
                    }
                } catch (ActivityNotFoundException e) {
                    Log.e("AppviralitySDK", "please add WelcomeScreenActivity in your manifest");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        AppviralityAPI.setReferrerDetailsHandler(new AppviralityAPI.ReferrerDetailsListner() {
//            @Override
//            public void onReferrerDetailsReady(ReferrerDetails referrerDetails) {
//                try {
//                    setReferrerDetails(referrerDetails);
//                    if (activity != null && !AppviralityAPI.isExistingUser()) {
//                        Intent intent = new Intent(activity, WelcomeScreenActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        activity.startActivity(intent);
//                        AppviralityAPI.setExistingUser(activity.getApplicationContext());
//                    }
//                } catch (ActivityNotFoundException e) {
//                    Log.e("AppviralitySDK", "please add WelcomeScreenActivity in your manifest");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    protected static void showWelcomeScreen(final Activity activity, final int requestCode) {
        AppviralityAPI.CheckAttribution(activity, null, new AppviralityAPI.CheckAttributionListener() {
            @Override
            public void onResponse(ReferrerDetails referrerDetails) {
                try {
                    setReferrerDetails(referrerDetails);
                    if (activity != null && referrerDetails != null && !referrerDetails.isExistingUser && referrerDetails.hasReferrer && !AppviralityAPI.isWelcomeScreenShown(activity)) {
                        Intent intent = new Intent(activity, WelcomeScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivityForResult(intent, requestCode);
                        AppviralityAPI.setWelcomeScreenShown(activity);
                    }
                } catch (ActivityNotFoundException e) {
                    Log.e("AppviralitySDK", "please add WelcomeScreenActivity in your manifest");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        AppviralityAPI.setReferrerDetailsHandler(new AppviralityAPI.ReferrerDetailsListner() {
//            @Override
//            public void onReferrerDetailsReady(ReferrerDetails referrerDetails) {
//                try {
//                    setReferrerDetails(referrerDetails);
//                    if (activity != null && !AppviralityAPI.isExistingUser()) {
//                        Intent intent = new Intent(activity, WelcomeScreenActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        activity.startActivityForResult(intent, requestCode);
//                        AppviralityAPI.setExistingUser(activity.getApplicationContext());
//                    }
//                } catch (ActivityNotFoundException e) {
//                    Log.e("AppviralitySDK", "please add WelcomeScreenActivity in your manifest");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

}