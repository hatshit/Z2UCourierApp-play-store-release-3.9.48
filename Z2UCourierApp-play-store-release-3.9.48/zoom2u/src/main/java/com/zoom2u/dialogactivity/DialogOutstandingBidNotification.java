package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.slidemenu.offerrequesthandlr.Bid_RequestView_Detail;

/**
 * Created by Arun on 29-Dec-2017.
 */

public class DialogOutstandingBidNotification  extends Activity {

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showNotificationDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logoutwindow);
        showNotificationDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showNotificationDialog() {

        if (LoginZoomToU.NOVA_BOLD == null)
            LoginZoomToU.staticFieldInit(DialogOutstandingBidNotification.this);

        try {
            TextView titleTxtOutstandingNotiDialog = (TextView) findViewById(R.id.logoutWindowDialog);
            titleTxtOutstandingNotiDialog.setText("Notification!");

            titleTxtOutstandingNotiDialog.setVisibility(View.VISIBLE);

            TextView msgTxtOutstandingNotiDialog = (TextView) findViewById(R.id.logoutWindowMessageText);

            msgTxtOutstandingNotiDialog.setText(PushReceiver.prefrenceForPushy.getString("NotificationMessage", ""));

            Button hideBtnOutstandingNotiDialog = (Button) findViewById(R.id.logoutWindowCancelBtn);

            hideBtnOutstandingNotiDialog.setTextSize(12.0f);
            hideBtnOutstandingNotiDialog.setText("Hide Notification");
            hideBtnOutstandingNotiDialog.setBackgroundResource(R.drawable.roundedskybluebg);
            hideBtnOutstandingNotiDialog.setTextColor(Color.parseColor("#ffffff"));
            hideBtnOutstandingNotiDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
                    PushReceiver.loginEditorForPushy.putString("Outstanding_RequestId", "");
                    PushReceiver.loginEditorForPushy.commit();
                    finish();
                }
            });

            Button viewDetailBtnOutstandingNotiDialog = (Button) findViewById(R.id.logoutWindowLogoutBtn);

            viewDetailBtnOutstandingNotiDialog.setText("View Details");
            viewDetailBtnOutstandingNotiDialog.setBackgroundResource(R.drawable.rounded_worrier_level);
            viewDetailBtnOutstandingNotiDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!PushReceiver.prefrenceForPushy.getString("Outstanding_RequestId", "").equals("")) {
                        try {
                            Intent intentHistoryDetail = new Intent(DialogOutstandingBidNotification.this, Bid_RequestView_Detail.class);
                            intentHistoryDetail.putExtra("RequestIdForDetail", Integer.parseInt(PushReceiver.prefrenceForPushy.getString("Outstanding_RequestId", "")));
                            intentHistoryDetail.putExtra("IsRequestIdIsCancel", false);
                            intentHistoryDetail.putExtra("UnReadChatCount", 0);
                            startActivityForResult(intentHistoryDetail, 678);
                            intentHistoryDetail = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
                    PushReceiver.loginEditorForPushy.putString("Outstanding_RequestId", "");
                    PushReceiver.loginEditorForPushy.commit();
                    finish();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
            PushReceiver.loginEditorForPushy.putString("Outstanding_RequestId", "");
            PushReceiver.loginEditorForPushy.commit();
        }
    }
}
