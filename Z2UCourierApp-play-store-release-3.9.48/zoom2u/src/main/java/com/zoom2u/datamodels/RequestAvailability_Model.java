package com.zoom2u.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestAvailability_Model implements Parcelable{

	private String PickupSuburb;
	private String DropSuburb;
	private int RequestId;
	private String Notes;

	public RequestAvailability_Model() {
		super();
	}

	public String getPickupSuburb() {
		return PickupSuburb;
	}

	public void setPickupSuburb(String pickupSuburb) {
		PickupSuburb = pickupSuburb;
	}

	public String getDropSuburb() {
		return DropSuburb;
	}

	public void setDropSuburb(String dropSuburb) {
		DropSuburb = dropSuburb;
	}

	public int getRequestId() {
		return RequestId;
	}

	public void setRequestId(int requestId) {
		RequestId = requestId;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(PickupSuburb);
		dest.writeString(DropSuburb);
		dest.writeInt(RequestId);
		dest.writeString(Notes);
	}
	
	 public static final Parcelable.Creator<RequestAvailability_Model> CREATOR  = new Parcelable.Creator<RequestAvailability_Model>() {
		 public RequestAvailability_Model createFromParcel(Parcel in) {
		     return new RequestAvailability_Model(in);
		 }

		 public RequestAvailability_Model[] newArray(int size) {
		     return new RequestAvailability_Model[size];
		 }
	};

	private RequestAvailability_Model(Parcel in) {
		PickupSuburb = in.readString();
		DropSuburb  = in.readString();
		RequestId = in.readInt();
		Notes = in.readString();
	}
	
}
