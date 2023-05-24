package com.zoom2u.offer_run_batch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geography implements Parcelable {
    @SerializedName("coordinateSystemId")
    @Expose
    private Integer coordinateSystemId;
    @SerializedName("wellKnownText")
    @Expose
    private String wellKnownText;

    public Integer getCoordinateSystemId() {
        return coordinateSystemId;
    }

    public void setCoordinateSystemId(Integer coordinateSystemId) {
        this.coordinateSystemId = coordinateSystemId;
    }

    public String getWellKnownText() {
        return wellKnownText;
    }

    public void setWellKnownText(String wellKnownText) {
        this.wellKnownText = wellKnownText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
