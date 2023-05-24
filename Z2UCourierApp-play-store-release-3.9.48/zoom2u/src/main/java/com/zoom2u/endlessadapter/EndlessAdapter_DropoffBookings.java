package com.zoom2u.endlessadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.z2u.booking.vc.NewBookingView;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.z2u.booking.vc.ActiveBookingView.ViewHolderPatternActiveBooking;
import com.zoom2u.BookingHistory;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.utility.Functional_Utility;

public class EndlessAdapter_DropoffBookings extends ArrayAdapter<DHL_SectionInterface>{
  	
	private Context context;
	int resourceId; 
	ViewHolderPickedUp viewHolderPickedUp;

	private int[] colors;

	public EndlessAdapter_DropoffBookings(Context context, int resourceId) {
		super(context, resourceId);
		this.context = context;
		this.resourceId = resourceId;
		if(BookingView.bookingListArray.size()%2 != 0)
			colors = new int[] {0xF0F0F0F3, 0xFFFFFFFF};
		else
			colors = new int[] {0xFFFFFFFF, 0xF0F0F0F3};
	}

	@Override
	public int getCount() {
		if(BookingView.bookingListArray!=null)
			return BookingView.bookingListArray.size();
		else
			return 0;
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SuspiciousIndentation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		try {
			if(convertView == null)
				viewHolderPickedUp = new ViewHolderPickedUp();
				convertView = LayoutInflater.from(context).inflate(R.layout.completebookinglist_item, parent, false);
			
			viewHolderPickedUp.frontTagPickedUp = (RelativeLayout) convertView.findViewById(R.id.frontCompleteListB);
			viewHolderPickedUp.relativeone = (RelativeLayout) convertView.findViewById(R.id.relativeone);
			viewHolderPickedUp.userNamePickedUp = (TextView) convertView.findViewById(R.id.userNameCompleteListBText);
			viewHolderPickedUp.bookingChrgesPickedUp = (TextView) convertView.findViewById(R.id.chargesTextCompleteListB);
			viewHolderPickedUp.locationDistancePickedUp = (TextView) convertView.findViewById(R.id.distanceCompleteListBText);
			viewHolderPickedUp.bookingTimeCompleteBook = (TextView) convertView.findViewById(R.id.bookingTimeCompleteBook);
			viewHolderPickedUp.line_blank = (View) convertView.findViewById(R.id.line_blank);

			if(position==getCount()-1)
				viewHolderPickedUp.line_blank.setVisibility(View.VISIBLE);
			else
				viewHolderPickedUp.line_blank.setVisibility(View.GONE);



			String bookingCreateTimeNewBookingDetail = null,bookingCreateDateNewBookingDetail = null;
			bookingCreateTimeNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer("" +((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDropDateTime());
			bookingCreateDateNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getDateFromServer("" + ((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDropDateTime());

			viewHolderPickedUp.bookingTimeCompleteBook.setText(""+bookingCreateTimeNewBookingDetail +" | "+bookingCreateDateNewBookingDetail);
			bookingCreateTimeNewBookingDetail = null;
			bookingCreateDateNewBookingDetail = null;

			ImageView timeToArriveInBookingList=  convertView.findViewById(R.id.imglocationMark);
			timeToArriveInBookingList.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						String address=((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Address();
						address = address.replace(' ', '+');
						Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)); // Prepare intent
						context.startActivity(geoIntent);    // Initiate lookup
						geoIntent = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			
			viewHolderPickedUp.orderNumberCBL = ViewHolderPatternActiveBooking.get(convertView, R.id.orderNumberCBL);

			if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getSource().equals("DHL")){
                viewHolderPickedUp.orderNumberCBL.setVisibility(View.VISIBLE);
                try {
                    if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getOrderNumber().equals("")){
                        viewHolderPickedUp.orderNumberCBL.setText("AWB - "+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getOrderNumber());
                    }else
                        viewHolderPickedUp.orderNumberCBL.setText("AWB - NA");
                } catch (Exception e) {
                    e.printStackTrace();
                    viewHolderPickedUp.orderNumberCBL.setText("AWB - NA");
                }
            }else
            	viewHolderPickedUp.orderNumberCBL.setVisibility(View.GONE);
			
			viewHolderPickedUp.packageTypeCompleteBook = (TextView) convertView.findViewById(R.id.packageTypeCompleteBook);
			viewHolderPickedUp.packageTypeCompleteBook.setText("");
			try{
				if (((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().size() > 0) {
					for (int i = 0; i < ((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().size(); i++){
						if (i < 3){
							if (i == 0)
								viewHolderPickedUp.packageTypeCompleteBook.append(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Category")+" ("+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Quantity")+")");
							else
								viewHolderPickedUp.packageTypeCompleteBook.append(", "+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Category")+" ("+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Quantity")+")");
						}else {
							viewHolderPickedUp.packageTypeCompleteBook.append("...");
							break;
						}
					}
				}else
					viewHolderPickedUp.packageTypeCompleteBook.append(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPackage());
			}catch (Exception e){
				e.printStackTrace();
				viewHolderPickedUp.packageTypeCompleteBook.append(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPackage());
			}


			viewHolderPickedUp.deliverySpeedTypeCompleteBook = (TextView) convertView.findViewById(R.id.arrivalTimeCompleteBook);
			viewHolderPickedUp.deliverySpeedTypeCompleteBook.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDeliverySpeed());
			viewHolderPickedUp.packageNotescompleteBook = (ReadMoreTextView) convertView.findViewById(R.id.packageNotescompleteBook);
			viewHolderPickedUp.packageNotescompleteBook.setVisibility(View.VISIBLE);
			try {
				if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getNotes().equals(""))
					viewHolderPickedUp.packageNotescompleteBook.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getNotes());
				else
					viewHolderPickedUp.packageNotescompleteBook.setText("No delivery notes");
			}catch(Exception e){
			   e.printStackTrace();
			   viewHolderPickedUp.packageNotescompleteBook.setText("No delivery notes");
			}

			viewHolderPickedUp.pickUpTextPickedUp = (TextView) convertView.findViewById(R.id.textPickupCompleteListB);
			viewHolderPickedUp.dropOffTextPickedUp = (TextView) convertView.findViewById(R.id.textDropoffCompleteListB);

			try{
				if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_ContactName().equals(""))
					viewHolderPickedUp.userNamePickedUp.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_ContactName());
				else
						viewHolderPickedUp.userNamePickedUp.setText("Pickup Contact  -  NA");
			} catch (Exception e) {
				e.printStackTrace();
				viewHolderPickedUp.userNamePickedUp.setText("Pickup Contact  -  NA");
			}

			if (((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getSource().equals("DHL") ||
					((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_ContactName().equalsIgnoreCase("Telstra"))
				viewHolderPickedUp.userNamePickedUp.setText("" + ((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_ContactName());

			  if (((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getRunId()>0)
				viewHolderPickedUp.userNamePickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_ContactName());

			ImageView vehical= NewBookingView.ViewHolderPattern.get(convertView,R.id.dot_car);
			if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getVehicle().equals("Car"))
				vehical.setImageResource(R.drawable.ic_from_to_car_icon);
			else if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getVehicle().equals("Bike"))
				vehical.setImageResource(R.drawable.ic_bike_normal_new);
			else	if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getVehicle().equals("Van"))
				vehical.setImageResource(R.drawable.ic_van_normal_new);



			String priceInt = Functional_Utility.returnCourierPrice((Double)((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPrice());
			viewHolderPickedUp.bookingChrgesPickedUp.setText("$"+priceInt);
			priceInt = "";
			
			viewHolderPickedUp.locationDistancePickedUp.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistance());
			//viewHolderPickedUp.locationMarkerTextPickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistanceFromCurrentLocation()+" km away");
			
			/*int colorPos = position % colors.length;
			viewHolderPickedUp.frontTagPickedUp.setBackgroundColor(colors[colorPos]);*/
			
			//viewHolderPickedUp.frontTagPickedUp.setClickable(false);

			viewHolderPickedUp.relativeone.setOnClickListener(view -> {
				Intent callPickedUpDetail = new Intent(context, BookingHistory.class);
				callPickedUpDetail.putExtra("positionCompleteBooking", position);
				context.startActivity(callPickedUpDetail);
				callPickedUpDetail = null;

			});



			

			// ************* For Temando bookings test  ************

			if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getSource().equals("Temando")){

				   if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Address().equals(""))
					   viewHolderPickedUp.pickUpTextPickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Address());
					else
						viewHolderPickedUp.pickUpTextPickedUp.setText("No Address");
				   
					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Address().equals(""))
					  viewHolderPickedUp.dropOffTextPickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Address());
				  else
					  viewHolderPickedUp.dropOffTextPickedUp.setText("No Address");
				   

				  if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistance().equals(""))
					  viewHolderPickedUp.locationDistancePickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistance());

				  
			}else{

				   
				if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Suburb().equals(""))
					   viewHolderPickedUp.pickUpTextPickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Suburb());
					else
						viewHolderPickedUp.pickUpTextPickedUp.setText("No suburb");
				   

					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Suburb().equals(""))
					   viewHolderPickedUp.dropOffTextPickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Suburb());
					else
						viewHolderPickedUp.dropOffTextPickedUp.setText("No suburb");
				   

					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistance().equals(""))
					   viewHolderPickedUp.locationDistancePickedUp.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistance());
				   else
					   viewHolderPickedUp.locationDistancePickedUp.setText("0 km");
				   
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}
}

class ViewHolderPickedUp{
  TextView orderNumberCBL;
  TextView userNamePickedUp;
  TextView bookingChrgesPickedUp;
  TextView locationDistancePickedUp;
  TextView pickUpTextPickedUp;
  TextView dropOffTextPickedUp;
  TextView locationMarkerTextPickedUp;
  TextView bookingTimeCompleteBook;
  TextView packageTypeCompleteBook;
  TextView deliverySpeedTypeCompleteBook;
  ReadMoreTextView packageNotescompleteBook;
  RelativeLayout frontTagPickedUp;
  RelativeLayout relativeone;
  View line_blank;
}