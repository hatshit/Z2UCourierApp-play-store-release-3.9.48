package com.zoom2u.slidemenu.offerrequesthandlr;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by akanshajain on 24/06/16.
 */
public class RequestView_DetailPojo implements Parcelable {
    private String ExtraLargeQuoteRef;
    private String courierNameMyOffers;
    private String dropContactName;
    private String pickupContactName;
    private String vehicle;
    private String ratingMyOffers;
    private String priceMyOffers;
    private String dateMyOffers;
    private int courierImageMyOffers;
    private String $id;
    private int Id;
    private String Notes;
    private String PickupLatitude;
    private String PickupLongitude;
    private String DropLatitude;
    private String DropLongitude;
    private String PickupSuburb;
    private String DropSuburb;
    private String PickupState;
    private String DropState;
    private String pickUpDateTime;
    private String dropDateTime;
    private String PackageImages;
    private String Offers$id;
    private int OfferId;
    private String Price;
    private String Courier;
    private String CourierImage;
    private String QuoteDateTime;
    private int minPrice;
    private int maxPrice;
    private String distance;
    private  boolean isSuggestedPrice;
    private boolean isCancel;
    private double CourierPricePrice;
    private String Customer;
    private String CustomerID;
    private int totalBids;
    private String pickUpAddress;
    private String dropOffAddress;
    private String offerPrice;
    private String averageBid;
    private String PackagingNotes;
    private String PrimaryPackageImage;
    private String routePolyline;
    private String CustomerPhoto;
    private BidRequest_ChatModel bidRequest_chatModel;

    public String getCustomerPhoto() {
        return CustomerPhoto;
    }

    public void setCustomerPhoto(String customerPhoto) {
        CustomerPhoto = customerPhoto;
    }

    public String getExtraLargeQuoteRef() {
        return ExtraLargeQuoteRef;
    }

    public void setExtraLargeQuoteRef(String extraLargeQuoteRef) {
        ExtraLargeQuoteRef = extraLargeQuoteRef;
    }

    public RequestView_DetailPojo(){
    }

    public String getDropContactName() {
        return dropContactName;
    }

    public void setDropContactName(String dropContactName) {
        this.dropContactName = dropContactName;
    }

    public String getPickupContactName() {
        return pickupContactName;
    }

    public void setPickupContactName(String pickupContactName) {
        this.pickupContactName = pickupContactName;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getCustomer() {
        return Customer;
    }

    public void setCustomer(String customer) {
        Customer = customer;
    }

    public String getDropDateTime() {
        return dropDateTime;
    }

    public void setDropDateTime(String dropDateTime) {
        this.dropDateTime = dropDateTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    private ArrayList<HashMap<String, Object>> shipmentsArray;

    public ArrayList<HashMap<String, Object>> getShipmentsArray() {
        return shipmentsArray;
    }

    public void setShipmentsArray(ArrayList<HashMap<String, Object>> shipmentsArray) {
        this.shipmentsArray = shipmentsArray;
    }

    public String getPickUpDateTime() {
        return pickUpDateTime;
    }

    public void setPickUpDateTime(String pickUpDateTime) {
        this.pickUpDateTime = pickUpDateTime;
    }

    public String getCourierNameMyOffers() {
        return courierNameMyOffers;
    }

    public void setCourierNameMyOffers(String courierNameMyOffers) {
        this.courierNameMyOffers = courierNameMyOffers;
    }

    public String getRatingMyOffers() {
        return ratingMyOffers;
    }

    public void setRatingMyOffers(String ratingMyOffers) {
        this.ratingMyOffers = ratingMyOffers;
    }

    public String getPriceMyOffers() {
        return priceMyOffers;
    }

    public void setPriceMyOffers(String priceMyOffers) {
        this.priceMyOffers = priceMyOffers;
    }

    public String getDateMyOffers() {
        return dateMyOffers;
    }

    public void setDateMyOffers(String dateMyOffers) {
        this.dateMyOffers = dateMyOffers;
    }

    public int getCourierImageMyOffers() {
        return courierImageMyOffers;
    }

    public void setCourierImageMyOffers(int courierImageMyOffers) {
        this.courierImageMyOffers = courierImageMyOffers;
    }

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public String getPickupLatitude() {
        return PickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        PickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return PickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        PickupLongitude = pickupLongitude;
    }

    public String getDropLatitude() {
        return DropLatitude;
    }

    public void setDropLatitude(String dropLatitude) {
        DropLatitude = dropLatitude;
    }

    public String getDropLongitude() {
        return DropLongitude;
    }

    public void setDropLongitude(String dropLongitude) {
        DropLongitude = dropLongitude;
    }

    public String getPickupSuburb() {
        return PickupSuburb;
    }

    public void setPickupSuburb(String pickupSuburb) {
        PickupSuburb = pickupSuburb;
    }

    public String getDropSuburb() {
        return DropSuburb;
    }

    public void setDropSuburb(String dropSuburb) {
        DropSuburb = dropSuburb;
    }

    public String getPickupState() {
        return PickupState;
    }

    public void setPickupState(String pickupState) {
        PickupState = pickupState;
    }

    public String getDropState() {
        return DropState;
    }

    public void setDropState(String dropState) {
        DropState = dropState;
    }

    public String getPackageImages() {
        return PackageImages;
    }

    public void setPackageImages(String packageImages) {
        PackageImages = packageImages;
    }

    public String getOffers$id() {
        return Offers$id;
    }

    public void setOffers$id(String offers$id) {
        Offers$id = offers$id;
    }

    public int getOfferId() {
        return OfferId;
    }

    public void setOfferId(int offerId) {
        OfferId = offerId;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCourier() {
        return Courier;
    }

    public void setCourier(String courier) {
        Courier = courier;
    }

    public String getCourierImage() {
        return CourierImage;
    }

    public void setCourierImage(String courierImage) {
        CourierImage = courierImage;
    }

    public String getQuoteDateTime() {
        return QuoteDateTime;
    }

    public void setQuoteDateTime(String quoteDateTime) {
        QuoteDateTime = quoteDateTime;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public boolean isSuggestedPrice() {
        return isSuggestedPrice;
    }

    public void setSuggestedPrice(boolean suggestedPrice) {
        isSuggestedPrice = suggestedPrice;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public double getCourierPrice() {
        return CourierPricePrice;
    }

    public void setCourierPrice(double customerPrice) {
        CourierPricePrice = customerPrice;
    }

    public int getTotalBids() {
        return totalBids;
    }

    public void setTotalBids(int totalBids) {
        this.totalBids = totalBids;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getAverageBid() {
        return averageBid;
    }

    public void setAverageBid(String averageBid) {
        this.averageBid = averageBid;
    }

    public BidRequest_ChatModel getBidRequest_chatModel() {
        return bidRequest_chatModel;
    }

    public void setBidRequest_chatModel(BidRequest_ChatModel bidRequest_chatModel) {
        this.bidRequest_chatModel = bidRequest_chatModel;
    }

    public String getPackagingNotes() {
        return PackagingNotes;
    }

    public void setPackagingNotes(String packagingNotes) {
        PackagingNotes = packagingNotes;
    }

    public String getPrimaryPackageImage() {
        return PrimaryPackageImage;
    }

    public void setPrimaryPackageImage(String primaryPackageImage) {
        PrimaryPackageImage = primaryPackageImage;
    }

    public String getRoutePolyline() {
        return routePolyline;
    }

    public void setRoutePolyline(String routePolyline) {
        this.routePolyline = routePolyline;
    }

    @Override
    public String toString() {
        return "RequestView_DetailPojo{" +
                "courierNameMyOffers='" + courierNameMyOffers + '\'' +
                "dropContactName='" + dropContactName + '\'' +
                "pickupContactName='" + pickupContactName + '\'' +
                "vehicle='" + vehicle + '\'' +
                ", ratingMyOffers='" + ratingMyOffers + '\'' +
                ", priceMyOffers='" + priceMyOffers + '\'' +
                ", dateMyOffers='" + dateMyOffers + '\'' +
                ", courierImageMyOffers=" + courierImageMyOffers +
                ", $id='" + $id + '\'' +
                ", Id='" + Id + '\'' +
                ", Notes='" + Notes + '\'' +
                ", PickupLatitude='" + PickupLatitude + '\'' +
                ", PickupLongitude='" + PickupLongitude + '\'' +
                ", DropLatitude='" + DropLatitude + '\'' +
                ", DropLongitude='" + DropLongitude + '\'' +
                ", PickupSuburb='" + PickupSuburb + '\'' +
                ", DropSuburb='" + DropSuburb + '\'' +
                ", PickupState='" + PickupState + '\'' +
                ", DropState='" + DropState + '\'' +
                ", pickUpDateTime='" + pickUpDateTime + '\'' +
                ", dropDateTime='" + dropDateTime + '\'' +
                ", PackageImages='" + PackageImages + '\'' +
                ", Offers$id='" + Offers$id + '\'' +
                ", OfferId='" + OfferId + '\'' +
                ", Price='" + Price + '\'' +
                ", Courier='" + Courier + '\'' +
                ", CourierImage='" + CourierImage + '\'' +
                ", QuoteDateTime='" + QuoteDateTime + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", maxPrice='" + maxPrice + '\'' +
                ", distance='" + distance + '\'' +
                ", isSuggestedPrice='" + isSuggestedPrice + '\'' +
                ", isCancel='" + isCancel + '\'' +
                ", CourierPricePrice='" + CourierPricePrice + '\'' +
                ", pickUpAddress='" + pickUpAddress + '\'' +
                ", dropOffAddress='" + dropOffAddress + '\'' +
                ", Customer='" + Customer + '\'' +
                ", totalBids='" + totalBids + '\'' +
                ", CustomerID='" + CustomerID + '\'' +
                ", offerPrice='" + offerPrice + '\'' +
        ", averageBid='" + averageBid + '\'' +
                ", bidRequest_chatModel='" + bidRequest_chatModel + '\'' +
                ", PackagingNotes='" + PackagingNotes + '\'' +
                ", PrimaryPackageImage='" + PrimaryPackageImage + '\'' +
                ", routePolyline='" + routePolyline + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courierNameMyOffers);
        dest.writeString(dropContactName);
        dest.writeString(pickupContactName);
        dest.writeString(vehicle);
        dest.writeString(ratingMyOffers);
        dest.writeString(priceMyOffers);
        dest.writeString(dateMyOffers);
        dest.writeInt(courierImageMyOffers);
        dest.writeString($id);
        dest.writeInt(Id);
        dest.writeString(Notes);
        dest.writeString(PickupLatitude);
        dest.writeString(PickupLongitude);
        dest.writeString(DropLatitude);
        dest.writeString(DropLongitude);
        dest.writeString(PickupSuburb);
        dest.writeString(DropSuburb);
        dest.writeString(PickupState);
        dest.writeString(DropState);
        dest.writeString(pickUpDateTime);
        dest.writeString(dropDateTime);
        dest.writeString(PackageImages);
        dest.writeString(Offers$id);
        dest.writeInt(OfferId);
        dest.writeString(Price);
        dest.writeString(Courier);
        dest.writeString(CourierImage);
        dest.writeString(QuoteDateTime);
        dest.writeInt(minPrice);
        dest.writeInt(maxPrice);
        dest.writeString(distance);
        dest.writeString(ExtraLargeQuoteRef);
        dest.writeByte((byte) (isSuggestedPrice ? 1 : 0));
        dest.writeByte((byte) (isCancel ? 1 : 0));
        dest.writeDouble(CourierPricePrice);
        dest.writeString(Customer);
        dest.writeString(CustomerID);
        dest.writeInt(totalBids);
        dest.writeString(pickUpAddress);
        dest.writeString(dropOffAddress);
        dest.writeString(offerPrice);
        dest.writeString(averageBid);
        dest.writeParcelable(bidRequest_chatModel, flags);
        dest.writeString(PackagingNotes);
        dest.writeString(PrimaryPackageImage);
        dest.writeString(routePolyline);
        dest.writeString(CustomerPhoto);
    }

    private RequestView_DetailPojo(Parcel in){
        courierNameMyOffers = in.readString();
        dropContactName = in.readString();
        pickupContactName = in.readString();
        vehicle = in.readString();
        ratingMyOffers = in.readString();
        priceMyOffers = in.readString();
        dateMyOffers = in.readString();
        courierImageMyOffers = in.readInt();
        $id = in.readString();
        Id = in.readInt();
        Notes = in.readString();
        PickupLatitude = in.readString();
        PickupLongitude = in.readString();
        DropLatitude = in.readString();
        DropLongitude = in.readString();
        PickupSuburb = in.readString();
        DropSuburb = in.readString();
        PickupState = in.readString();
        DropState = in.readString();
        pickUpDateTime = in.readString();
        dropDateTime = in.readString();
        PackageImages = in.readString();
        Offers$id = in.readString();
        OfferId = in.readInt();
        Price = in.readString();
        Courier = in.readString();
        CourierImage = in.readString();
        QuoteDateTime = in.readString();
        minPrice = in.readInt();
        maxPrice = in.readInt();
        distance = in.readString();
        ExtraLargeQuoteRef = in.readString();
        isSuggestedPrice = in.readByte() != 0;
        isCancel = in.readByte() != 0;
        CourierPricePrice = in.readDouble();
        Customer = in.readString();
        CustomerID = in.readString();
        totalBids = in.readInt();
        pickUpAddress = in.readString();
        dropOffAddress = in.readString();
        offerPrice = in.readString();
        averageBid = in.readString();
        bidRequest_chatModel = in.readParcelable(BidRequest_ChatModel.class.getClassLoader());
        PackagingNotes = in.readString();
        PrimaryPackageImage =  in.readString();
        routePolyline = in.readString();
        CustomerPhoto=in.readString();
    }

    public static final Parcelable.Creator<RequestView_DetailPojo> CREATOR  = new Parcelable.Creator<RequestView_DetailPojo>() {
        public RequestView_DetailPojo createFromParcel(Parcel in) {
            return new RequestView_DetailPojo(in);
        }

        public RequestView_DetailPojo[] newArray(int size) {
            return new RequestView_DetailPojo[size];
        }
    };
}
