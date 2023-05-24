package com.suggestprice_team.courier_team;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Arun on 17-july-2018.
 */

public class MyTeamList_Model implements Parcelable {

    private String courierId;
    private String name;
    private String mobile;
    private String email;
    private String photo;
    private int activeJobCount;
    private Address_Model addressModel;
    private String firstName;
    private String lastName;
    private String vehicle;

    private boolean setFlagToSelectItem;

    public MyTeamList_Model() {
    }

    public String getCourierId() {
        return courierId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSetFlagToSelectItem() {
        return setFlagToSelectItem;
    }

    public void setSetFlagToSelectItem(boolean setFlagToSelectItem) {
        this.setFlagToSelectItem = setFlagToSelectItem;
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
        dest.writeString(courierId);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeString(email);
        dest.writeString(photo);
        dest.writeInt(activeJobCount);
        dest.writeParcelable(addressModel, 0);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(vehicle);

    }

    public static final Parcelable.Creator<MyTeamList_Model> CREATOR  = new Parcelable.Creator<MyTeamList_Model>() {
        public MyTeamList_Model createFromParcel(Parcel in) {
            return new MyTeamList_Model(in);
        }

        public MyTeamList_Model[] newArray(int size) {
            return new MyTeamList_Model[size];
        }
    };

    private MyTeamList_Model(Parcel in) {
        courierId = in.readString();
        name = in.readString();
        mobile = in.readString();
        email = in.readString();
        photo = in.readString();
        activeJobCount = in.readInt();
        addressModel = in.readParcelable(Address_Model.class.getClassLoader());
        firstName = in.readString();
        lastName = in.readString();
        vehicle = in.readString();

    }

    public int getActiveJobCount() {
        return activeJobCount;
    }

    public void setActiveJobCount(int activeJobCount) {
        this.activeJobCount = activeJobCount;
    }

    public Address_Model getAddressModel() {
        return addressModel;
    }

    public void setAddressModel(JSONObject jOBjOfAddress) {
        this.addressModel = new Address_Model(jOBjOfAddress);
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

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }
}
