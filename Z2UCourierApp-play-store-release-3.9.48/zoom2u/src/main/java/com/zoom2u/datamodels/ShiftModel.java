package com.zoom2u.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;

public class ShiftModel  implements Parcelable, DHL_SectionInterface {

	int shiftID;
	String titleShiftItem;
	String startDateTime;
	String endDateTime;
	int payPerDelivery;
	String status;
	String area;
	String city;
	String state;
	
	public ShiftModel() {
		super();
	}

	public int getShiftID() {
		return shiftID;
	}

	public void setShiftID(int shiftID) {
		this.shiftID = shiftID;
	}

	public String getTitleShiftItem() {
		return titleShiftItem;
	}

	public void setTitleShiftItem(String titleShiftItem) {
		this.titleShiftItem = titleShiftItem;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	public int getPayPerDelivery() {
		return payPerDelivery;
	}

	public void setPayPerDelivery(int payPerDelivery) {
		this.payPerDelivery = payPerDelivery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(shiftID);
		dest.writeString(titleShiftItem);
		dest.writeString(startDateTime);
		dest.writeString(endDateTime);
		dest.writeInt(payPerDelivery);
		dest.writeString(status);
		dest.writeString(area);
		dest.writeString(city);
		dest.writeString(state);
	}
	
	 public static final Parcelable.Creator<ShiftModel> CREATOR  = new Parcelable.Creator<ShiftModel>() {
		 public ShiftModel createFromParcel(Parcel in) {
		     return new ShiftModel(in);
		 }

		 public ShiftModel[] newArray(int size) {
		     return new ShiftModel[size];
		 }
	};

	private ShiftModel(Parcel in) {
		shiftID = in.readInt();
		titleShiftItem = in.readString();
		startDateTime  = in.readString();
		endDateTime = in.readString();
		payPerDelivery = in.readInt();
		status = in.readString();
		area = in.readString();
		city = in.readString();
		state = in.readString();
	}

	@Override
	public boolean isSection() {
		return false;
	}

	@Override
	public boolean isRouteSection() {
		return false;
	}
}
