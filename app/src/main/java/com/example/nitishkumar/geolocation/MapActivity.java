package com.example.nitishkumar.geolocation;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    private static final int ERROR_DIALOUGE_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (isServicesOK()) {
            init();
        }
    }


    public void init() {
        Button button_map = (Button) findViewById(R.id.mapButton);
        button_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, ButtonMap.class);
                startActivity(intent);
            }
        });

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServiceOK: checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServiceOK: Google play service is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServiceOK: Error Occured But we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, ERROR_DIALOUGE_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(MapActivity.this, "You can't make app request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
