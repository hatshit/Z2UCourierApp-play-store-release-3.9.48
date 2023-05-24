package com.z2u.booking.vc.dhlgroupingmodel;

import android.os.Parcel;

public class Runs_RouteSectionModel  implements DHL_SectionInterface{

    @Override
    public boolean isSection() {
        return false;
    }

    private final String title;

    public Runs_RouteSectionModel(String title) {
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
        return true;
    }
}
