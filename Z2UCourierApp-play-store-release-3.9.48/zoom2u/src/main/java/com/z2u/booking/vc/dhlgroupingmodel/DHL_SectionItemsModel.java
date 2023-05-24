package com.z2u.booking.vc.dhlgroupingmodel;

import android.os.Parcel;

public class DHL_SectionItemsModel implements DHL_SectionInterface{

    @Override
    public boolean isSection() {
        return true;
    }

    private final String title;

    public DHL_SectionItemsModel(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public boolean isRouteSection() {
        return false;
    }
}
