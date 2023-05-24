package com.suggestprice_team.courier_team.teammember_bookings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.ChatViewBookingScreen;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.utility.DividerItemDecoration;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Arun on 27-July-2018.
 */

public class List_CourierTeamMembersBooking extends Activity  implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ProgressDialog progressDialogTeamBookingList;
    TextView countChatTxtTeamMemberBookingsPage;
    SwipeRefreshLayout swipeRefreshTeamMemberBookingsPage;
    RecyclerView listTeamMemberBookingsPage;
    TextView bookingNotAvailTxtTeamMemberBookingsPage;

    public List<TeamCategeory> teamMemberBookingArray;
    TeamCategeoryAdapter adapterTeamCategeory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teammembers_list);


        RelativeLayout headerLayoutAllTitleBar = findViewById(R.id.headerTeamMemberBookingsView);

        Window window = List_CourierTeamMembersBooking.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if(MainActivity.isIsBackGroundGray()){
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        }else{
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }

        findViewById(R.id.backBtnTeamMemberBookingsPage);
        findViewById(R.id.backBtnTeamMemberBookingsPage).setOnClickListener(this);
        findViewById(R.id.titleTxtTeamMemberBookingsPage);
        ((TextView) findViewById(R.id.titleTxtTeamMemberBookingsPage)).setText("My Team Bookings");
        findViewById(R.id.chatBtnTeamMemberBookingsPage).setOnClickListener(this);
        countChatTxtTeamMemberBookingsPage = (TextView)  findViewById(R.id.countChatTxtTeamMemberBookingsPage);
        countChatTxtTeamMemberBookingsPage.setVisibility(View.GONE);
        bookingNotAvailTxtTeamMemberBookingsPage = (TextView)  findViewById(R.id.bookingNotAvailTxtTeamMemberBookingsPage);
        bookingNotAvailTxtTeamMemberBookingsPage.setVisibility(View.GONE);
        swipeRefreshTeamMemberBookingsPage = (SwipeRefreshLayout)  findViewById(R.id.swipeRefreshTeamMemberBookingsPage);
        swipeRefreshTeamMemberBookingsPage.setOnRefreshListener(this);
        swipeRefreshTeamMemberBookingsPage.setColorSchemeColors(Color.parseColor("#215400"), Color.parseColor("#4f5151"), Color.parseColor("#a1c93a"));

        listTeamMemberBookingsPage = (RecyclerView)  findViewById(R.id.listTeamMemberBookingsPage);
        listTeamMemberBookingsPage.setNestedScrollingEnabled(false);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listTeamMemberBookingsPage.setLayoutManager(mLayoutManager);
        listTeamMemberBookingsPage.setItemAnimator(new DefaultItemAnimator());
        listTeamMemberBookingsPage.addItemDecoration(new DividerItemDecoration(List_CourierTeamMembersBooking.this, null));

        listTeamMemberBookingsPage.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                swipeRefreshTeamMemberBookingsPage.setEnabled(mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });

        inItTeamMemberListArray();

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            apiCallToMyTeamList();
        else
            DialogActivity.alertDialogView(List_CourierTeamMembersBooking.this, "No Network!", "No Network connection, Please try again later.");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtnTeamMemberBookingsPage:
                finish();
                break;
            case R.id.chatBtnTeamMemberBookingsPage:
                Intent chatViewItent = new Intent(List_CourierTeamMembersBooking.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
        }
    }

    //*********** Init Team member array ************
    public void inItTeamMemberListArray() {
        if (teamMemberBookingArray != null) {
            if (teamMemberBookingArray.size() > 0)
                teamMemberBookingArray.clear();
        } else
            teamMemberBookingArray = new ArrayList<TeamCategeory>();
    }

    //*********** Refresh Team member list data **********
    void refreshTeamMemberList (){
        inItTeamMemberListArray();
        swipeRefreshTeamMemberBookingsPage.setRefreshing(false);
       GetTeamMembersBooking();
    }

    @Override
    public void onRefresh() {
        refreshTeamMemberList ();
    }

    //*************** API call to Invite team member *************
    void apiCallToMyTeamList () {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetTeamMembersBooking();
        else
            DialogActivity.alertDialogView(List_CourierTeamMembersBooking.this, "No Network!", "No Network connection, Please try again later.");
    }

    private void GetTeamMembersBooking(){
        final String[] responseMsgStr = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if(progressDialogTeamBookingList == null)
                    progressDialogTeamBookingList = new ProgressDialog(List_CourierTeamMembersBooking.this);
                Custom_ProgressDialogBar.inItProgressBar(progressDialogTeamBookingList);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseMsgStr[0] = webServiceHandler.getBookingList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                if(progressDialogTeamBookingList != null)
                    if(progressDialogTeamBookingList.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressDialogTeamBookingList);
                swipeRefreshTeamMemberBookingsPage.setRefreshing(false);
                switch (LoginZoomToU.isLoginSuccess) {
                    case 0:
                        if (!responseMsgStr.equals("")) {
                            try {
                                JSONObject jObjOfMemberBookingList = new JSONObject(responseMsgStr[0]);
                                if (jObjOfMemberBookingList.getBoolean("success")) {
                                    JSONArray jArrayMemberBookings = jObjOfMemberBookingList.getJSONArray("bookings");
                                    for (int i = 0; i < jArrayMemberBookings.length(); i++) {
                                        JSONArray jObjOfMemberBookingsItem = jArrayMemberBookings.getJSONArray(i);
                                        if (jObjOfMemberBookingsItem.length() > 0) {
                                            TeamCategeory teamCategeory = new TeamCategeory();
                                            List<All_Bookings_DataModels> bookingModelArray = new ArrayList<All_Bookings_DataModels>();
                                            String courierName = "";
                                            for (int j = 0; j < jObjOfMemberBookingsItem.length(); j++) {
                                                JSONObject jObjOfBooking = jObjOfMemberBookingsItem.getJSONObject(j);
                                                All_Bookings_DataModels bookingsDataModels = new All_Bookings_DataModels();
                                                if (j == 0)
                                                    courierName = jObjOfBooking.getString("courierName");

                                                bookingsDataModels.setCourierId(jObjOfBooking.getString("courierId"));
                                                bookingsDataModels.setCourierName(jObjOfBooking.getString("courierName"));
                                                bookingsDataModels.setCourierMobile(jObjOfBooking.getString("courierMobile"));
                                                bookingsDataModels.setBookingId(jObjOfBooking.getInt("bookingId"));
                                                bookingsDataModels.setCustomerName(jObjOfBooking.getString("customerName"));
                                                bookingsDataModels.setCustomerCompany(jObjOfBooking.getString("customerCompany"));
                                                bookingsDataModels.setCustomerContact(jObjOfBooking.getString("customerContact"));
                                                bookingsDataModels.setBookingRefNo(jObjOfBooking.getString("bookingRefNo"));
                                                bookingsDataModels.setPickupDateTime(jObjOfBooking.getString("pickupDateTime"));
                                                bookingsDataModels.setPick_Address(jObjOfBooking.getString("pickupAddress"));
                                                bookingsDataModels.setPick_ContactName(jObjOfBooking.getString("pickupContactName"));
                                                bookingsDataModels.setPick_GPSX(jObjOfBooking.getString("pickupGPSX"));
                                                bookingsDataModels.setPick_GPSY(jObjOfBooking.getString("pickupGPSY"));
                                                bookingsDataModels.setPick_Notes(jObjOfBooking.getString("pickupNotes"));
                                                bookingsDataModels.setPick_Phone(jObjOfBooking.getString("pickupPhone"));
                                                bookingsDataModels.setPick_Suburb(jObjOfBooking.getString("pickupSuburb"));
                                                bookingsDataModels.setPick_StreetNo(jObjOfBooking.getString("pickupStreetNumber"));
                                                bookingsDataModels.setPick_StreetName(jObjOfBooking.getString("pickupStreet"));
                                                bookingsDataModels.setDropDateTime(jObjOfBooking.getString("dropDateTime"));
                                                bookingsDataModels.setDrop_Address(jObjOfBooking.getString("dropAddress"));
                                                bookingsDataModels.setDrop_ContactName(jObjOfBooking.getString("dropContactName"));
                                                bookingsDataModels.setDrop_GPSX(jObjOfBooking.getString("dropGPSX"));
                                                bookingsDataModels.setDrop_GPSY(jObjOfBooking.getString("dropGPSY"));
                                                bookingsDataModels.setDrop_Notes(jObjOfBooking.getString("dropNotes"));
                                                bookingsDataModels.setDrop_Phone(jObjOfBooking.getString("dropPhone"));
                                                bookingsDataModels.setDrop_Suburb(jObjOfBooking.getString("dropSuburb"));
                                                bookingsDataModels.setDrop_StreetNo(jObjOfBooking.getString("dropStreetNumber"));
                                                bookingsDataModels.setDrop_StreetName(jObjOfBooking.getString("dropStreet"));
                                                bookingsDataModels.setCreatedDateTime(jObjOfBooking.getString("createdDateTime"));
                                                bookingsDataModels.setDeliverySpeed(jObjOfBooking.getString("deliverySpeed"));
                                                bookingsDataModels.setDistance(jObjOfBooking.getString("distance"));
                                                bookingsDataModels.setNotes(jObjOfBooking.getString("notes"));
                                                bookingsDataModels.setPrice(jObjOfBooking.getDouble("courierPrice"));
                                                bookingsDataModels.setPickupETA(jObjOfBooking.getString("pickupETA"));
                                                bookingsDataModels.setDropETA(jObjOfBooking.getString("dropETA"));
                                                bookingsDataModels.setVehicle(jObjOfBooking.getString("vehicle"));
                                                bookingsDataModels.setStatus(jObjOfBooking.getString("status"));
                                                bookingsDataModels.setSource(jObjOfBooking.getString("source"));

                                                try {
                                                    bookingsDataModels.setRoutePolyline(jObjOfBooking.getString("routePolyline"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    bookingsDataModels.setOrderNumber(jObjOfBooking.getString("orderNumber"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    bookingsDataModels.setOrderNumber("");
                                                }

                                                ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
                                                for (int k = 0; k < jObjOfBooking.getJSONArray("shipments").length(); k++) {
                                                    HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
                                                    objOFShipments.put("Category", jObjOfBooking.getJSONArray("shipments").getJSONObject(k).getString("category"));
                                                    objOFShipments.put("Quantity", jObjOfBooking.getJSONArray("shipments").getJSONObject(k).getInt("quantity"));
                                                    arrayOfShipments.add(objOFShipments);
                                                    objOFShipments = null;
                                                }
                                                bookingsDataModels.setShipmentsArray(arrayOfShipments);
                                                arrayOfShipments = null;
                                                bookingModelArray.add(bookingsDataModels);
                                            }
                                            teamCategeory.setCourierName(courierName);

                                            // Convert each UTC date string to Instant
                                            List<Instant> instants = bookingModelArray.stream()
                                                    .map(obj -> Instant.parse(obj.getDropDateTime()))
                                                    .collect(Collectors.toList());

                                            // Create a comparator that compares objects by their UTC date property
                                            Comparator<All_Bookings_DataModels> myObjectComparator = Comparator.comparing(obj -> Instant.parse(obj.getDropDateTime()));

                                            // Sort the list of objects
                                            Collections.sort(bookingModelArray, myObjectComparator);

                                            teamCategeory.setTeamMemberBookingModel(bookingModelArray);
                                            teamMemberBookingArray.add(teamCategeory);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                DialogActivity.alertDialogView(List_CourierTeamMembersBooking.this, "Sorry!", "Something went wrong, Please try again later.");
                            }

                            try {
                                if (teamMemberBookingArray.size() > 0) {
                                    adapterTeamCategeory = new TeamCategeoryAdapter(List_CourierTeamMembersBooking.this, teamMemberBookingArray);
                                    adapterTeamCategeory.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                                        @Override
                                        public void onListItemExpanded(int position) {
                                            //TeamCategeory expandedTeamMemberBookingCategory = teamMemberBookingArray.get(position);
                                        }

                                        @Override
                                        public void onListItemCollapsed(int position) {
                                            //TeamCategeory expandedTeamMemberBookingCategory = teamMemberBookingArray.get(position);
                                        }
                                    });
                                    listTeamMemberBookingsPage.setAdapter(adapterTeamCategeory);
                                    listTeamMemberBookingsPage.setVisibility(View.VISIBLE);
                                    bookingNotAvailTxtTeamMemberBookingsPage.setVisibility(View.GONE);
                                } else {
                                    listTeamMemberBookingsPage.setVisibility(View.GONE);
                                    bookingNotAvailTxtTeamMemberBookingsPage.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                DialogActivity.alertDialogView(List_CourierTeamMembersBooking.this, "Sorry!", "Something went wrong, Please try again later.");
                            }
                        }
                        break;
                    case 1:
                        listTeamMemberBookingsPage.setVisibility(View.GONE);
                        bookingNotAvailTxtTeamMemberBookingsPage.setVisibility(View.VISIBLE);
                        DialogActivity.alertDialogView(List_CourierTeamMembersBooking.this, "No Network!", "No network connection, Please try again later.");
                        break;
                    case 2:case 3:
                        listTeamMemberBookingsPage.setVisibility(View.GONE);
                        bookingNotAvailTxtTeamMemberBookingsPage.setVisibility(View.VISIBLE);
                        Functional_Utility.validationErrorMsg(List_CourierTeamMembersBooking.this, responseMsgStr[0]);
                        break;
                }
            }
        }.execute();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        adapterTeamCategeory.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adapterTeamCategeory.onRestoreInstanceState(savedInstanceState);
    }
}
