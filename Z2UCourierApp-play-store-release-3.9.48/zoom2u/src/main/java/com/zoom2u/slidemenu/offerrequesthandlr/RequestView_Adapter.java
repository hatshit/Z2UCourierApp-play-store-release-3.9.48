package com.zoom2u.slidemenu.offerrequesthandlr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.z2u.booking.vc.NewBookingView;
import com.z2u.chatview.ChatView_BidConfirmation;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.BidImageDialog;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.R;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestView_Adapter extends BaseAdapter {

    private Context context;
    private RequestView baseClassRef;
    List<RequestView_DetailPojo> requestPojoList;
    ProgressDialog progressForDeliveryHistory;
    RequestView_DetailPojo requestView_detailPojo;
    BidDetailsModel bidDetailsModel;
    int images_array_length;
    public RequestView_Adapter(Context context, RequestView baseClass, List<RequestView_DetailPojo> requestPojoList) {
        this.context=context;
        baseClassRef = baseClass;
        this.requestPojoList = requestPojoList;
    }

    @Override
    public int getCount() {
        return requestPojoList.size();
    }

    @Override
    public Object getItem(int position) {
        return requestPojoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.quote_list_item, null);

        RelativeLayout quotesListMainView = (RelativeLayout) convertView.findViewById(R.id.quotesListMainView);

        ImageView placeBidItemImgList  = (ImageView) convertView.findViewById(R.id.placeBidItemImgList);
        ImageView dot_car  = (ImageView) convertView.findViewById(R.id.dot_car);
        TextView bid_imageTypeTxt = (TextView) convertView.findViewById(R.id.bid_imageTypeTxt);
        ImageView primaryImageView =  convertView.findViewById(R.id.primaryImageView);
        final TextView placeBidItemTxtList = (TextView) convertView.findViewById(R.id.placeBidItemTxtList);
        TextView pickAddressPlaceBidList = (TextView) convertView.findViewById(R.id.pickAddress);
        TextView dropAddressPlaceBidList = (TextView) convertView.findViewById(R.id.dropAddress);
        TextView distanceTxtPlaceBidList = (TextView) convertView.findViewById(R.id.distanceTxtPlaceBidList);
        TextView bidActiveForTxt = (TextView) convertView.findViewById(R.id.bidActiveForTxtInList);
        TextView pickTimePlaceBidList = (TextView) convertView.findViewById(R.id.pickTimePlaceBidList);
        TextView dropTimePlaceBidList = (TextView) convertView.findViewById(R.id.dropTimePlaceBidList);
        TextView numberOfBidTxtPlaceBidList = (TextView) convertView.findViewById(R.id.numberOfBidTxtPlaceBidList);
        ReadMoreTextView noteTxtPlaceBidList = (ReadMoreTextView) convertView.findViewById(R.id.noteTxtPlaceBidList);
        Button btnNotifyUnreadCount = (Button) convertView.findViewById(R.id.btnNotifyUnreadCount);
        btnNotifyUnreadCount.setVisibility(View.GONE);
        Button placeBidBtnList = (Button) convertView.findViewById(R.id.placeBidBtnList);
        Button chatBtnPlaceBidList = (Button) convertView.findViewById(R.id.chatBtnPlaceBidList);
        Button cancelBtnPlaceBidList = (Button) convertView.findViewById(R.id.cancelBtnPlaceBidList);
        final RequestView_DetailPojo requestView_pojo = requestPojoList.get(position);

        if (requestView_pojo.getNotes() != null) {
            if (!requestView_pojo.getNotes().equals("")) {
                noteTxtPlaceBidList.setVisibility(View.VISIBLE);
                noteTxtPlaceBidList.setText("Note : "+requestView_pojo.getNotes());
            }else
                noteTxtPlaceBidList.setVisibility(View.GONE);
        }else
            noteTxtPlaceBidList.setVisibility(View.GONE);

        pickAddressPlaceBidList.setText(requestView_pojo.getPickupSuburb());
        dropAddressPlaceBidList.setText(requestView_pojo.getDropSuburb());
        pickTimePlaceBidList.setText(requestView_pojo.getPickUpDateTime());
        dropTimePlaceBidList.setText(requestView_pojo.getDropDateTime());
        distanceTxtPlaceBidList.setText(requestView_pojo.getDistance());
        numberOfBidTxtPlaceBidList.setText(requestView_pojo.getTotalBids()+" bids");

        primaryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetMyRequestOfCustomerDetail(requestView_pojo.getId());
            }
        });

        try{
            if(requestView_pojo.getVehicle().equals("Car"))
                dot_car.setImageResource(R.drawable.ic_from_to_car_icon);
            else if(requestView_pojo.getVehicle().equals("Bike"))
                dot_car.setImageResource(R.drawable.ic_bike_normal_new);
            else if(requestView_pojo.getVehicle().equals("Van"))
                dot_car.setImageResource(R.drawable.ic_van_normal_new);

        }catch (Exception e){

        }


        try {
            if (requestView_pojo.getPrimaryPackageImage() != null && !requestView_pojo.getPrimaryPackageImage().isEmpty() && !requestView_pojo.getPrimaryPackageImage().equals("null")) {
                primaryImageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(requestView_pojo.getPrimaryPackageImage()).placeholder(R.drawable.bid_placeholder).into(primaryImageView);
                placeBidItemImgList.setImageResource(R.drawable.bid_largeitems);
                bid_imageTypeTxt.setText("General Truck Shipments");
            } else {
                primaryImageView.setVisibility(View.GONE);
                setShipmentImgOrText(requestView_pojo.getShipmentsArray(), placeBidItemImgList, bid_imageTypeTxt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setShipmentImgOrText(requestView_pojo.getShipmentsArray(), placeBidItemImgList, bid_imageTypeTxt);
        }

        placeBidItemTxtList.setText("");
        if (requestView_pojo.getShipmentsArray().size() > 0) {
            for (int i = 0; i < requestView_pojo.getShipmentsArray().size(); i++) {
                if (i == 0)
                    placeBidItemTxtList.append("" + requestView_pojo.getShipmentsArray().get(i).get("Category")+" ("+requestView_pojo.getShipmentsArray().get(i).get("Quantity")+")");
                else
                    placeBidItemTxtList.append(", " + requestView_pojo.getShipmentsArray().get(i).get("Category")+" ("+requestView_pojo.getShipmentsArray().get(i).get("Quantity")+")");
            }
        }

        if (requestView_pojo.getOfferPrice().equals("null") || requestView_pojo.getOfferPrice().equals("")) {
            placeBidBtnList.setText("Place Bid");
            placeBidBtnList.setClickable(true);
            placeBidBtnList.setEnabled(true);
            placeBidBtnList.setAlpha(1.0f);
            chatBtnPlaceBidList.setVisibility(View.VISIBLE);
            chatBtnPlaceBidList.setText("Reject Bid");
            chatBtnPlaceBidList.setTextColor(context.getResources().getColor(R.color.gold_dark));
            chatBtnPlaceBidList.setBackgroundResource(R.drawable.new_boder);
            btnNotifyUnreadCount.setVisibility(View.GONE);
            bidActiveForTxt.setVisibility(View.VISIBLE);
            cancelBtnPlaceBidList.setVisibility(View.GONE);
            setBidActivePeriodTxt(bidActiveForTxt, requestView_pojo, placeBidBtnList);
        } else {
            chatBtnPlaceBidList.setVisibility(View.VISIBLE);
            if (requestView_pojo.isCancel()) {
                bidActiveForTxt.setVisibility(View.VISIBLE);
                setBidActivePeriodTxt(bidActiveForTxt, requestView_pojo, placeBidBtnList);
                placeBidBtnList.setText("Place Bid");
                placeBidBtnList.setClickable(true);
                placeBidBtnList.setEnabled(true);
                placeBidBtnList.setAlpha(1.0f);
                chatBtnPlaceBidList.setText("Reject Bid");
                chatBtnPlaceBidList.setTextColor(context.getResources().getColor(R.color.gold_dark));
                chatBtnPlaceBidList.setBackgroundResource(R.drawable.new_boder);
                cancelBtnPlaceBidList.setVisibility(View.VISIBLE);
                cancelBtnPlaceBidList.setClickable(false);
                cancelBtnPlaceBidList.setEnabled(false);
                cancelBtnPlaceBidList.setAlpha(0.5f);
                cancelBtnPlaceBidList.setText("Cancelled");
            } else {
                bidActiveForTxt.setVisibility(View.GONE);
                placeBidBtnList.setText("Bid placed");
                placeBidBtnList.setClickable(false);
                placeBidBtnList.setEnabled(false);
                placeBidBtnList.setAlpha(0.5f);
                chatBtnPlaceBidList.setBackgroundResource(R.drawable.green_bg_withborder);
                chatBtnPlaceBidList.setTextColor(context.getResources().getColor(R.color.white));
                chatBtnPlaceBidList.setText("Chat Customer");
                cancelBtnPlaceBidList.setVisibility(View.VISIBLE);
                cancelBtnPlaceBidList.setClickable(true);
                cancelBtnPlaceBidList.setEnabled(true);
                cancelBtnPlaceBidList.setAlpha(1.0f);
                cancelBtnPlaceBidList.setText("Cancel Bid");
                if (requestView_pojo.getBidRequest_chatModel().getUnreadBidChatCountForCustomer() > 0) {
                    btnNotifyUnreadCount.setVisibility(View.VISIBLE);
                    btnNotifyUnreadCount.setText(""+requestView_pojo.getBidRequest_chatModel().getUnreadBidChatCountForCustomer());
                } else
                    btnNotifyUnreadCount.setVisibility(View.GONE);
            }
        }

        quotesListMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHistoryDetail = new Intent(context, Bid_RequestView_Detail.class);
                intentHistoryDetail.putExtra("RequestIdForDetail", requestView_pojo.getId());
                intentHistoryDetail.putExtra("IsRequestIdIsCancel", requestView_pojo.isCancel());
                if (requestView_pojo.getOfferPrice().equals("null") || requestView_pojo.getOfferPrice().equals(""))
                    intentHistoryDetail.putExtra("UnReadChatCount", 0);
                else
                    intentHistoryDetail.putExtra("UnReadChatCount", (int) requestView_pojo.getBidRequest_chatModel().getUnreadBidChatCountForCustomer());
                ((Activity)context).startActivityForResult(intentHistoryDetail, 678);
                intentHistoryDetail = null;
            }
        });

        placeBidBtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent placeBidView = new Intent(context, Alert_To_PlaceBid.class);
                placeBidView.putExtra("isFromBidDetail", 1);
                placeBidView.putExtra("requestView_detailPojo", requestView_pojo);
                placeBidView.putExtra("shipmentItems", placeBidItemTxtList.getText().toString());
                context.startActivity(placeBidView);
                placeBidView = null;
            }
        });

        chatBtnPlaceBidList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestView_pojo.getOfferPrice().equals("null") || requestView_pojo.getOfferPrice().equals(""))
                    baseClassRef.cancelBidRequestConfirmationDialog(requestView_pojo.getOfferId(), true);
                else if (requestView_pojo.isCancel()) {
                    baseClassRef.cancelBidRequestConfirmationDialog(requestView_pojo.getOfferId(), true);
                } else {
                    Intent bidChatWithCustomer = new Intent(context, ChatView_BidConfirmation.class);
                    bidChatWithCustomer.putExtra("BidIDForChat", requestView_pojo.getId());
                    bidChatWithCustomer.putExtra("CustomerNameForChat", requestView_pojo.getCustomer());
                    bidChatWithCustomer.putExtra("CustomerIDForChat", requestView_pojo.getCustomerID());
                    context.startActivity(bidChatWithCustomer);
                    bidChatWithCustomer = null;
                }
            }
        });

        cancelBtnPlaceBidList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseClassRef.cancelBidRequestConfirmationDialog(requestView_pojo.getOfferId(), false);
            }
        });

        return convertView;
    }

    private void setShipmentImgOrText(ArrayList<HashMap<String, Object>> shipmentArray, ImageView placeBidItemImgList, TextView bid_imageTypeTxt) {
        bid_imageTypeTxt.setVisibility(View.VISIBLE);
        if (shipmentArray.size() > 0)
            setPackageImgNText((String) shipmentArray.get(shipmentArray.size()-1).get("Category"), placeBidItemImgList, bid_imageTypeTxt);
        else {
            placeBidItemImgList.setImageResource(R.drawable.bid_largeitems);
            bid_imageTypeTxt.setText("General Truck Shipments");
        }
    }

    private void setPackageImgNText(String categeory, ImageView placeBidItemImgList, TextView bid_imageTypeTxt) {
        switch (categeory) {
            case "Documents":
                placeBidItemImgList.setImageResource(R.drawable.bid_documents);
                bid_imageTypeTxt.setText("Documents");
                break;
            case "Bag":
                placeBidItemImgList.setImageResource(R.drawable.bid_satchelandlaptops);
                bid_imageTypeTxt.setText("Satchel, laptops");
                break;
            case "Box":
                placeBidItemImgList.setImageResource(R.drawable.bid_smallbox);
                bid_imageTypeTxt.setText("Small box");
                break;
            case "Flowers":
                placeBidItemImgList.setImageResource(R.drawable.bid_cakesflowersdelicates);
                bid_imageTypeTxt.setText("Cakes, flowers & delicates");
                break;
            case "Large":
                placeBidItemImgList.setImageResource(R.drawable.bid_largebox);
                bid_imageTypeTxt.setText("Large box");
                break;
            default:
                placeBidItemImgList.setImageResource(R.drawable.bid_largeitems);
                bid_imageTypeTxt.setText("General Truck Shipments");
                break;
        }
    }

    private void setBidActivePeriodTxt(TextView bidActiveForTxt, RequestView_DetailPojo requestView_pojo, Button placeBidBtnList) {
        try {
            if (!requestView_pojo.getDropDateTime().equals("")) {
                String utcDateTime = LoginZoomToU.checkInternetwithfunctionality.returnDateToShowBidActiveFor(requestView_pojo.getDropDateTime());
                String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiff(utcDateTime, true);
                if (minInStr.equals("Expired")) {
                    placeBidBtnList.setClickable(false);
                    placeBidBtnList.setEnabled(false);
                    placeBidBtnList.setAlpha(0.5f);
                    bidActiveForTxt.setText(minInStr);
                } else if (!minInStr.equals(""))
                    bidActiveForTxt.setText("Bid Active For - " + minInStr);
                else
                    bidActiveForTxt.setText("");
            } else {
                bidActiveForTxt.setText("");
            }
        } catch (Exception e) {
            bidActiveForTxt.setText("");
        }
    }


    public void addItems(List<RequestView_DetailPojo> delHisList) {
        if (null == delHisList || delHisList.size() <= 0)
            return ;

        if (null == requestPojoList)
            requestPojoList = new ArrayList<RequestView_DetailPojo>();

        requestPojoList.addAll(delHisList);
        notifyDataSetChanged();
    }

    private void GetMyRequestOfCustomerDetail(int reqId){
        final String[] webserviceMyRequestList = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressForDeliveryHistory == null)
                        progressForDeliveryHistory = new ProgressDialog(context);
                    Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                WebserviceHandler handler = new WebserviceHandler();
                webserviceMyRequestList[0] = handler.getQuoteRequestOfCustomerDetails(reqId);
            }

            @Override
            public void onPostExecute() {
                try {
                    JSONObject jsonObjectOflIstDelivery = new JSONObject(webserviceMyRequestList[0]);
                    JSONArray jsonArrayOfListDelivery = jsonObjectOflIstDelivery.getJSONArray("data");
                    JSONObject objDeliveryDetail = jsonArrayOfListDelivery.getJSONObject(0);
                    requestView_detailPojo = new RequestView_DetailPojo();

                    Gson gson = new Gson();
                    bidDetailsModel = gson.fromJson(String.valueOf(objDeliveryDetail), BidDetailsModel.class);


                    JSONArray images_array = objDeliveryDetail.getJSONArray("PackageImages");
                    images_array_length = images_array.length();
                    String total_images = "";
                    if (images_array_length > 0) {
                        for (int j = 0; j < images_array_length; j++) {
                            if (total_images != "")
                                total_images = total_images + "," + images_array.getString(j);
                            else
                                total_images = images_array.getString(j);
                        }
                    }
                    requestView_detailPojo.setPackageImages(total_images);


                    if (requestView_detailPojo.getPackageImages() != "") {
                        BidImageDialog.alertDialogToFinishView(requestView_detailPojo.getPackageImages(),context,images_array_length);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(context, "Error!", "Something went wrong, Please try again.");
                }
                Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
            }
        }.execute();

    }

}

