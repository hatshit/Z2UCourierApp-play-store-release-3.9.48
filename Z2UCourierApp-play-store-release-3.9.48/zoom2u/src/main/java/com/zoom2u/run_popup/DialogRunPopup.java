package com.zoom2u.run_popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoom2u.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahendra Dabi on 19-11-2021.
 */
public class DialogRunPopup extends Activity {

    List<RunPopupModel> msgs = new ArrayList<>();
    ImageView img_logo;
    TextView tv_head;
    TextView tv_msg;
    Button button;
    int msgIndex = 0;
    RunPopupModel runPopupModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_run_popup_info);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        img_logo = findViewById(R.id.img_logo);
        tv_head = findViewById(R.id.tv_head);
        tv_msg = findViewById(R.id.tv_msg);
        button = findViewById(R.id.button);

        initMessages();

        button.setOnClickListener(v -> {
            msgIndex++;
            if (msgIndex==3) {
                Intent sendintent = new Intent("selfieUpload");
                sendintent.putExtra("showDialogMsg", true);
                LocalBroadcastManager.getInstance(this).sendBroadcast(sendintent);
                finish();
                return;
            }
            updateMsg(msgIndex);
        });


    }

    private void updateMsg(int msgIndex) {
         runPopupModel = msgs.get(msgIndex);
        img_logo.setImageResource(runPopupModel.getImageId());
        tv_head.setText(runPopupModel.getHeading());
        switch (msgIndex)
        {
            case 0: tv_msg.setText(getText(R.string.text1));
            break;
            case 1:tv_msg.setText(getText(R.string.text2));
            break;
            case 2:tv_msg.setText(getText(R.string.text3));
            break;
        }


        button.setText(runPopupModel.getButtonText());

    }

    private void initMessages() {
        msgs.clear();
        msgs.add(new RunPopupModel(R.drawable.important_1, "Important", getString(R.string.text1), "Next", false));
        msgs.add(new RunPopupModel(R.drawable.important_2, "Important", getString(R.string.text2), "Next", false));
        msgs.add(new RunPopupModel(R.drawable.important_3, "Important", getString(R.string.text3), "I understand", true));
    }


    class RunPopupModel {
        RunPopupModel(int imageId, String heading, String msg, String buttonText, boolean isFinish) {
            this.imageId = imageId;
            this.heading = heading;
            this.msg = msg;
            this.buttonText = buttonText;
            this.isFinish = isFinish;

        }

        int imageId;
        String heading;
        String msg;
        String buttonText;
        boolean isFinish;

        public int getImageId() {
            return imageId;
        }

        public String getHeading() {
            return heading;
        }

        public String getMsg() {
            return msg;
        }

        public String getButtonText() {
            return buttonText;
        }

        public boolean isFinish() {
            return isFinish;
        }
    }
}
