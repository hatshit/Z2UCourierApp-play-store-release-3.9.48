package com.zoom2u.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

public class Hero_LeaderBoardModel implements Parcelable{

	private String firstName;
	private String lastName;
	private int points;
	private String photo;
	
	public Hero_LeaderBoardModel() {
		super();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeInt(points);
		dest.writeString(photo);
	}
	
	 public static final Parcelable.Creator<Hero_LeaderBoardModel> CREATOR  = new Parcelable.Creator<Hero_LeaderBoardModel>() {
		 public Hero_LeaderBoardModel createFromParcel(Parcel in) {
		     return new Hero_LeaderBoardModel(in);
		 }

		 public Hero_LeaderBoardModel[] newArray(int size) {
		     return new Hero_LeaderBoardModel[size];
		 }
	};

	private Hero_LeaderBoardModel(Parcel in) {
		firstName = in.readString();
		lastName  = in.readString();
		photo = in.readString();
		points = in.readInt();
	}
	
}
