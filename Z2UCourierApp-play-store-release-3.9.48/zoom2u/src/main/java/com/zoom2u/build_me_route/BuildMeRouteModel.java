package com.zoom2u.build_me_route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BuildMeRouteModel implements Serializable {

    @SerializedName("courierId")
    @Expose
    private String courierId;
    @SerializedName("courierName")
    @Expose
    private String courierName;
    @SerializedName("isUnallocatedRun")
    @Expose
    private Boolean isUnallocatedRun;
    @SerializedName("deliveries")
    @Expose
    private List<Delivery> deliveries = null;
    @SerializedName("routePolyline")
    @Expose
    private String routePolyline;
    @SerializedName("runId")
    @Expose
    private Object runId;
    @SerializedName("totalDistanceMetres")
    @Expose
    private Double totalDistanceMetres;
    @SerializedName("pickupLocation")
    @Expose
    private PickupLocation pickupLocation;
    @SerializedName("estimatedEndTime")
    @Expose
    private String EstimatedEndTime;
    @SerializedName("totalDeliveryPrice")
    @Expose
    private String TotalDeliveryPrice;
    @SerializedName("totalCourierPrice")
    @Expose
    private String TotalCourierPrice;
    @SerializedName("routeModelId")
    @Expose
    private String RouteModelId;
    @SerializedName("runNumber")
    @Expose
    private Object runNumber;
    @SerializedName("suburbArea")
    @Expose
    private Object suburbArea;


    public String getRouteModelId() {
        return RouteModelId;
    }

    public void setRouteModelId(String routeModelId) {
        RouteModelId = routeModelId;
    }

    public String getTotalCourierPrice() {
        return TotalCourierPrice;
    }

    public void setTotalCourierPrice(String totalCourierPrice) {
        TotalCourierPrice = totalCourierPrice;
    }

    public Boolean getUnallocatedRun() {
        return isUnallocatedRun;
    }

    public void setUnallocatedRun(Boolean unallocatedRun) {
        isUnallocatedRun = unallocatedRun;
    }

    public PickupLocation getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(PickupLocation pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getEstimatedEndTime() {
        return EstimatedEndTime;
    }

    public void setEstimatedEndTime(String estimatedEndTime) {
        EstimatedEndTime = estimatedEndTime;
    }

    public String getTotalDeliveryPrice() {
        return TotalDeliveryPrice;
    }

    public void setTotalDeliveryPrice(String totalDeliveryPrice) {
        TotalDeliveryPrice = totalDeliveryPrice;
    }
    public String getCourierId() {
        return courierId;
    }

    public void setCourierId(String courierId) {
        this.courierId = courierId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public Boolean getIsUnallocatedRun() {
        return isUnallocatedRun;
    }

    public void setIsUnallocatedRun(Boolean isUnallocatedRun) {
        this.isUnallocatedRun = isUnallocatedRun;
    }

    public List<Delivery> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(List<Delivery> deliveries) {
        this.deliveries = deliveries;
    }

    public String getRoutePolyline() {
        return routePolyline;
    }

    public void setRoutePolyline(String routePolyline) {
        this.routePolyline = routePolyline;
    }

    public Object getRunId() {
        return runId;
    }

    public void setRunId(Object runId) {
        this.runId = runId;
    }

    public Double getTotalDistanceMetres() {
        return totalDistanceMetres;
    }

    public void setTotalDistanceMetres(Double totalDistanceMetres) {
        this.totalDistanceMetres = totalDistanceMetres;
    }

    public Object getRunNumber() {
        return runNumber;
    }

    public void setRunNumber(Object runNumber) {
        this.runNumber = runNumber;
    }

    public Object getSuburbArea() {
        return suburbArea;
    }

    public void setSuburbArea(Object suburbArea) {
        this.suburbArea = suburbArea;
    }



}
