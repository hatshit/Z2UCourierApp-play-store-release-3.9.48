package com.zoom2u;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LocationPermissionDialog extends Activity {

    private boolean isGPS = false;

    public static int DOES_GPS_REQUEST_ENABLED = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission_dialog);

        Button btnNotThanksPermissionDialog = (Button) findViewById(R.id.btnNotThanksPermissionDialog);
        Button btnTurnOnPermissionDialog = (Button) findViewById(R.id.btnTurnOnPermissionDialog);

        btnNotThanksPermissionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnPermissionData = new Intent();
                returnPermissionData.putExtra("UserAllowToTurnOnLocationPermission", false);
                setResult(Activity.RESULT_OK, returnPermissionData);
                finish();
            }
        });

        btnTurnOnPermissionDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(LocationPermissionDialog.this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        1101);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1101) {
            Intent returnPermissionData = new Intent();
            returnPermissionData.putExtra("UserAllowToTurnOnLocationPermission", true);
            setResult(Activity.RESULT_OK, returnPermissionData);
            finish();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
