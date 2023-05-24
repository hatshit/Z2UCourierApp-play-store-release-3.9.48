package com.zoom2u.slidemenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.StrictMode;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.Model_GetCourierRoute;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Hashtable;

public class CourierRouteDetail extends Fragment implements View.OnClickListener, GoogleMap.OnMarkerClickListener {//, SwipeRefreshLayout.OnRefreshListener {

    TextView unreadChatIcon,dhlRunBtn,standardBtn;
    GoogleMap mapCourierRouteView;
    RelativeLayout bgLayoutView;
    ArrayList<Model_GetCourierRoute> arrayOfGetCourierRoute;
    private Hashtable<String, Model_GetCourierRoute> markers;
    LinearLayout dl_ll,standard_ll,mapListBtnLayout;
    public static View courierRouteView;

    public static boolean isRouteForDHLRun = false;

    private int markerCounter = 0;
    ProgressDialog progressForCourierRoute;
    public CourierRouteDetail() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
            StrictMode.setThreadPolicy(policy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSlideMenuChatCounterTxt(TextView unreadChatIcon) {
        this.unreadChatIcon = unreadChatIcon;
    }

    //***********   Removing child view before load fragment   *********//
    @SuppressLint("InflateParams")
    private void mapViewInitialization(LayoutInflater inflater, ViewGroup container) {
        try {
            if (courierRouteView != null) {
                ViewGroup parent = (ViewGroup) courierRouteView.getParent();
                if (parent != null)
                    parent.removeView(courierRouteView);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        try {
            if (courierRouteView == null)
                courierRouteView = inflater.inflate(R.layout.courier_routeview, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapViewInitialization(inflater, container);
        inItRouteViewArray();
        //************ Ask for location permission ***************//
        BookingDetail_New.askForLocationPermission(getActivity());
        try {
            if (savedInstanceState != null) {
                try {
                    ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItem");
                    if (LoginZoomToU.NOVA_BOLD == null)
                        LoginZoomToU.staticFieldInit(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ActiveBookingView.getCurrentLocation(getActivity());
            inItCourierRouteDetailView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courierRouteView;
    }

    private void inItRouteViewArray() {
        if (arrayOfGetCourierRoute != null)
            arrayOfGetCourierRoute.clear();
        else
            arrayOfGetCourierRoute = new ArrayList<Model_GetCourierRoute>();

        if (markers != null)
            markers.clear();
        else
            markers = new Hashtable<String, Model_GetCourierRoute>();
    }

    //***********   Initialize map fragment and load map   *********//
    private void showMarkersOnMap() {
        try {
            if (BookingDetail_New.mapFragmentBookingDetail != null)
                BookingDetail_New.mapFragmentBookingDetail = null;
            BookingDetail_New.mapFragmentBookingDetail = (WorkaroundMapFragment) (getActivity().getFragmentManager().findFragmentById(R.id.mapInCourierRouteView));

            BookingDetail_New.mapFragmentBookingDetail.setMapReadyCallback(googleMap -> {
                if (mapCourierRouteView != null)
                    mapCourierRouteView.clear();
                else
                    mapCourierRouteView = BookingDetail_New.mapFragmentBookingDetail.getMap();
                mapCourierRouteView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //mapCourierRouteView.setMyLocationEnabled(true);
                mapCourierRouteView.getUiSettings().setZoomControlsEnabled(false);
                mapCourierRouteView.getUiSettings().setCompassEnabled(true);
                mapCourierRouteView.getUiSettings().setRotateGesturesEnabled(true);
                mapCourierRouteView.getUiSettings().setZoomGesturesEnabled(true);

                mapCourierRouteView.setOnMarkerClickListener(this);

                BookingDetail_New.mapFragmentBookingDetail.getView().setVisibility(View.VISIBLE);
                // Stop scrolling of layout View when map scroll
                BookingDetail_New.mapFragmentBookingDetail.setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                    }
                });

                updateMapMarkers();
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updateMapMarkers() {
        try {
            if (arrayOfGetCourierRoute != null) {
                if (arrayOfGetCourierRoute.size() > 0) {
                    if (mapCourierRouteView != null)
                        mapCourierRouteView.clear();
                    //            mapCourierRouteView.setMyLocationEnabled(true);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Model_GetCourierRoute model_getCourierRoute : arrayOfGetCourierRoute) {
//                        if (!model_getCourierRoute.getStatus().equals("Dropped Off")) {
                            View marker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_route, null);
                            ImageView routeMarkerImg = (ImageView) marker.findViewById(R.id.routeMarkerImg);

                            if (model_getCourierRoute.isRunningLate())
                                routeMarkerImg.setImageResource(R.drawable.marker_late);
                            else
                                routeMarkerImg.setImageResource(R.drawable.marker_ontime);

                            TextView numTxt = (TextView) marker.findViewById(R.id.num_txtRouteMarker);
                            numTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
                            numTxt.setText("" + model_getCourierRoute.getMarkerCounter());
                            Marker routeMarker;
                            double locationLat, locationLong;

                            locationLat = model_getCourierRoute.getLatitude();
                            locationLong = model_getCourierRoute.getLongitude();

                            if (createDrawableFromView(marker) != null)
                                routeMarker = mapCourierRouteView.addMarker(new MarkerOptions()
                                        .position(new LatLng(locationLat, locationLong))
                                        .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(marker)))
                                        .anchor(0.5f, 1));
                            else
                                routeMarker = mapCourierRouteView.addMarker(new MarkerOptions()
                                        .position(new LatLng(locationLat, locationLong))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_ontime))
                                        .anchor(0.5f, 1));

                            markers.put(routeMarker.getId(), model_getCourierRoute);
                            builder.include(new LatLng(locationLat, locationLong));
                            marker = null;
                            System.gc();
                        }
                        if (!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0"))
                            builder.include(new LatLng(Double.parseDouble(LoginZoomToU.getCurrentLocatnlatitude), Double.parseDouble(LoginZoomToU.getCurrentLocatnLongitude)));
                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 3);
                        mapCourierRouteView.moveCamera(cu);
                    }else
                    showDefaultLocation();
//                }
            }else
                showDefaultLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDefaultLocation() {
        if (!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0"))
            mapCourierRouteView.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(LoginZoomToU.getCurrentLocatnlatitude), Double.parseDouble(LoginZoomToU.getCurrentLocatnLongitude)), 15));
        else
            mapCourierRouteView.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-35.103802, 147.361288), 6));
    }

    // Convert a view to bitmap
    public Bitmap createDrawableFromView(View view) {
        Bitmap bitmap = null;
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.buildDrawingCache();
            bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e){
            e.printStackTrace();
        }

        return bitmap;
    }

    //*************** Init courier route detail view *************
    private void inItCourierRouteDetailView() {
        if (bgLayoutView == null)
            bgLayoutView = (RelativeLayout) courierRouteView.findViewById(R.id.bgLayoutView);
        bgLayoutView.setVisibility(View.GONE);

        standard_ll=courierRouteView.findViewById(R.id.standard_ll);
        dl_ll=courierRouteView.findViewById(R.id.dl_ll);
        mapListBtnLayout=courierRouteView.findViewById(R.id.mapListBtnLayout);
        dhlRunBtn=courierRouteView.findViewById(R.id.dhlRunBtn);
        dhlRunBtn.setOnClickListener(this);
        standardBtn=courierRouteView.findViewById(R.id.standardBtn);
        standardBtn.setOnClickListener(this);
        if(MainActivity.isIsBackGroundGray()) {
            mapListBtnLayout.setBackgroundResource(R.color.base_color_gray);
            dhlRunBtn.setBackgroundResource(R.color.base_color_gray);
            standardBtn.setBackgroundResource(R.drawable.selected_background_gray);
        }else{
            mapListBtnLayout.setBackgroundResource(R.color.base_color1);
            dhlRunBtn.setBackgroundResource(R.color.base_color1);
            standardBtn.setBackgroundResource(R.drawable.selected_background);
        }
       /* if (isRouteForDHLRun) {
            setSelectedColorAndTxtOfBtnHeader(R.id.dhlRunBtn, "#FFFFFF", R.drawable.roundedskybluebg);
            setUnSelectedColorAndTxtOfBtnHeader(R.id.standardBtn, "#0099CC", "#FFFFFF");
        } else {
            setSelectedColorAndTxtOfBtnHeader(R.id.standardBtn, "#FFFFFF", R.drawable.roundedskybluebg);
            setUnSelectedColorAndTxtOfBtnHeader(R.id.dhlRunBtn, "#0099CC", "#FFFFFF");
        }*/

        callRouteAPIForDHLOrDeliveries();
    }

    private void callRouteAPIForDHLOrDeliveries() {
        inItRouteViewArray();
        if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            //new GetCourierRouteDetail().execute();
            GetCourierRouteDetail();
        else
            DialogActivity.alertDialogView(getActivity(), "No Network !", "No network connection, Please try again later.");
    }

    private void setSelectedColorAndTxtOfBtnHeader(int viewId, String textColor, int bgColor) {
        ((TextView) courierRouteView.findViewById(viewId)).setTextColor(Color.parseColor(textColor));
        ((TextView) courierRouteView.findViewById(viewId)).setBackgroundResource(bgColor);
    }

    private void setUnSelectedColorAndTxtOfBtnHeader(int viewId, String textColor, String bgColor) {
        ((TextView) courierRouteView.findViewById(viewId)).setTextColor(Color.parseColor(textColor));
        ((TextView) courierRouteView.findViewById(viewId)).setBackgroundColor(Color.parseColor(bgColor));
    }

    @Override
    public void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        SlideMenuZoom2u.countChatBookingView = unreadChatIcon;
        Model_DeliveriesToChat.showExclamationForUnreadChat(unreadChatIcon);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try{
            outState.putInt("SlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getId() != null && markers != null && markers.size() > 0) {
           openMarkerDialog(marker, markers);
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dhlRunBtn:
                if (!isRouteForDHLRun) {
                    isRouteForDHLRun = true;
                    if(MainActivity.isIsBackGroundGray()) {
                        standardBtn.setBackgroundResource(R.color.base_color_gray);
                        dhlRunBtn.setBackgroundResource(R.drawable.selected_background_gray);
                    }else{
                        standardBtn.setBackgroundResource(R.color.base_color1);
                        dhlRunBtn.setBackgroundResource(R.drawable.selected_background);
                    }
                    callRouteAPIForDHLOrDeliveries();
                }
                break;
            case R.id.standardBtn:
                if (isRouteForDHLRun) {
                    isRouteForDHLRun = false;
                    if(MainActivity.isIsBackGroundGray()) {
                        standardBtn.setBackgroundResource(R.drawable.selected_background_gray);
                        dhlRunBtn.setBackgroundResource(R.color.base_color_gray);
                    }else{
                        standardBtn.setBackgroundResource(R.drawable.selected_background);
                        dhlRunBtn.setBackgroundResource(R.color.base_color1);
                    }
                    markerCounter = 0;
                    callRouteAPIForDHLOrDeliveries();
                }
                break;
        }
    }

    private void GetCourierRouteDetail(){
        final String[] responseCourierRouteStr = {""};


        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if(progressForCourierRoute == null)
                        progressForCourierRoute = new ProgressDialog(getActivity());
                    Custom_ProgressDialogBar.inItProgressBar(progressForCourierRoute);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler handler = new WebserviceHandler();
                    if (isRouteForDHLRun)
                        responseCourierRouteStr[0] = handler.getCourierRouteDetails();       // For DHL deliveries
                    else
                        responseCourierRouteStr[0] = handler.getCourierDeliveriesRouteData();  // For Normal deliveries

                    handler = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    try {
                        JSONArray jsonArrayOfCourierRoute = new JSONArray(responseCourierRouteStr[0]);
                        if (jsonArrayOfCourierRoute.length() > 0){
                            markerCounter = 0;
                            if (isRouteForDHLRun) {
                                for (int i = 0; i < jsonArrayOfCourierRoute.length(); i++) {
                                    addRouteDataToArray(jsonArrayOfCourierRoute, i, true);
                                }
                            } else {
                                for (int i = 0; i < jsonArrayOfCourierRoute.length(); i++) {
                                    addRouteDataToArray(jsonArrayOfCourierRoute, i, false);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DialogActivity.alertDialogView(getActivity(), "Error!", "Something went wrong, Please try again later.");
                    }
                    showMarkersOnMap();
                    Custom_ProgressDialogBar.dismissProgressBar(progressForCourierRoute);
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(getActivity(), "Error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();

    }

    //************ Add route data to array from API response***********
    private void addRouteDataToArray(JSONArray jsonArrayOfCourierRoute, int i, boolean isRouteForDHLRun) {
        try {
            Model_GetCourierRoute modelGetCourierRoute;
            if (isRouteForDHLRun) {
                modelGetCourierRoute = new Model_GetCourierRoute(jsonArrayOfCourierRoute.getJSONObject(i), (i + 1));
                arrayOfGetCourierRoute.add(modelGetCourierRoute);
            } else {
                if (!jsonArrayOfCourierRoute.getJSONObject(i).getString("Status").equals("Dropped Off") && !jsonArrayOfCourierRoute.getJSONObject(i).getString("Status").equals("Returned")) {
                    markerCounter++;
                    modelGetCourierRoute = new Model_GetCourierRoute(jsonArrayOfCourierRoute.getJSONObject(i), markerCounter, 0);
                    arrayOfGetCourierRoute.add(modelGetCourierRoute);
                }
            }
            modelGetCourierRoute = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    Dialog alertDialogRoute;
    private void openMarkerDialog(final Marker marker, final Hashtable<String, Model_GetCourierRoute> markers){
        try {
            bgLayoutView.setVisibility(View.VISIBLE);
            if(alertDialogRoute != null) {
                try {
                    if (alertDialogRoute.isShowing())
                        alertDialogRoute.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertDialogRoute = null;
            }

            alertDialogRoute = new Dialog(getActivity());
            alertDialogRoute.setCancelable(false);
            alertDialogRoute.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialogRoute.setContentView(R.layout.route_model_alert);

            Window window = alertDialogRoute.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            TextView etaTxtValueRouteAlrt = (TextView) alertDialogRoute.findViewById(R.id.etaTxtValueRouteAlrt);

            ImageView cancelBtnRouteAlert = (ImageView) alertDialogRoute.findViewById(R.id.cancelBtnRouteAlert);
            cancelBtnRouteAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bgLayoutView.setVisibility(View.GONE);
                    dissmissRouteAlertDialog();
                }
            });
            ImageView countImgRouteAlrt = (ImageView) alertDialogRoute.findViewById(R.id.countImgRouteAlrt);
            if (markers.get(marker.getId()).isRunningLate()) {
                etaTxtValueRouteAlrt.setTextColor(Color.parseColor("#FF476A"));
                countImgRouteAlrt.setImageResource(R.drawable.marker_late);
            }else {
                etaTxtValueRouteAlrt.setTextColor(Color.parseColor("#7BCE20"));
                countImgRouteAlrt.setImageResource(R.drawable.marker_ontime);
            }

            TextView counterTxtRouteAlrt = (TextView) alertDialogRoute.findViewById(R.id.counterTxtRouteAlrt);
            counterTxtRouteAlrt.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);
            counterTxtRouteAlrt.setText(""+markers.get(marker.getId()).getMarkerCounter());

            TextView titleTxtRouteAlert = (TextView) alertDialogRoute.findViewById(R.id.titleTxtRouteAlert);
            titleTxtRouteAlert.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);

            TextView courierNameRouteAlrt = (TextView) alertDialogRoute.findViewById(R.id.courierNameRouteAlrt);
            courierNameRouteAlrt.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);

            TextView addressTxtRouteAlrt = (TextView) alertDialogRoute.findViewById(R.id.addressTxtRouteAlrt);
            addressTxtRouteAlrt.setTypeface(LoginZoomToU.NOVA_REGULAR);

            TextView arriveBeforeTxtRouteAlrt = (TextView) alertDialogRoute.findViewById(R.id.arriveBeforeTxtRouteAlrt);
            arriveBeforeTxtRouteAlrt.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);
            TextView arriveBeforeTxtValueRouteAlrt = (TextView) alertDialogRoute.findViewById(R.id.arriveBeforeTxtValueRouteAlrt);
            arriveBeforeTxtValueRouteAlrt.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);

            TextView eTATxtRouteAlrt = (TextView) alertDialogRoute.findViewById(R.id.eTATxtRouteAlrt);
            eTATxtRouteAlrt.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);

            etaTxtValueRouteAlrt.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);

            if (markers.get(marker.getId()).getStatus().equals("Accepted") || markers.get(marker.getId()).getStatus().equals("On Route to Pickup"))
                if (isRouteForDHLRun) {
                    titleTxtRouteAlert.setText("Drop off");
                    arriveBeforeTxtRouteAlrt.setText("Arrive before");
                } else {
                    titleTxtRouteAlert.setText("Pick up");
                    arriveBeforeTxtRouteAlrt.setText("Parcel Ready After");
                }
            else {
                titleTxtRouteAlert.setText("Drop off");
                arriveBeforeTxtRouteAlrt.setText("Arrive before");
            }

            courierNameRouteAlrt.setText(markers.get(marker.getId()).getDropContactPerson());
            addressTxtRouteAlrt.setText(markers.get(marker.getId()).getDropOffAddress());
            setRequestedDateTime(arriveBeforeTxtValueRouteAlrt, markers.get(marker.getId()).getDropDateTime());
            setRequestedDateTime(etaTxtValueRouteAlrt, markers.get(marker.getId()).getDropETA());

            Button viewBookingBtnRouteAlrt = (Button) alertDialogRoute.findViewById(R.id.viewBookingBtnRouteAlrt);
            viewBookingBtnRouteAlrt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
            viewBookingBtnRouteAlrt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bgLayoutView.setVisibility(View.GONE);
                    dissmissRouteAlertDialog();
                    Intent activeBookingDetailIntent = new Intent(getActivity(), ActiveBookingDetail_New.class);
                    activeBookingDetailIntent.putExtra("RouteViewCalling", 181);
                    activeBookingDetailIntent.putExtra("bookingIDToLoad", markers.get(marker.getId()).getBookingId());
                    startActivity(activeBookingDetailIntent);
                    activeBookingDetailIntent = null;
                }
            });

            alertDialogRoute.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //**************** Set date time to required text field *****************
    private void setRequestedDateTime(TextView arriveBeforeTxtValueRouteAlrt, String dropDateTime) {
        if (!dropDateTime.equals("") && !dropDateTime.equals("null")){
            try{
                String [] dropDateTimeFromServer;
                if (isRouteForDHLRun)
                    dropDateTimeFromServer = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServerForCourierRouteForDHL(dropDateTime).split(" ");
                else
                    dropDateTimeFromServer = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(dropDateTime).split(" ");
                arriveBeforeTxtValueRouteAlrt.setText(dropDateTimeFromServer[1]+" "+dropDateTimeFromServer[2]);
                dropDateTimeFromServer = null;
            }catch (Exception e){
                e.printStackTrace();
                arriveBeforeTxtValueRouteAlrt.setText("-");
            }
        }else
            arriveBeforeTxtValueRouteAlrt.setText("-");
    }

    //**************** Close route alert dialog *****************
    private void dissmissRouteAlertDialog() {
        try {
            if (alertDialogRoute.isShowing())
                alertDialogRoute.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
