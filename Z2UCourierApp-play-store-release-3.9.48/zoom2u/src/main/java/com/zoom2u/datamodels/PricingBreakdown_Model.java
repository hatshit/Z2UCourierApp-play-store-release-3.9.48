package com.zoom2u.datamodels;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class PricingBreakdown_Model implements Parcelable {

    private double bookingFeeExGst;
    private double courierPaymentExGst;
    private double gst;
    private double totalPrice;
    private double zoom2uFeeExGst;

    public PricingBreakdown_Model(JSONObject jObjOfPricing) {
        try {
            bookingFeeExGst = jObjOfPricing.getDouble("bookingFeeExGst");
            courierPaymentExGst = jObjOfPricing.getDouble("courierPaymentExGst");
            gst = jObjOfPricing.getDouble("gst");
            totalPrice = jObjOfPricing.getDouble("totalPrice");
            zoom2uFeeExGst = jObjOfPricing.getDouble("zoom2uFeeExGst");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getBookingFeeExGst() {
        return bookingFeeExGst;
    }

    public double getCourierPaymentExGst() {
        return courierPaymentExGst;
    }

    public double getGst() {
        return gst;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getZoom2uFeeExGst() {
        return zoom2uFeeExGst;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(bookingFeeExGst);
        dest.writeDouble(courierPaymentExGst);
        dest.writeDouble(gst);
        dest.writeDouble(totalPrice);
        dest.writeDouble(zoom2uFeeExGst);
    }

    public static final Parcelable.Creator<PricingBreakdown_Model> CREATOR  = new Parcelable.Creator<PricingBreakdown_Model>() {
        public PricingBreakdown_Model createFromParcel(Parcel in) {
            return new PricingBreakdown_Model(in);
        }

        public PricingBreakdown_Model[] newArray(int size) {
            return new PricingBreakdown_Model[size];
        }
    };

    private PricingBreakdown_Model(Parcel in) {
        bookingFeeExGst = in.readDouble();
        courierPaymentExGst = in.readDouble();
        gst = in.readDouble();
        totalPrice = in.readDouble();
        zoom2uFeeExGst = in.readDouble();
    }
}
