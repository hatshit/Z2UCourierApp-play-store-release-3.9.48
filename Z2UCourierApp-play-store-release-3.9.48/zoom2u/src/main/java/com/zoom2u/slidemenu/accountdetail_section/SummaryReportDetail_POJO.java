package com.zoom2u.slidemenu.accountdetail_section;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/*** Created by Arun on 24-Nov-2017 ***/

public class SummaryReportDetail_POJO {

    private String courierID;
    private int deliveriesOfToday;
    private int deliveriesOfLastWeek;
    private int moneyOfToday;
    private int moneyOfLastWeek;
    private int thumbsUpOfToday;
    private int thumbsUpOfLastWeek;
    private int thumbsDownOfToday;
    private int thumbsDownOfLastWeek;
    private int pointsOfToday;
    private int pointsOfLastWeek;
    private String startDate;
    private String endDate;
    private int totalThumbsUp;
    private int totalThumbsDown;
    private String email;
    private ArrayList<RecentBookingItems_SR_POJO> arrayOfRecentBookings;
    private double totalBookingsPercentage;

    public SummaryReportDetail_POJO (JSONObject jObjOfSummaryReportDetail) {
        try {
            JSONObject jobjOfCourierSummary = jObjOfSummaryReportDetail.getJSONObject("courierSummaryReport");
            this.courierID = jobjOfCourierSummary.getString("CourierId");
            this.deliveriesOfToday = jobjOfCourierSummary.getInt("DeliveriesOfToday");
            this.deliveriesOfLastWeek = jobjOfCourierSummary.getInt("DeliveriesOfLastWeek");
            this.moneyOfToday = jobjOfCourierSummary.getInt("MoneyOfToday");
            this.moneyOfLastWeek = jobjOfCourierSummary.getInt("MoneyOfLastWeek");
            this.thumbsUpOfToday = jobjOfCourierSummary.getInt("ThumbsUpOfToday");
            this.thumbsUpOfLastWeek = jobjOfCourierSummary.getInt("ThumbsUpOfLastWeek");
            this.pointsOfToday = jobjOfCourierSummary.getInt("PointsOfToday");
            this.pointsOfLastWeek = jobjOfCourierSummary.getInt("PointsOfLastWeek");
            this.startDate = jobjOfCourierSummary.getString("StartDate");
            this.endDate = jobjOfCourierSummary.getString("EndDate");
            this.totalThumbsUp = jobjOfCourierSummary.getInt("TotalThumbsUp");
            this.totalThumbsDown = jobjOfCourierSummary.getInt("TotalThumbsDown");
            this.email = jobjOfCourierSummary.getString("Email");

            if (this.arrayOfRecentBookings != null)
                this.arrayOfRecentBookings.clear();
            else
                this.arrayOfRecentBookings = new ArrayList<RecentBookingItems_SR_POJO>();
            for (int i = 0; i < jObjOfSummaryReportDetail.getJSONArray("recentBookings").length(); i++) {
                RecentBookingItems_SR_POJO recentBookingItemsSrPojo = new RecentBookingItems_SR_POJO(jObjOfSummaryReportDetail.getJSONArray("recentBookings").getJSONObject(i));
                this.arrayOfRecentBookings.add(recentBookingItemsSrPojo);
                recentBookingItemsSrPojo = null;
            }
            this.totalBookingsPercentage = jObjOfSummaryReportDetail.getDouble("rating");

//            this.thumbsDownOfToday = jobjOfCourierSummary.getInt("ThumbsDownOfToday");
//            this.thumbsDownOfLastWeek = jobjOfCourierSummary.getInt("ThumbsDownOfLastWeek");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCourierID() {
        return courierID;
    }

    public int getDeliveriesOfToday() {
        return deliveriesOfToday;
    }

    public int getDeliveriesOfLastWeek() {
        return deliveriesOfLastWeek;
    }

    public int getMoneyOfToday() {
        return moneyOfToday;
    }

    public int getMoneyOfLastWeek() {
        return moneyOfLastWeek;
    }

    public int getThumbsUpOfToday() {
        return thumbsUpOfToday;
    }

    public int getThumbsUpOfLastWeek() {
        return thumbsUpOfLastWeek;
    }

    public int getThumbsDownOfToday() {
        return thumbsDownOfToday;
    }

    public void setThumbsDownOfToday(int thumbsDownOfToday) {
        this.thumbsDownOfToday = thumbsDownOfToday;
    }

    public int getThumbsDownOfLastWeek() {
        return thumbsDownOfLastWeek;
    }

    public void setThumbsDownOfLastWeek(int thumbsDownOfLastWeek) {
        this.thumbsDownOfLastWeek = thumbsDownOfLastWeek;
    }

    public int getPointsOfToday() {
        return pointsOfToday;
    }

    public int getPointsOfLastWeek() {
        return pointsOfLastWeek;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public int getTotalThumbsUp() {
        return totalThumbsUp;
    }

    public int getTotalThumbsDown() {
        return totalThumbsDown;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<RecentBookingItems_SR_POJO> getArrayOfRecentBookings() {
        return arrayOfRecentBookings;
    }

    public double getTotalBookingsPercentage() {
        return totalBookingsPercentage;
    }
}
