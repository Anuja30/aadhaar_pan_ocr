/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * recognizes text.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    ProgressDialog progressDialog = null;
    private StoreKYCTask storeKYCTask = null;
    String panCardNum;
    // Use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView statusMessage;
    private TextView textValue;
    private EditText panCard;

    private static final int RC_OCR_CAPTURE = 9003;
    private static final String TAG = "MainActivity";
    Button KYC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.status_message);
        textValue = (TextView) findViewById(R.id.text_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        panCard = (EditText) findViewById(R.id.panCardEditText);

        findViewById(R.id.read_text).setOnClickListener(this);
        KYC = (Button) findViewById(R.id.check_KYC);

        KYC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPANKYC();
            }
        });
    }

    private void checkPANKYC() {

        panCardNum = panCard.getText().toString();
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

        Matcher matcher = pattern.matcher(panCardNum);

        Log.d(TAG, "Text read: " + panCardNum);
        if (matcher.matches()) {

            storeKYCTask = new StoreKYCTask();
            storeKYCTask.execute((Void) null);

        } else {

            panCard.setText("", TextView.BufferType.EDITABLE);
            Toast.makeText(getApplicationContext(), "Detected text validation failed", Toast.LENGTH_LONG).show();

            //toast
        }
    }

    public class StoreKYCTask extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;
        String result = "";
//        String commAddress = "";

        // declare other objects as per your need
        @Override
        protected void onPreExecute() {
            // do initialization of required objects objects here
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "", "Checking your KYC Status", true);
        }

        ;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                CallServerFunction callServerFunction = new CallServerFunction(getApplicationContext());

                if(!callServerFunction.isOnline()) {
                    return false;
                }

                result = callServerFunction.saveCustomerKYC("557", panCardNum, "madhav baug", "400829", "mumbai", "15018", "16101", "501", "", "", "");
//                result = callServerFunction.saveCustomerKYC("557", panCardNum, "Regus", "400607", "Mumbai", "15018", "16101", "500", "", "", "");
                Log.d("PAN", "Result: " + result);

                if (result.contains("ERROR")) {
                    return false;
                } else {

                    return true;
                }


            } catch (Exception e) {
                Log.e("PAN", e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {

            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            storeKYCTask = null;
//            Toast.makeText(getApplicationContext(), "Result:" + result, Toast.LENGTH_LONG).show();
            if (success) {
                try {

                    Log.d("PAN", " PAN result: " + result);
                    if (result.equals("502")) {
//KYC yes
                        if(!(MainActivity.this).isFinishing()) {
                            CallServerFunction.appAlertDialog(MainActivity.this, "", "You are KYC Compliant");
                        }

//                        new AlertDialog.Builder(getApplicationContext())
//                                .setTitle("You are a KYC Compliant")
//                                .setMessage("Do you want to try again?")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                })
//                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.cancel();
//                                    }
//                                })
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();

                    } else if (result.equals("504")) {
                        if(!(MainActivity.this).isFinishing()) {
                            CallServerFunction.appAlertDialog(MainActivity.this, "", "You are not KYC Compliant");
                        }

                    } else {
                        if(!(MainActivity.this).isFinishing()) {
                            CallServerFunction.appAlertDialog(MainActivity.this, "", "System error occurred. Please try again after sometime");
                        }


                    }

                } catch (Exception ex) {
                    this.exception = ex;
                    ex.printStackTrace();
                }

            } else {

                if(!(MainActivity.this).isFinishing()) {
                    CallServerFunction.appAlertDialog(MainActivity.this, "", "System error occurred. Please try again after sometime");
                }


            }
        }

        @Override
        protected void onCancelled() {
            storeKYCTask = null;

        }
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_text) {
            // launch Ocr capture activity.
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(OcrCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(OcrCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_OCR_CAPTURE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject);

                    panCard.setText(text, TextView.BufferType.EDITABLE);
                    panCardNum = panCard.getText().toString();

                } else {
                    statusMessage.setText(R.string.ocr_failure);
                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.ocr_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
