package com.suggestprice_team.courier_team;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class Address_Model implements Parcelable {

    private String location;
    private String province;
    private String street1;
    private String street2;
    private String suburb;
    private int zipcode;

    public Address_Model (JSONObject jObjOfAddress) {
        try {
            location = jObjOfAddress.getString("location");
            province = jObjOfAddress.getString("province");
            street1 = jObjOfAddress.getString("street1");
            street2 = jObjOfAddress.getString("street2");
            suburb = jObjOfAddress.getString("suburb");
            zipcode = jObjOfAddress.getInt("zipcode");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocation() {
        return location;
    }

    public String getProvince() {
        return province;
    }

    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getSuburb() {
        return suburb;
    }

    public int getZipcode() {
        return zipcode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeString(province);
        dest.writeString(street1);
        dest.writeString(street2);
        dest.writeString(suburb);
        dest.writeInt(zipcode);
    }

    public static final Parcelable.Creator<Address_Model> CREATOR  = new Parcelable.Creator<Address_Model>() {
        public Address_Model createFromParcel(Parcel in) {
            return new Address_Model(in);
        }

        public Address_Model[] newArray(int size) {
            return new Address_Model[size];
        }
    };

    private Address_Model(Parcel in) {
        location = in.readString();
        province = in.readString();
        street1 = in.readString();
        street2 = in.readString();
        suburb = in.readString();
        zipcode = in.readInt();
    }
}
