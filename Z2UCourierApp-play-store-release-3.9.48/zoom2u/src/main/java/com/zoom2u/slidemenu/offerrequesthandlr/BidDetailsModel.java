package com.zoom2u.slidemenu.offerrequesthandlr;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BidDetailsModel {

    @SerializedName("$id")
    @Expose
    private String $id;
    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("OfferId")
    @Expose
    private Integer offerId;
    @SerializedName("RequestId")
    @Expose
    private Integer requestId;
    @SerializedName("Customer")
    @Expose
    private String customer;
    @SerializedName("CustomerPhoto")
    @Expose
    private Object customerPhoto;
    @SerializedName("Notes")
    @Expose
    private String notes;
    @SerializedName("PickupLatitude")
    @Expose
    private String pickupLatitude;
    @SerializedName("PickupLongitude")
    @Expose
    private String pickupLongitude;
    @SerializedName("DropLatitude")
    @Expose
    private String dropLatitude;
    @SerializedName("DropLongitude")
    @Expose
    private String dropLongitude;
    @SerializedName("PickupSuburb")
    @Expose
    private String pickupSuburb;
    @SerializedName("DropSuburb")
    @Expose
    private String dropSuburb;
    @SerializedName("PickupState")
    @Expose
    private String pickupState;
    @SerializedName("DropState")
    @Expose
    private String dropState;
    @SerializedName("PickupDateTime")
    @Expose
    private String pickupDateTime;
    @SerializedName("DropDateTime")
    @Expose
    private String dropDateTime;
    @SerializedName("PickupContactName")
    @Expose
    private String pickupContactName;
    @SerializedName("DropContactName")
    @Expose
    private String dropContactName;
    @SerializedName("PickupAddress")
    @Expose
    private String pickupAddress;
    @SerializedName("DropAddress")
    @Expose
    private String dropAddress;
    @SerializedName("PackageImages")
    @Expose
    private List<String> packageImages = null;
    @SerializedName("OfferDetails")
    @Expose
    private OfferDetails offerDetails;
    @SerializedName("TotalBids")
    @Expose
    private Integer totalBids;
    @SerializedName("IsSuggestedPrice")
    @Expose
    private Boolean isSuggestedPrice;
    @SerializedName("SuggestedPrice")
    @Expose
    private Double suggestedPrice;
    @SerializedName("Distance")
    @Expose
    private String distance;
    @SerializedName("CustomerId")
    @Expose
    private String customerId;
    @SerializedName("MinPrice")
    @Expose
    private Double minPrice;
    @SerializedName("MaxPrice")
    @Expose
    private Double maxPrice;
    @SerializedName("AverageBid")
    @Expose
    private Double averageBid;
    @SerializedName("Price")
    @Expose
    private String price;
    @SerializedName("Shipments")
    @Expose
    private List<Shipment> shipments = null;
    @SerializedName("IsCancel")
    @Expose
    private Boolean isCancel;
    @SerializedName("Vehicle")
    @Expose
    private String vehicle;
    @SerializedName("PackagingNotes")
    @Expose
    private String packagingNotes;
    @SerializedName("ExtraLargeQuoteRef")
    @Expose
    private String extraLargeQuoteRef;
    @SerializedName("RoutePolyline")
    @Expose
    private String routePolyline;
    @SerializedName("PrimaryPackageImage")
    @Expose
    private Object primaryPackageImage;
    @SerializedName("PickupCompany")
    @Expose
    private String pickupCompany;
    @SerializedName("DropCompany")
    @Expose
    private String dropCompany;
    @SerializedName("CourierPrice")
    @Expose
    private Double courierPrice;

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }

    public String get$type() {
        return $type;
    }

    public void set$type(String $type) {
        this.$type = $type;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Object getCustomerPhoto() {
        return customerPhoto;
    }

    public void setCustomerPhoto(Object customerPhoto) {
        this.customerPhoto = customerPhoto;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getDropLatitude() {
        return dropLatitude;
    }

    public void setDropLatitude(String dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public String getDropLongitude() {
        return dropLongitude;
    }

    public void setDropLongitude(String dropLongitude) {
        this.dropLongitude = dropLongitude;
    }

    public String getPickupSuburb() {
        return pickupSuburb;
    }

    public void setPickupSuburb(String pickupSuburb) {
        this.pickupSuburb = pickupSuburb;
    }

    public String getDropSuburb() {
        return dropSuburb;
    }

    public void setDropSuburb(String dropSuburb) {
        this.dropSuburb = dropSuburb;
    }

    public String getPickupState() {
        return pickupState;
    }

    public void setPickupState(String pickupState) {
        this.pickupState = pickupState;
    }

    public String getDropState() {
        return dropState;
    }

    public void setDropState(String dropState) {
        this.dropState = dropState;
    }

    public String getPickupDateTime() {
        return pickupDateTime;
    }

    public void setPickupDateTime(String pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public String getDropDateTime() {
        return dropDateTime;
    }

    public void setDropDateTime(String dropDateTime) {
        this.dropDateTime = dropDateTime;
    }

    public String getPickupContactName() {
        return pickupContactName;
    }

    public void setPickupContactName(String pickupContactName) {
        this.pickupContactName = pickupContactName;
    }

    public String getDropContactName() {
        return dropContactName;
    }

    public void setDropContactName(String dropContactName) {
        this.dropContactName = dropContactName;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public List<String> getPackageImages() {
        return packageImages;
    }

    public void setPackageImages(List<String> packageImages) {
        this.packageImages = packageImages;
    }

    public OfferDetails getOfferDetails() {
        return offerDetails;
    }

    public void setOfferDetails(OfferDetails offerDetails) {
        this.offerDetails = offerDetails;
    }

    public Integer getTotalBids() {
        return totalBids;
    }

    public void setTotalBids(Integer totalBids) {
        this.totalBids = totalBids;
    }

    public Boolean getIsSuggestedPrice() {
        return isSuggestedPrice;
    }

    public void setIsSuggestedPrice(Boolean isSuggestedPrice) {
        this.isSuggestedPrice = isSuggestedPrice;
    }

    public Double getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(Double suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getAverageBid() {
        return averageBid;
    }

    public void setAverageBid(Double averageBid) {
        this.averageBid = averageBid;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public Boolean getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Boolean isCancel) {
        this.isCancel = isCancel;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getPackagingNotes() {
        return packagingNotes;
    }

    public void setPackagingNotes(String packagingNotes) {
        this.packagingNotes = packagingNotes;
    }

    public String getExtraLargeQuoteRef() {
        return extraLargeQuoteRef;
    }

    public void setExtraLargeQuoteRef(String extraLargeQuoteRef) {
        this.extraLargeQuoteRef = extraLargeQuoteRef;
    }

    public String getRoutePolyline() {
        return routePolyline;
    }

    public void setRoutePolyline(String routePolyline) {
        this.routePolyline = routePolyline;
    }

    public Object getPrimaryPackageImage() {
        return primaryPackageImage;
    }

    public void setPrimaryPackageImage(Object primaryPackageImage) {
        this.primaryPackageImage = primaryPackageImage;
    }

    public String getPickupCompany() {
        return pickupCompany;
    }

    public void setPickupCompany(String pickupCompany) {
        this.pickupCompany = pickupCompany;
    }

    public String getDropCompany() {
        return dropCompany;
    }

    public void setDropCompany(String dropCompany) {
        this.dropCompany = dropCompany;
    }

    public Double getCourierPrice() {
        return courierPrice;
    }

    public void setCourierPrice(Double courierPrice) {
        this.courierPrice = courierPrice;
    }

}


class OfferDetails {

    @SerializedName("$id")
    @Expose
    private String $id;
    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("OfferId")
    @Expose
    private Integer offerId;
    @SerializedName("Price")
    @Expose
    private Object price;
    @SerializedName("QuoteDateTime")
    @Expose
    private Object quoteDateTime;
    @SerializedName("PickupETA")
    @Expose
    private Object pickupETA;
    @SerializedName("IsCancel")
    @Expose
    private Boolean isCancel;

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }

    public String get$type() {
        return $type;
    }

    public void set$type(String $type) {
        this.$type = $type;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public Object getQuoteDateTime() {
        return quoteDateTime;
    }

    public void setQuoteDateTime(Object quoteDateTime) {
        this.quoteDateTime = quoteDateTime;
    }

    public Object getPickupETA() {
        return pickupETA;
    }

    public void setPickupETA(Object pickupETA) {
        this.pickupETA = pickupETA;
    }

    public Boolean getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Boolean isCancel) {
        this.isCancel = isCancel;
    }

}

class Shipment {

    @SerializedName("$id")
    @Expose
    private String $id;
    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("Category")
    @Expose
    private String category;
    @SerializedName("Quantity")
    @Expose
    private Integer quantity;
    @SerializedName("LengthCm")
    @Expose
    private Integer lengthCm;
    @SerializedName("WidthCm")
    @Expose
    private Integer widthCm;
    @SerializedName("HeightCm")
    @Expose
    private Integer heightCm;
    @SerializedName("ItemWeightKg")
    @Expose
    private Double itemWeightKg;
    @SerializedName("TotalWeightKg")
    @Expose
    private Double totalWeightKg;

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }

    public String get$type() {
        return $type;
    }

    public void set$type(String $type) {
        this.$type = $type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getLengthCm() {
        return lengthCm;
    }

    public void setLengthCm(Integer lengthCm) {
        this.lengthCm = lengthCm;
    }

    public Integer getWidthCm() {
        return widthCm;
    }

    public void setWidthCm(Integer widthCm) {
        this.widthCm = widthCm;
    }

    public Integer getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Integer heightCm) {
        this.heightCm = heightCm;
    }

    public Double getItemWeightKg() {
        return itemWeightKg;
    }

    public void setItemWeightKg(Double itemWeightKg) {
        this.itemWeightKg = itemWeightKg;
    }

    public Double getTotalWeightKg() {
        return totalWeightKg;
    }

    public void setTotalWeightKg(Double totalWeightKg) {
        this.totalWeightKg = totalWeightKg;
    }

}