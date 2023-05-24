package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by arun on 27/11/17.
 */

public class DefaultWindowForToast extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Toast.makeText(DefaultWindowForToast.this, getIntent().getStringExtra("thumbsUpMsg"), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}