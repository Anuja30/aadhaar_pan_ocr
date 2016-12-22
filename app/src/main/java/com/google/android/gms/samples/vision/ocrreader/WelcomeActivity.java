package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.samples.vision.ocrreader.aadhaar.HomeActivity;

public class WelcomeActivity extends Activity {

    ImageButton panCardImageButton,aadhaarCardImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        panCardImageButton =(ImageButton)findViewById(R.id.panCardImageButton);
        aadhaarCardImageButton =(ImageButton)findViewById(R.id.aadhaarCardImageButton);

        panCardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        aadhaarCardImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(i);
            }
        });
    }
}
