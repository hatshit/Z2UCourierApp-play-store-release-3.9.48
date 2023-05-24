package com.zoom2u.slidemenu.accountdetail_section;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Arun on 24-Nov-2017.
 */

public class RecentBookingItems_SR_POJO {

    private String recentBookingDate;
    private String recentBookingRating;
    private String recentBookingPickUpSuburb;
    private String recentBookingDropoffSuburb;

    public RecentBookingItems_SR_POJO (JSONObject jObjOfRecentBookingItems) {
        try {
            this.recentBookingDate = jObjOfRecentBookingItems.getString("Date");
            this.recentBookingRating = jObjOfRecentBookingItems.getString("Rating");
            this.recentBookingPickUpSuburb = jObjOfRecentBookingItems.getString("PickupSuburb");
            this.recentBookingDropoffSuburb = jObjOfRecentBookingItems.getString("DropSuburb");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getRecentBookingDate() {
        return recentBookingDate;
    }

    public String getRecentBookingRating() {
        return recentBookingRating;
    }

    public String getRecentBookingPickUpSuburb() {
        return recentBookingPickUpSuburb;
    }

    public String getRecentBookingDropoffSuburb() {
        return recentBookingDropoffSuburb;
    }
}
