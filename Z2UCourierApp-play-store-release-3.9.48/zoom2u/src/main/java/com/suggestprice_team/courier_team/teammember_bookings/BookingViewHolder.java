package com.suggestprice_team.courier_team.teammember_bookings;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.R;

/**
 * Created by Arun on 27-july-2018.
 */

public class BookingViewHolder extends ChildViewHolder {

    private RelativeLayout teamMemberListItemLayout;
    private TextView userNameTABList;
    private ImageView vichleImgTABList;
    private TextView courierPriceTABList;
    private TextView packageTxtTABList;
    private TextView deliverySpeedTABList;
    private TextView statusTxtTABList;
    private TextView pickUpStreetTxtTABList;

    private TextView dropStreetTxtTABList;
    private TextView distanceTxtTABList;
    //private TextView distanceDividerTxtTABList;
    private TextView timerTxtTABList;
    private TextView tv_drop_date_time;

    public BookingViewHolder(View itemView) {
        super(itemView);
        teamMemberListItemLayout = (RelativeLayout) itemView.findViewById(R.id.teamMemberListItemLayout);
        userNameTABList = (TextView) itemView.findViewById(R.id.userNameTABList);
        vichleImgTABList = (ImageView) itemView.findViewById(R.id.vichleImgTABList);
        courierPriceTABList = (TextView) itemView.findViewById(R.id.courierPriceTABList);
        packageTxtTABList = (TextView) itemView.findViewById(R.id.packageTxtTABList);
        deliverySpeedTABList = (TextView) itemView.findViewById(R.id.deliverySpeedTABList);
        statusTxtTABList = (TextView) itemView.findViewById(R.id.statusTxtTABList);
        pickUpStreetTxtTABList = (TextView) itemView.findViewById(R.id.pickUpStreetTxtTABList);
        dropStreetTxtTABList = (TextView) itemView.findViewById(R.id.dropStreetTxtTABList);
        distanceTxtTABList = (TextView) itemView.findViewById(R.id.distanceTxtTABList);
       // distanceDividerTxtTABList = (TextView) itemView.findViewById(R.id.distanceDividerTxtTABList);
        timerTxtTABList = (TextView) itemView.findViewById(R.id.timerTxtTABList);
        tv_drop_date_time = (TextView) itemView.findViewById(R.id.tv_drop_date_time);

    }

    public void bind(final Context context, final All_Bookings_DataModels bookingModel) {
        try {
            if (bookingModel.getSource().equals("DHL"))
                setPickDropContactName(bookingModel.getDrop_ContactName(), "Drop off contact  -  NA");
            else
                setPickDropContactName(bookingModel.getPick_ContactName(), "Pickup contact  -  NA");

            statusTxtTABList.setText(bookingModel.getStatus());
            deliverySpeedTABList.setText(bookingModel.getDeliverySpeed());

            if(bookingModel.getVehicle().equals("Bike"))
                vichleImgTABList.setImageResource(R.drawable.bike);
            else if(bookingModel.getVehicle().equals("Van"))
                vichleImgTABList.setImageResource(R.drawable.van);
            else
                vichleImgTABList.setImageResource(R.drawable.car);

            packageTxtTABList.setText("");
            try {
                if (bookingModel.getShipmentsArray().size() > 0) {
                    for (int i = 0; i < bookingModel.getShipmentsArray().size(); i++) {
                        if (i < 3) {
                            if (i == 0)
                                packageTxtTABList.append(bookingModel.getShipmentsArray().get(i).get("Category") + " (" + bookingModel.getShipmentsArray().get(i).get("Quantity") + ")");
                            else
                                packageTxtTABList.append(", " + bookingModel.getShipmentsArray().get(i).get("Category") + " (" + bookingModel.getShipmentsArray().get(i).get("Quantity") + ")");
                        } else {
                            packageTxtTABList.append("...");
                            break;
                        }
                    }
                } else
                    packageTxtTABList.append("Shipments not available");
            } catch (Exception e) {
                e.printStackTrace();
                packageTxtTABList.append("Shipments not available");
            }

            String priceInt = Functional_Utility.returnCourierPrice(bookingModel.getPrice());
            courierPriceTABList.setText("$" + priceInt);

            setAddressText(pickUpStreetTxtTABList, bookingModel.getPick_StreetNo(), bookingModel.getPick_Address(),
                    bookingModel.getPick_Suburb());
            setAddressText(dropStreetTxtTABList, bookingModel.getDrop_StreetNo(), bookingModel.getDrop_Address(),
                    bookingModel.getDrop_Suburb());

            distanceTxtTABList.setText(bookingModel.getDistance());

            try {
                if (!bookingModel.getDropDateTime().equals("")) {
                    String minInStr = LoginZoomToU.checkInternetwithfunctionality.returnTimeInHourAndMinute(bookingModel.getDropDateTime());
                    timerTxtTABList.setText(minInStr);
                    String dateServer = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServerForCourierRouteForDHL(bookingModel.getDropDateTime());
                    tv_drop_date_time.setText(dateServer);
                } else {
                    timerTxtTABList.setText("NA");
                    tv_drop_date_time.setText("NA");
                }
            } catch (Exception e) {
                timerTxtTABList.setText("NA");
                tv_drop_date_time.setText("NA");
            }


            teamMemberListItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailViewItent = new Intent(context, BookingDetail_TeamMember.class);
                    detailViewItent.putExtra("BookingModel", bookingModel);
                    context.startActivity(detailViewItent);
                    detailViewItent = null;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPickDropContactName(String pickdrop_contactName, String defaultNName) {
        try {
            if (!pickdrop_contactName.equals(""))
                userNameTABList.setText("" + pickdrop_contactName);
            else
                userNameTABList.setText(defaultNName);
        } catch (Exception e) {
            e.printStackTrace();
            userNameTABList.setText(defaultNName);
        }
    }

    private void setAddressText(TextView addressTxtTABList, String pickdrop_streetNo, String pickdrop_streetName, String pickdrop_suburb) {
        String pickUpAddressTxt = "";
        try {
            if (!pickdrop_streetNo.equals(""))
                pickUpAddressTxt = pickdrop_streetNo+", ";
            if (!pickdrop_streetName.equals("")) {
                if (pickdrop_streetName.contains(",")) {
                    String[] pickStreetName = pickdrop_streetName.split(",");
                    pickUpAddressTxt = pickUpAddressTxt + pickStreetName[0] + ", ";
                    pickStreetName = null;
                }else
                    pickUpAddressTxt = pickUpAddressTxt + pickdrop_streetName + ", ";
            }
            if (!pickdrop_suburb.equals(""))
                pickUpAddressTxt = pickUpAddressTxt + pickdrop_suburb;
            else
                pickUpAddressTxt = "-NA-";
        } catch (Exception e) {
            e.printStackTrace();
            pickUpAddressTxt = "-NA-";
        }

        addressTxtTABList.setText(pickdrop_streetName);
    }
}
