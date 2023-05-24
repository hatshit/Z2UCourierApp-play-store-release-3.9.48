package com.zoom2u.onboardpopup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;


import com.zoom2u.R;

import java.util.ArrayList;
import java.util.List;

public class OnBoardViewPagerAdapter extends PagerAdapter {

    Context context;
    List<OnBoardModel> list;
    OnBoardActivity onBoardActivity;
    Boolean isFirstItemPositionChange=true;
    private ArrayList<String> recyclerDataArrayList;
    public OnBoardViewPagerAdapter(Context context,List<OnBoardModel> list,OnBoardActivity onBoardActivity){
        this.context = context;
        this.list=list;
        this.onBoardActivity=onBoardActivity;
    }

    @Override
    public int getCount() {
        return  list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.onboard_item_view,container,false);

        ImageView slidetitleimage = (ImageView) view.findViewById(R.id.img_logo);
        TextView slideHeading = (TextView) view.findViewById(R.id.tv_head);
        TextView slideDesciption = (TextView) view.findViewById(R.id.tv_msg);
        LinearLayout parentView=(LinearLayout) view.findViewById(R.id.parent_view);
        RecyclerView recyclerView=(RecyclerView)view.findViewById(R.id.screen_6_item);
        TextView screen7Heading=(TextView)view.findViewById(R.id.screen7Heading);

        recyclerDataArrayList=new ArrayList<>();
        recyclerDataArrayList.add("HeadSet");
        recyclerDataArrayList.add("Safety Shoes");
        recyclerDataArrayList.add("Trolley");
        recyclerDataArrayList.add("A smile");
        recyclerDataArrayList.add("Safety Vest");


        Screen6Adapter adapter=new Screen6Adapter(recyclerDataArrayList,context);
        GridLayoutManager layoutManager=new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        slidetitleimage.setImageResource(list.get(position).getImageId());
        slideHeading.setText(list.get(position).getHeading());
        slideDesciption.setText(list.get(position).getMsg());



        if(position==0 ) {
            slideDesciption.setVisibility(View.VISIBLE);
            parentView.setBackgroundResource(R.color.base_color);
            slideHeading.setVisibility(View.VISIBLE);
            if(isFirstItemPositionChange){
                onBoardActivity.setButtonFirstUi();
                isFirstItemPositionChange=false;
            }
        }else if(position==2){
            slideDesciption.setVisibility(View.VISIBLE);
            slidetitleimage.requestLayout();
            slidetitleimage.getLayoutParams().height = 500;
            slidetitleimage.getLayoutParams().width = 500;
            slidetitleimage.setScaleType(ImageView.ScaleType.FIT_XY);
            slideHeading.setVisibility(View.GONE);
            parentView.setBackgroundResource(R.color.gunmetal_new1);
        }else if(position==3){
            slideDesciption.setVisibility(View.VISIBLE);
            slideHeading.setVisibility(View.GONE);
            parentView.setBackgroundResource(R.color.gunmetal_new1);
        }else if(position==4){
            slideDesciption.setVisibility(View.VISIBLE);
            slideHeading.setVisibility(View.GONE);
            parentView.setBackgroundResource(R.color.gunmetal_new1);
        }
        else if(position==5){
            slideDesciption.setVisibility(View.GONE);
            slideHeading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            parentView.setBackgroundResource(R.color.gunmetal_new1);
        }
        else if(position==6){
           slideDesciption.setVisibility(View.GONE);
           screen7Heading.setVisibility(View.VISIBLE);
           screen7Heading.setText("Great to have you onboard!");
           parentView.setBackgroundResource(R.color.gunmetal_new1);
        }
        else {
            slideHeading.setVisibility(View.VISIBLE);
            parentView.setBackgroundResource(R.color.gunmetal_new1);
        }
        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}