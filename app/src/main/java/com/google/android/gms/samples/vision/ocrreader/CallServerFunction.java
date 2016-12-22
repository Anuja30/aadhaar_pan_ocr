package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by Medha on 11/24/2016.
 */

public class CallServerFunction {

    private Context context;

    public CallServerFunction(Context context) {
        this.context = context;
    }

    public boolean isOnline() {
        try{
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            if(netInfo != null) {
                for (NetworkInfo ni : netInfo) {
                    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                        if (ni.isConnected())
                            haveConnectedWifi = true;
                    if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                        if (ni.isConnected())
                            haveConnectedMobile = true;
                }
            }
            return haveConnectedWifi || haveConnectedMobile;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public String saveCustomerKYC(String customer_id, String pan_card,
                                  String addressLine, String pincode, String city, String state,
                                  String country, String customer_status,
                                  String chequePath, String signPath, String aadharNo) throws Exception {

        String rtrnVal = "ERROR";

        String url = "http://52.74.157.20/cashrich/saveCustomerKYC.json";
        Log.d("CR","URL ::::" + url);
        Log.d("CR", "cust id:" + customer_id);
        Log.d("CR","pancard:" + pan_card);
        Log.d("CR", "country:" + country);
        Log.d("CR","cust status:" + customer_status);

        try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if(chequePath != null && chequePath.trim().length() > 0 && !chequePath.equals("")) {

                File fileCheque = new File(chequePath);
                entityBuilder.addBinaryBody("cheque_pic", fileCheque);
            }

            if(signPath != null && signPath.trim().length() > 0 && !signPath.equals("")) {

                File fileSign = new File(signPath);
                Log.d("CR", "sign path in callServer: " + signPath);
                entityBuilder.addBinaryBody("sign_pic", fileSign);
            }

            if(aadharNo != null && aadharNo.trim().length() > 0 && !aadharNo.equals("")) {

                entityBuilder.addTextBody("aadhar_no", aadharNo);
            }

            entityBuilder.addTextBody("customer_id", customer_id);
            entityBuilder.addTextBody("pan_card", pan_card);
            entityBuilder.addTextBody("comm_address", addressLine);
            entityBuilder.addTextBody("comm_city", city);
            entityBuilder.addTextBody("comm_state", state);
            entityBuilder.addTextBody("comm_country", country);
            entityBuilder.addTextBody("comm_pincode", pincode);
            entityBuilder.addTextBody("res_address", addressLine);
            entityBuilder.addTextBody("res_city", city);
            entityBuilder.addTextBody("res_state", state);
            entityBuilder.addTextBody("res_country", country);
            entityBuilder.addTextBody("res_pincode", pincode);
//            entityBuilder.addTextBody("aadhar_no", aadharNo);
            entityBuilder.addTextBody("customer_status", customer_status);


            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
//            HttpEntity entity = entityBuilder.build();
            postRequest.setEntity(entityBuilder.build());

            HttpResponse response = httpClient.execute(postRequest);
            HttpEntity httpEntity = response.getEntity();

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpEntity.getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);

            // try parse the string to a JSON object
            JSONObject jObj = null;

            jObj = new JSONObject(s.toString());

            boolean isError = false;
            // Getting Object of REQUEST
            String req = jObj.getString("REQUEST");
            Log.d("Cash Rich",req + "---------req");

            // Getting Objects of Status
            JSONObject testStatus = jObj.getJSONObject("status");
            Log.d("Cash Rich",testStatus + "------testStatus");

            // Getting attribute StatusCode
            String statusCode = testStatus.getString("code");
            Log.d("Cash Rich",statusCode + "--------------statusCode");

            if(isError)
                return rtrnVal;

            String responseResult = jObj.getString("response");
            if(statusCode.equals("-1")){
                rtrnVal = "ERROR";
            }else if (statusCode.equals("200") && !responseResult.equals("null")){

//                // Getting Object of Response
                JSONObject testResponse = jObj.getJSONObject("response");
                Log.d("CashRich",testResponse + "-------------testResponse");

                String status = testResponse.getString("status");
                rtrnVal = status;

            }else{
                rtrnVal = "NOT FOUND";
            }
        } /*catch (KeyManagementException | UnrecoverableKeyException| NoSuchAlgorithmException | KeyStoreException
            | IOException | URISyntaxException | NullPointerException | JSONException e1) {
         e1.printStackTrace();
      } */ catch (Exception e) {
            e.printStackTrace();
        }

        return rtrnVal;

    }

    public static void appAlertDialog(final Activity activity, String title, String message)
    {
        final AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
        alertbox.setTitle(title);
        alertbox.setMessage(message);
        alertbox.setPositiveButton("Ok", new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
//        alertbox.setNegativeButton("No", new
//                DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface arg0, int arg1) {
//
//                    }
//                });
        alertbox.show();
    }
}
