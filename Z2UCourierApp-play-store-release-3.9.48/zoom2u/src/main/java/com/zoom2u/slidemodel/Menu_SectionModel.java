package com.zoom2u.slidemodel;

import android.os.Parcel;

public class Menu_SectionModel implements MenuSection_Interface {

    @Override
    public boolean isSection() {
        return true;
    }

    private final String title;

    public Menu_SectionModel(String title) {
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

}
