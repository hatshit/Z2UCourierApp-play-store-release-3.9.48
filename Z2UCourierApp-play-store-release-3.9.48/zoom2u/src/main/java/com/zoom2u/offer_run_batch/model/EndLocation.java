package com.zoom2u.offer_run_batch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EndLocation implements Parcelable {
    @SerializedName("dropLocation")
    @Expose
    private DropLocation dropLocation;
    @SerializedName("gpsx")
    @Expose
    private String gpsx;
    @SerializedName("gpsy")
    @Expose
    private String gpsy;

    public DropLocation getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation() {
        this.dropLocation = dropLocation;
    }

    public String getGpsx() {
        return gpsx;
    }

    public void setGpsx(String gpsx) {
        this.gpsx = gpsx;
    }

    public String getGpsy() {
        return gpsy;
    }

    public void setGpsy(String gpsy) {
        this.gpsy = gpsy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
