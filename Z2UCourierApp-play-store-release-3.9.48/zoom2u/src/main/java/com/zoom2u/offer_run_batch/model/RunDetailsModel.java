package com.zoom2u.offer_run_batch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Mahendra Dabi on 09-08-2021.
 */
public class RunDetailsModel implements Serializable {
    @SerializedName("runId")
    @Expose
    private int runId;
    @SerializedName("isReverseRun")
    @Expose
    private boolean isReverseRun;
    @SerializedName("totalDistanceInMetres")
    @Expose
    private int totalDistanceInMetres;
    @SerializedName("possibleEarnings")
    @Expose
    private double possibleEarnings;
    @SerializedName("runDate")
    @Expose
    private String runDate;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    @SerializedName("vehicle")
    @Expose
    private String vehicle;
    @SerializedName("startLocation")
    @Expose
    private StartLocation startLocation;
    @SerializedName("customer")
    @Expose
    private Customer customer;
    @SerializedName("stops")
    @Expose
    private List<Stop> stops = null;


    public boolean getIsReverseRun() {
        return isReverseRun;
    }

    public void setIsReverseRun(boolean isReverseRun) {
        this.isReverseRun = isReverseRun;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }


    public int getTotalDistanceInMetres() {
        return totalDistanceInMetres;
    }

    public void setTotalDistanceInMetres(int totalDistanceInMetres) {
        this.totalDistanceInMetres = totalDistanceInMetres;
    }

    public double getPossibleEarnings() {
        return possibleEarnings;
    }

    public void setPossibleEarnings(double possibleEarnings) {
        this.possibleEarnings = possibleEarnings;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public StartLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(StartLocation startLocation) {
        this.startLocation = startLocation;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

}
