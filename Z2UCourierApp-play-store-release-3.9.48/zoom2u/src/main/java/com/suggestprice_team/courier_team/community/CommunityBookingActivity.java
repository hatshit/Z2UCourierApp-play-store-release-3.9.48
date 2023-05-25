package com.suggestprice_team.courier_team.community;

import static com.zoom2u.slidemenu.BookingView.bookingViewSelection;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.z2u.booking.vc.CompletedView;
import com.z2u.booking.vc.NewBookingView;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.z2u.chatview.ChatViewBookingScreen;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.ShiftViewDetail;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CommunityBookingActivity extends Activity implements View.OnClickListener {

    RecyclerView recycle;
    RelativeLayout headerDLayout;
    LinearLayout nobookingoffer;
    ImageView backFromImg, chatIconTeamList;
    CommunityBookingOfferAdapter communityBookingOfferAdapter;
    private ProgressDialog progressDialogSRView;
    SwipeRefreshLayout refreshlist;
    String responseCommunityofferbookinglist;
    String responseCommunityofferbookingAcceptRejectedBooking;
    int offetid, bookingid;

    public static final int REQUEST_CODE_FOR_NEW_BOOKING_DETAIL = 1020;

    private All_Bookings_DataModels newBookingDetailModel;
    boolean isshow1 =true;

    public static ArrayList<All_Bookings_DataModels> list = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communitybookingactivity);

        recycle = findViewById(R.id.recycle);
        headerDLayout = findViewById(R.id.headerDLayout);
        backFromImg = findViewById(R.id.backFromImg);
        chatIconTeamList = findViewById(R.id.chatIconTeamList);
        refreshlist = findViewById(R.id.refreshlist);
        nobookingoffer = findViewById(R.id.nobookingoffer);

        Window window = CommunityBookingActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (MainActivity.isIsBackGroundGray()) {
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerDLayout.setBackgroundResource(R.color.base_color_gray);
        } else {
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerDLayout.setBackgroundResource(R.color.base_color1);
        }

        list.clear();
        refreshlist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callApiCommuniyofferBookinglist();
                refreshlist.setRefreshing(false);
            }
        });

        chatIconTeamList.setOnClickListener(this);
        backFromImg.setOnClickListener(this);

        callApiCommuniyofferBookinglist();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // offerBookinglist Api
    private void callApiCommuniyofferBookinglist() {

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetcommunityofferBookinglistAsyncTask();
            // new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(CommunityBookingActivity.this, "No Network!", "No network connection, Please try again later.");
    }

    /*************  Save activity state when process killed in background*************/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            outState.putBoolean("SetRoutific", WebserviceHandler.isRoutific);
            outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
            outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
            //        outState.putParcelableArrayList("NewBookingArray", BookingView.bookingListArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    private void GetcommunityofferBookinglistAsyncTask() {

        new MyAsyncTasks() {
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(CommunityBookingActivity.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunityofferbookinglist = webServiceHandler.getCommunityofferbookinglist();
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);

                try {
                    JSONObject newBookingResponseJObj = new JSONObject(responseCommunityofferbookinglist);
                    if (newBookingResponseJObj.getBoolean("success")) {
                        JSONArray newBookingResponseArray = newBookingResponseJObj.getJSONArray("list");
                        if (newBookingResponseArray.length() > 0) {
                            list.clear();
                            recycle.setVisibility(View.VISIBLE);
                            nobookingoffer.setVisibility(View.GONE);
                            for (int i = 0; i < newBookingResponseArray.length(); i++) {
                                JSONObject responseDataObject = newBookingResponseArray.getJSONObject(i);

                                String distanceFromCurrentLoc = LoginZoomToU.checkInternetwithfunctionality.distanceBetweenTwoPosition
                                        (LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude,
                                                responseDataObject.getString("PickupGPSX"),
                                                responseDataObject.getString("PickupGPSY"));

                                String orderNumberForMenuLog = "";
                                try {
                                    if (!responseDataObject.getString("OrderNo").equals("null") || responseDataObject.getString("OrderNo") != null)
                                        orderNumberForMenuLog = responseDataObject.getString("OrderNo");
                                    else
                                        orderNumberForMenuLog = "";
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    orderNumberForMenuLog = "";
                                }

                                All_Bookings_DataModels dataModel_AllBookingList = new All_Bookings_DataModels();

                                dataModel_AllBookingList.setBookingId(responseDataObject.getInt("BookingId"));

                                dataModel_AllBookingList.setOfferId(responseDataObject.getInt("OfferId"));
                                dataModel_AllBookingList.setBookingRefNo(responseDataObject.getString("BookingRefNo"));
                               // dataModel_AllBookingList.setCustomerId(responseDataObject.getString("CustomerId"));
                                //dataModel_AllBookingList.setNewCustomer(responseDataObject.getBoolean("isNewCustomer"));
                                dataModel_AllBookingList.setPickupDateTime(responseDataObject.getString("PickupDateTime"));
                                dataModel_AllBookingList.setPick_Address(responseDataObject.getString("PickupAddress"));
                                dataModel_AllBookingList.setPick_ContactName(responseDataObject.getString("PickupContactName"));
                                dataModel_AllBookingList.setPick_GPSX(responseDataObject.getString("PickupGPSX"));
                                dataModel_AllBookingList.setPick_GPSY(responseDataObject.getString("PickupGPSY"));
                                //dataModel_AllBookingList.setPick_Notes(responseDataObject.getString("PickupNotes"));
                                //dataModel_AllBookingList.setPick_Phone(responseDataObject.getString("PickupPhone"));
                                dataModel_AllBookingList.setPick_Suburb(responseDataObject.getString("PickupSuburb"));
                                dataModel_AllBookingList.setDropDateTime(responseDataObject.getString("DropDateTime"));
                                dataModel_AllBookingList.setDrop_Address(responseDataObject.getString("DropAddress"));
                                dataModel_AllBookingList.setDrop_ContactName(responseDataObject.getString("DropContactName"));
                                dataModel_AllBookingList.setDrop_GPSX(responseDataObject.getString("DropGPSX"));
                                dataModel_AllBookingList.setDrop_GPSY(responseDataObject.getString("DropGPSY"));
                               // dataModel_AllBookingList.setDrop_Notes(responseDataObject.getString("DropNotes"));
                               // dataModel_AllBookingList.setDrop_Phone(responseDataObject.getString("DropPhone"));
                                dataModel_AllBookingList.setDrop_Suburb(responseDataObject.getString("DropSuburb"));
                                dataModel_AllBookingList.setCreatedDateTime(responseDataObject.getString("CreatedDateTime"));
                                dataModel_AllBookingList.setDeliverySpeed(responseDataObject.getString("DeliverySpeed"));
                                dataModel_AllBookingList.setDistance(responseDataObject.getString("Distance"));
                                dataModel_AllBookingList.setNotes(responseDataObject.getString("Notes"));
                                dataModel_AllBookingList.setVehicle(responseDataObject.getString("Vehicle"));
                                dataModel_AllBookingList.setSource(responseDataObject.getString("Source"));
                                //dataModel_AllBookingList.setPackage(responseDataObject.getString("PackageType"));

                                try {
                                    dataModel_AllBookingList.setRoutePolyline(responseDataObject.getString("RoutePolyline"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
                                for (int k = 0; k < responseDataObject.getJSONArray("Shipments").length(); k++) {
                                    HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
                                    JSONObject jObjOfShipmentItem = responseDataObject.getJSONArray("Shipments").getJSONObject(k);
                                        objOFShipments.put("Category", jObjOfShipmentItem.getString("Category"));
                                    objOFShipments.put("Quantity", jObjOfShipmentItem.getInt("Quantity"));
                                    try {
                                        objOFShipments.put("LengthCm", jObjOfShipmentItem.getDouble("LengthCm"));
                                        objOFShipments.put("WidthCm", jObjOfShipmentItem.getDouble("WidthCm"));
                                        objOFShipments.put("HeightCm", jObjOfShipmentItem.getDouble("HeightCm"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        objOFShipments.put("LengthCm", 0.0);
                                        objOFShipments.put("WidthCm", 0.0);
                                        objOFShipments.put("HeightCm", 0.0);
                                    }
                                    try {
                                        objOFShipments.put("ItemWeightKg", jObjOfShipmentItem.getDouble("ItemWeightKg"));
                                        objOFShipments.put("TotalWeightKg", jObjOfShipmentItem.getDouble("TotalWeightKg"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        objOFShipments.put("ItemWeightKg", 0.0);
                                        objOFShipments.put("TotalWeightKg", 0.0);
                                    }
                                    arrayOfShipments.add(objOFShipments);
                                    objOFShipments = null;
                                }
                                dataModel_AllBookingList.setShipmentsArray(arrayOfShipments);
                                arrayOfShipments = null;
                                dataModel_AllBookingList.setPrice(responseDataObject.getDouble("CourierPrice"));
                                dataModel_AllBookingList.setDistanceFromCurrentLocation(distanceFromCurrentLoc);
                                // dataModel_AllBookingList.setOrderNumber(orderNumberForMenuLog);
                                //	dataModel_AllBookingList.setPricingBreakdown_model(responseDataObject.getJSONObject("PricingBreakdowns"));
                                list.add(dataModel_AllBookingList);
                                dataModel_AllBookingList = null;
                                distanceFromCurrentLoc = null;
                                responseDataObject = null;
                            }

                            if (list != null) {
                                setAdapters(list);
                            } else {
                                recycle.setVisibility(View.GONE);
                                nobookingoffer.setVisibility(View.VISIBLE);
                            }
                        } else {
                            recycle.setVisibility(View.GONE);
                            nobookingoffer.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    recycle.setVisibility(View.GONE);
                    nobookingoffer.setVisibility(View.VISIBLE);
                   // Toast.makeText(CommunityBookingActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

        }.execute();
    }


    private void setAdapters(ArrayList<All_Bookings_DataModels> list) {
        recycle.setLayoutManager(new LinearLayoutManager(CommunityBookingActivity.this));
        communityBookingOfferAdapter = new CommunityBookingOfferAdapter(CommunityBookingActivity.this, list);
        recycle.setAdapter(communityBookingOfferAdapter);
        communityBookingOfferAdapter.notifyDataSetChanged();

        communityBookingOfferAdapter.setOnItemClickListener(new CommunityBookingOfferAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String type) {
                try
                {
                    offetid = list.get(position).getOfferId();
                    bookingid = list.get(position).getBookingId();
                    if (type.equals("swipeAccept")) {
                        callApiAcceptReject(position, "Accept");
                    } else if (type.equals("swipeReject")) {
                        callApiAcceptReject(position, "Reject");
                    } else if (type.equals("details")) {
                        offetid=list.get(position).getOfferId();
                        bookingid=list.get(position).getBookingId();
                    /* BookingView.bookingListArray.clear();
                        BookingView.bookingListArray.add(list.get(position));*/
                        Intent i = new Intent(CommunityBookingActivity.this, BookingDetail_New.class);
                        Bundle newBookingDetailBundle = new Bundle();
                        newBookingDetailBundle.putInt("position", 0);
                        newBookingDetailBundle.putInt("listPosition", position);
                        i.putExtra("NewBookingBundle", newBookingDetailBundle);
                        i.putExtra("my_boolean_key", isshow1);
                        i.putExtra("list", list);
                        i.putExtra("offerId",offetid);
                        i.putExtra("bookingId",bookingid);
                        startActivityForResult(i, REQUEST_CODE_FOR_NEW_BOOKING_DETAIL);
                        newBookingDetailBundle = null;
                        i = null;
                    }
                }catch (Exception e) {
                 //   Toast.makeText(CommunityBookingActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_NEW_BOOKING_DETAIL:
                if (resultCode == RESULT_OK) {
                    if (data.getStringExtra("result") != null) {
                        String result = data.getStringExtra("result").toString();
                        String position = data.getStringExtra("listPosition").toString();
                        offetid = data.getExtras().getInt("offerId");
                        bookingid = data.getExtras().getInt("bookingId");
                        if (result.equals("Accept")) {
                            //Api call for Accepted Item
                       callApiAcceptReject(Integer.parseInt(position),"Accept");
                        } else {
                            //Api call for Rejected Item
                        callApiAcceptReject(Integer.parseInt(position),"Reject");
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void callApiAcceptReject(int position, String type) {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            callApiAcceptRejectAsyncTask(position, type);
            // new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(CommunityBookingActivity.this, "No Network!", "No network connection, Please try again later.");
    }

    private void callApiAcceptRejectAsyncTask(int position, String type) {
        new MyAsyncTasks() {
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(CommunityBookingActivity.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (type.equals("Accept")) {
                        responseCommunityofferbookingAcceptRejectedBooking = webServiceHandler.getCommunitybookingofferAcceptBooking(offetid, bookingid);
                        webServiceHandler = null;
                    } else {
                        responseCommunityofferbookingAcceptRejectedBooking = webServiceHandler.getCommunitybookingofferRejectBooking(offetid, bookingid);
                        webServiceHandler = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onPostExecute() {
                try {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                    JSONObject newBookingResponseJObj = new JSONObject(responseCommunityofferbookingAcceptRejectedBooking);
                    if (type.equals("Accept")) {
                        if (newBookingResponseJObj.has("Message") && !newBookingResponseJObj.isNull("Message")){
                            Toast.makeText(CommunityBookingActivity.this,newBookingResponseJObj.getString("Message"), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(CommunityBookingActivity.this, "Booking accepted", Toast.LENGTH_SHORT).show();
                            communityBookingOfferAdapter.removeitem(position);
                            communityBookingOfferAdapter.notifyDataSetChanged();
                            if (list.size() > 0) {
                                recycle.setVisibility(View.VISIBLE);
                                nobookingoffer.setVisibility(View.GONE);
                            } else {
                                BookingView.bookingViewSelection = 2;
                                Intent i = new Intent(CommunityBookingActivity.this, SlideMenuZoom2u.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }else {
                        if (newBookingResponseJObj.has("success") && !newBookingResponseJObj.isNull("success")) {
                            if (newBookingResponseJObj.getBoolean("success")){
                                Toast.makeText(CommunityBookingActivity.this, "Booking rejected", Toast.LENGTH_SHORT).show();
                                communityBookingOfferAdapter.removeitem(position);
                                communityBookingOfferAdapter.notifyDataSetChanged();
                                if (list.size() > 0) {
                                    recycle.setVisibility(View.VISIBLE);
                                    nobookingoffer.setVisibility(View.GONE);
                                } else {
                                    recycle.setVisibility(View.GONE);
                                    nobookingoffer.setVisibility(View.VISIBLE);
                                }
                            }else {
                                Toast.makeText(CommunityBookingActivity.this, newBookingResponseJObj.getString("Message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CommunityBookingActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
          switch (v.getId())
          {
              case R.id.chatIconTeamList:
                  Intent intent = new Intent(CommunityBookingActivity.this, ChatViewBookingScreen.class);
                  startActivity(intent);
                  break;
              case R.id.backFromImg:
                  finish();
                  break;
          }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}


