package com.zoom2u.dialogactivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Picasso;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.slidemenu.offerrequesthandlr.Bid_RequestView_Detail;
import com.zoom2u.slidemenu.offerrequesthandlr.TouchImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class BidImageDialog extends Activity {
    public static Dialog enterFieldDialog;
    static HashMap<Integer, Bitmap> bitmapHashMap = new HashMap<Integer, Bitmap>();
    static int packageImgCount;
    static ConstraintLayout bidDetailMainLayout;
    public BidImageDialog() {
    }

    public static void alertDialogToFinishView( String getPackageImages, Context context,int images_array_length){
        try{
            if(enterFieldDialog != null)
                if(enterFieldDialog.isShowing())
                    enterFieldDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


        try {
            if(enterFieldDialog != null)
                enterFieldDialog = null;
            enterFieldDialog = new Dialog(context,android.R.style.Theme_Light);
            enterFieldDialog.setCancelable(false);
            enterFieldDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A000000")));
            enterFieldDialog.setContentView(R.layout.a);

            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            ViewPager viewPagerQuote = enterFieldDialog.findViewById(R.id.viewPagerQuote);
            ImageView bidDetailRightArrow = enterFieldDialog.findViewById(R.id.bidDetailRightArrow);
            ImageView bidDetailLeftArrow = enterFieldDialog.findViewById(R.id.bidDetailLeftArrow);

            bidDetailMainLayout=enterFieldDialog.findViewById(R.id.bidDetailMainLayout);
            bidDetailMainLayout.setOnClickListener(v -> enterFieldDialog.dismiss());
            String[] packageImagesArray = new String[0];

            if (!getPackageImages.equals("")) {
                final AQuery aQuery = new AQuery(context);
                packageImagesArray = getPackageImages.split(",");
                if (packageImagesArray.length > 0) {
                    if (packageImagesArray.length == 1)
                        hideBothArrowBtn(bidDetailRightArrow,bidDetailLeftArrow);
                    packageImgCount = 1;
                    for (int i = 0; i < packageImagesArray.length; i++) {
                        final int setImagePosition = i;
                        aQuery.ajax(packageImagesArray[i], Bitmap.class, 0, new AjaxCallback<Bitmap>() {
                            @Override
                            public void callback(String url, Bitmap bm, AjaxStatus status) {
                                bitmapHashMap.put(setImagePosition, bm);
                            }
                        });
                    }
                } else {
                    hideBothArrowBtn(bidDetailRightArrow,bidDetailLeftArrow);
                }
            } else {
                hideBothArrowBtn(bidDetailRightArrow,bidDetailLeftArrow);
            }

            viewPagerQuote.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    packageImgCount = position + 1;
                    if (packageImgCount == 1) {
                        enableImageArrowBtn(bidDetailRightArrow);
                        disableImageArrowBtn(bidDetailLeftArrow);
                    } else if (packageImgCount > 1 && packageImgCount < images_array_length) {
                        enableImageArrowBtn(bidDetailLeftArrow);
                        enableImageArrowBtn(bidDetailRightArrow);
                    } else if (packageImgCount == images_array_length) {
                        enableImageArrowBtn(bidDetailLeftArrow);
                        disableImageArrowBtn(bidDetailRightArrow);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });


            bidDetailRightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    packageImgCount++;
                    if (packageImgCount < images_array_length) {
                        viewPagerQuote.setCurrentItem(viewPagerQuote.getCurrentItem()+1);
                        enableImageArrowBtn(bidDetailLeftArrow);
                        enableImageArrowBtn(bidDetailRightArrow);
                    } else if (packageImgCount == images_array_length){
                        viewPagerQuote.setCurrentItem(viewPagerQuote.getCurrentItem()+1);
                        enableImageArrowBtn(bidDetailLeftArrow);
                        disableImageArrowBtn(bidDetailRightArrow);
                    }else
                        packageImgCount--;
                }
            });
            bidDetailLeftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    packageImgCount--;
                    if (packageImgCount > 1) {
                        viewPagerQuote.setCurrentItem(viewPagerQuote.getCurrentItem()-1);
                        enableImageArrowBtn(bidDetailLeftArrow);
                        enableImageArrowBtn(bidDetailRightArrow);
                    } else if (packageImgCount == 1){
                        viewPagerQuote.setCurrentItem(viewPagerQuote.getCurrentItem()-1);
                        enableImageArrowBtn(bidDetailRightArrow);
                        disableImageArrowBtn(bidDetailLeftArrow);
                    }else
                        packageImgCount++;
                }
            });

            PagerAdapter adapter = new ImageAdapter(context,packageImagesArray);
            viewPagerQuote.setAdapter(adapter);
            enterFieldDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ImageAdapter extends PagerAdapter {
        Context context;
        String[] packageImagesArray;
        ImageView imageView;

        ImageAdapter(Context context,String[] packageImagesArray){
            this.context = context;
            this.packageImagesArray=packageImagesArray; }

        @Override
        public int getCount() {
            if (packageImagesArray != null)
                return packageImagesArray.length;
            else
                return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            View viewItem = inflater.inflate(R.layout.bidrequest_image_item, container, false);

            imageView = (ImageView) viewItem.findViewById(R.id.imageViewBidDetailItem);
            TextView bidDetail_imageTypeTxt = (TextView) viewItem.findViewById(R.id.bidDetail_imageTypeTxt);
            bidDetail_imageTypeTxt.setTypeface(LoginZoomToU.NOVA_BOLD);
            if (packageImagesArray != null) {
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                param.addRule(RelativeLayout.CENTER_VERTICAL);
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);
                imageView.setLayoutParams(param);
                //imageView.setImageBitmap(getBitmapFromPatch(packageImagesArray[position]));
                Picasso.with(context).load(packageImagesArray[position]).placeholder(R.drawable.bid_placeholder).into(imageView);
                bidDetail_imageTypeTxt.setVisibility(View.GONE);
            } else {
               // setShipmentImgOrText(requestView_detailPojo.getShipmentsArray(), imageView, bidDetail_imageTypeTxt);
            }

            ((ViewPager)container).addView(viewItem);

            return viewItem;
        }



        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

    }

    //************** Enable package image arrow button *************
    private static void enableImageArrowBtn(ImageView imageArrowBtn){
        imageArrowBtn.setAlpha(1.0f);
        imageArrowBtn.setEnabled(true);
        imageArrowBtn.setClickable(true);
    }

    //************** Disable package image arrow button *************
    private static void disableImageArrowBtn(ImageView imageArrowBtn){
        imageArrowBtn.setAlpha(0.5f);
        imageArrowBtn.setEnabled(false);
        imageArrowBtn.setClickable(false);
    }
    private static void hideBothArrowBtn(ImageView bidDetailRightArrow, ImageView bidDetailLeftArrow) {
        bidDetailRightArrow.setVisibility(View.GONE);
        bidDetailLeftArrow.setVisibility(View.GONE);
    }
}
