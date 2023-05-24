package com.zoom2u.onboardpopup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.run_popup.DialogRunPopup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jyotiraditya singh on 5-04-2022.
 */
public class OnBoardActivity extends Activity implements View.OnClickListener {
    ConstraintLayout parentView;
    CustomViewPager viewPager;
    LinearLayout dotLayout;
    TextView[] dots;
    Button nextBtn;
    LinearLayout nextBtn1;
    OnBoardViewPagerAdapter onBoardViewPagerAdapter;
    List<OnBoardModel> list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_board_view);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        initMessages();
        parentView =(ConstraintLayout)findViewById(R.id.parent_view);
        nextBtn = (Button)findViewById(R.id.next_btn);
        nextBtn1 = (LinearLayout)findViewById(R.id.next_btn1);
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        dotLayout = (LinearLayout) findViewById(R.id.indicator_layout);
        nextBtn1.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        viewPager.disableScroll(false);
        onBoardViewPagerAdapter = new OnBoardViewPagerAdapter(this,list,OnBoardActivity.this);
        viewPager.setAdapter(onBoardViewPagerAdapter);
        setUpindicator(0);
        viewPager.addOnPageChangeListener(viewListener);

    }

    void setButtonFirstUi(){
        setStatusBarColor(R.color.base_color);
        parentView.setBackgroundResource(R.color.base_color);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn1.setVisibility(View.GONE);
    }



    private void initMessages() {
        list.clear();
        list.add(new OnBoardModel(R.drawable.ob_map_icon, "Welcome to Zoom2u.", getString(R.string.text_ob1)));
        list.add(new OnBoardModel(R.drawable.ob_screen2, "Firstly, this isn't a job.", getString(R.string.text_ob2)));
        list.add(new OnBoardModel(R.drawable.ob_screen3, "", getString(R.string.text_ob3)));
        list.add(new OnBoardModel(R.drawable.ob_screen4, "", getString(R.string.text_ob4)));
        list.add(new OnBoardModel(R.drawable.ob_screen5, "", getString(R.string.text_ob5)));
        list.add(new OnBoardModel(R.drawable.ob_screen6, getString(R.string.text_ob6),""));
        list.add(new OnBoardModel(R.drawable.ob_screen7, "Good Luck!", getString(R.string.text1)));

    }


    public void setUpindicator(int position){
        dots = new TextView[7];
        dotLayout.removeAllViews();
        for (int i = 0 ; i < dots.length ; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.white,getApplicationContext().getTheme()));
            dotLayout.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.active,getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {

            setUpindicator(position);
            if (position== 0){
                setStatusBarColor(R.color.base_color);
                parentView.setBackgroundResource(R.color.base_color);
                nextBtn.setVisibility(View.VISIBLE);
                nextBtn.setText("Next");
                nextBtn.setTextColor(getResources().getColor(R.color.gunmetal_new1));
                nextBtn.setBackgroundResource(R.drawable.chip_background_white);
                nextBtn1.setVisibility(View.GONE);
             }else if(position==6){
                setStatusBarColor(R.color.gunmetal_new1);
                parentView.setBackgroundResource(R.color.gunmetal_new1);
                nextBtn.setVisibility(View.VISIBLE);
                nextBtn.setBackgroundResource(R.drawable.chip_background);
                nextBtn.setText("Let's get started");
                nextBtn.setTextColor(getResources().getColor(R.color.white));
                nextBtn1.setVisibility(View.GONE);
            }
            else {
                setStatusBarColor(R.color.gunmetal_new1);
                parentView.setBackgroundResource(R.color.gunmetal_new1);
                nextBtn1.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setStatusBarColor(int color){
        Window window = OnBoardActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(OnBoardActivity.this.getResources().getColor(color));

    }


    private int getitem(int i){
        return viewPager.getCurrentItem() + i;
    }

    @Override
    public void onClick(View view) {
        int id =view.getId();
        switch(id){
            case R.id.next_btn:
            case R.id.next_btn1:
                if (getitem(0) < 6)
                    viewPager.setCurrentItem(getitem(1),true);
                else {
                    Intent loginPage = new Intent(this, SlideMenuZoom2u.class);
                    loginPage.putExtra("intentFromLoginUI", "intentFromLoginUI");
                    this.startActivity(loginPage);
                    loginPage = null;
                }
                break;
        }
    }
}
