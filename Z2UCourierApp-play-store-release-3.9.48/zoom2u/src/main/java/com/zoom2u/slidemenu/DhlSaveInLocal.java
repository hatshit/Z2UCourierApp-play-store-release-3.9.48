package com.zoom2u.slidemenu;

import android.os.Parcel;
import android.os.Parcelable;

public class DhlSaveInLocal implements Parcelable {
    private int runId;
    private String orderNo;
    private int bookingId;
    private String pickupAddress;
    private String status;


    public DhlSaveInLocal(int runId, String orderNo, int bookingId, String pickupAddress, String status) {
        this.runId = runId;
        this.orderNo = orderNo;
        this.bookingId = bookingId;
        this.pickupAddress = pickupAddress;
        this.status = status;
    }
    public int getRunId() {
        return runId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(runId);
        dest.writeString(orderNo);
        dest.writeInt(bookingId);
        dest.writeString(pickupAddress);
        dest.writeString(status);
    }


}
