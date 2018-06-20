package com.appvirality.appviralitytestapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.appvirality.android.AppviralityAPI;


public class UpdateUserDetails extends Activity {

    ProgressDialog progressDialog;
    EditText editTextName, editTextEmail, editTextStoreID, editTextImage, editTextCountry, editTextState, editTextCity, editTextPhone, editTextPush;
    CheckBox chkExistingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateuserdetails);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailID);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextStoreID = (EditText) findViewById(R.id.editTextStoreID);
        editTextImage = (EditText) findViewById(R.id.editTextProfileImage);
        editTextCountry = (EditText) findViewById(R.id.editTextCountry);
        editTextState = (EditText) findViewById(R.id.editTextState);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPush = (EditText) findViewById(R.id.editTextPushToken);
        chkExistingUser = (CheckBox) findViewById(R.id.chkisExistingUser);

        findViewById(R.id.btnUpdateUserDetails).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(UpdateUserDetails.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(true);
                progressDialog.show();

                AppviralityAPI.UserDetails.setInstance(getApplicationContext())
                        .setUserEmail(editTextEmail.getText().toString())
                        .setUserName(editTextName.getText().toString())
                        .setUseridInStore(editTextStoreID.getText().toString())
                        .setProfileImage(editTextImage.getText().toString())
                        .setCountry(editTextCountry.getText().toString())
                        .setState(editTextState.getText().toString())
                        .setCity(editTextCity.getText().toString())
                        .setPhoneNumber(editTextPhone.getText().toString())
                        .setPushRegID(editTextPush.getText().toString())
                        .isExistingUser(chkExistingUser.isChecked())
                        .Update(new AppviralityAPI.UpdateUserDetailsListner() {
                            @Override
                            public void onSuccess(boolean isSuccess) {
                                try {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                    if (isSuccess) {
                                        Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to update the user info", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {

                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }

                                }
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
