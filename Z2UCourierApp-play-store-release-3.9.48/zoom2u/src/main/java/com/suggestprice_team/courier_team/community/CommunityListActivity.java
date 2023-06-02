package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.MainActivity;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;

import java.util.ArrayList;

public class CommunityListActivity extends Activity implements View.OnClickListener{

    TextView txtInvitation;
    ImageView backFromTeamList;
    ImageView chatIconTeamList;
    RelativeLayout headerDLayout;
    TextView txtMemberTxt;
    FrameLayout frame;
    LinearLayout linearmember,linearinvation;
    TextView chatCountInviteTeamMamber;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communityteam);

        setUp();

        Window window = CommunityListActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if(MainActivity.isIsBackGroundGray()){
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerDLayout.setBackgroundResource(R.color.base_color_gray);
        }else{
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerDLayout.setBackgroundResource(R.color.base_color1);
        }

      //by default open fragment
      openFragment();

    }



    private void setUp() {
        backFromTeamList=findViewById(R.id.backFromTeamList);
        chatIconTeamList=findViewById(R.id.chatIconTeamList);
        headerDLayout=findViewById(R.id.headerDLayout);
        txtMemberTxt=findViewById(R.id.txtMemberTxt);
        txtInvitation=findViewById(R.id.txtInvitation);
        frame=findViewById(R.id.frame);
        linearinvation=findViewById(R.id.linearinvation);
        linearmember=findViewById(R.id.linearmember);

        chatCountInviteTeamMamber = (TextView)  findViewById(R.id.chatCountInviteTeamMamber);
        chatCountInviteTeamMamber.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = chatCountInviteTeamMamber;

        backFromTeamList.setOnClickListener(this);
        txtMemberTxt.setOnClickListener(this);
        txtInvitation.setOnClickListener(this);
        chatIconTeamList.setOnClickListener(this);
    }

    private void openFragment() {
        txtMemberTxt.setBackgroundResource(R.drawable.selected_background);
        MembersFragment membersFragment = new MembersFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,membersFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(chatCountInviteTeamMamber);
        SlideMenuZoom2u.countChatBookingView = chatCountInviteTeamMamber;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.backFromTeamList:
                finish();
                break;
            case R.id.chatIconTeamList:
                Intent intent = new Intent(CommunityListActivity.this, ChatViewBookingScreen.class);
                startActivity(intent);
                break;
            case R.id.txtInvitation:
                InvitationsFragment invitationsFragment = new InvitationsFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame,invitationsFragment);
                fragmentTransaction.commit();
                txtMemberTxt.setBackgroundResource(R.color.base_color1);
                txtInvitation.setBackgroundResource(R.drawable.selected_background);
                break;
            case R.id.txtMemberTxt:
                MembersFragment membersFragment = new MembersFragment();
                FragmentTransaction fragmentTransactionone = getFragmentManager().beginTransaction();
                fragmentTransactionone.replace(R.id.frame,membersFragment);
                fragmentTransactionone.commit();
                txtMemberTxt.setBackgroundResource(R.drawable.selected_background);
                txtInvitation.setBackgroundResource(R.color.base_color1);
                break;
        }
    }


}
