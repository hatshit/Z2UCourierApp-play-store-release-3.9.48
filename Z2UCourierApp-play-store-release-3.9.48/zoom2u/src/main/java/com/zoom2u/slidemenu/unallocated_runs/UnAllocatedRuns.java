package com.zoom2u.slidemenu.unallocated_runs;

import android.os.Parcel;
import android.os.Parcelable;

import com.zoom2u.offer_run_batch.model.Customer;
import com.zoom2u.offer_run_batch.model.EndLocation;
import com.zoom2u.offer_run_batch.model.StartLocation;

public class UnAllocatedRuns implements Parcelable {
    private String runId;
    private String runBatchId;
    private String suburbArea;
    private int totalDistanceInMetres;
    private String runDate;
    private String startTime;
    private String endTime;
    private StartLocation startLocation;
    private Customer customer;
    private Integer numberOfStops;
    private Integer numberOfRuns;
    private String totalCourierPayment;
    private String routePolyline;

    public String getRoutePolyline() {
        return routePolyline;
    }

    public void setRoutePolyline(String routePolyline) {
        this.routePolyline = routePolyline;
    }

    public String getRunBatchId() {
        return runBatchId;
    }

    public void setRunBatchId(String runBatchId) {
        this.runBatchId = runBatchId;
    }

    public Integer getNumberOfRuns() {
        return numberOfRuns;
    }

    public void setNumberOfRuns(Integer numberOfRuns) {
        this.numberOfRuns = numberOfRuns;
    }

    public String getTotalCourierPayment() {
        return totalCourierPayment;
    }

    public void setTotalCourierPayment(String totalCourierPayment) {
        this.totalCourierPayment = totalCourierPayment;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getSuburbArea() {
        return suburbArea;
    }

    public void setSuburbArea(String suburbArea) {
        this.suburbArea = suburbArea;
    }

    public int getTotalDistanceInMetres() {
        return totalDistanceInMetres;
    }

    public void setTotalDistanceInMetres(int totalDistanceInMetres) {
        this.totalDistanceInMetres = totalDistanceInMetres;
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

    public StartLocation getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(StartLocation startLocation) {
        this.startLocation = startLocation;
    }

    public Integer getNumberOfStops() {
        return numberOfStops;
    }

    public void setNumberOfStops(Integer numberOfStops) {
        this.numberOfStops = numberOfStops;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
