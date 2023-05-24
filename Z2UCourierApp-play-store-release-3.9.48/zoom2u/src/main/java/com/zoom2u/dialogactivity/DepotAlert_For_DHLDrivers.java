package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;

/**
 * Created by arun on 6/6/18.
 */

public class DepotAlert_For_DHLDrivers extends Activity {

    Intent intent;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        this.intent = intent;
//        showNotificationDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_near_dhl_office);

        this.intent = getIntent();
        showNotificationDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showNotificationDialog() {

        if(LoginZoomToU.NOVA_BOLD == null)
            LoginZoomToU.staticFieldInit(DepotAlert_For_DHLDrivers.this);

        try {
            TextView titleTxt_EnterDHLDialog = (TextView) findViewById(R.id.titleTxt_EnterDHLDialog);


            TextView routeCountTxt_EnterDHLDialog = (TextView) findViewById(R.id.routeCountTxt_EnterDHLDialog);

            try {
                if (intent.getStringExtra("RouteCode") != null && !intent.getStringExtra("RouteCode").equals(""))
                    routeCountTxt_EnterDHLDialog.setText(intent.getStringExtra("RouteCode"));
                else
                    routeCountTxt_EnterDHLDialog.setText("NA");
            } catch (Exception e) {
                e.printStackTrace();
                routeCountTxt_EnterDHLDialog.setText("NA");
            }

            TextView notificationMsgTxt = (TextView) findViewById(R.id.notificationMsgTxt);


            try {
                if (intent.getStringExtra("NotificationMessageStr") != null)
                    notificationMsgTxt.setText(intent.getStringExtra("NotificationMessageStr"));
                else
                    finish();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }

            TextView staticMsgTxt = (TextView) findViewById(R.id.staticMsgTxt);


            Button okBtnEnterDHLDepot = (Button) findViewById(R.id.okBtnEnterDHLDepot);

            okBtnEnterDHLDepot.setTransformationMethod(null);
            okBtnEnterDHLDepot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //	notificationDialogCount = 0;
                    PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
                    PushReceiver.loginEditorForPushy.commit();
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
            PushReceiver.loginEditorForPushy.commit();
        }
    }
}
