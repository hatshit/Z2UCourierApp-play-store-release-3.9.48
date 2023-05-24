package com.zoom2u.build_me_route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Delivery implements Serializable {

    @SerializedName("row")
    @Expose
    private Integer row;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fullAddress")
    @Expose
    private String fullAddress;
    @SerializedName("geocodingResultCode")
    @Expose
    private Object geocodingResultCode;
    @SerializedName("unitNumber")
    @Expose
    private String unitNumber;
    @SerializedName("streetNumber")
    @Expose
    private String streetNumber;
    @SerializedName("street")
    @Expose
    private String street;
    @SerializedName("suburb")
    @Expose
    private String suburb;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("postcode")
    @Expose
    private String postcode;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("sender")
    @Expose
    private String sender;
    @SerializedName("authorityToLeave")
    @Expose
    private Boolean authorityToLeave;
    @SerializedName("offerAuthorityToLeave")
    @Expose
    private Boolean offerAuthorityToLeave;
    @SerializedName("contactName")
    @Expose
    private String contactName;
    @SerializedName("contactPhone")
    @Expose
    private String contactPhone;
    @SerializedName("contactEmail")
    @Expose
    private Object contactEmail;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("order")
    @Expose
    private Integer order;
    @SerializedName("dropEta")
    @Expose
    private String dropEta;
    @SerializedName("routePolyline")
    @Expose
    private Object routePolyline;
    @SerializedName("runNumber")
    @Expose
    private Object runNumber;
    @SerializedName("bookingId")
    @Expose
    private Integer bookingId;
    @SerializedName("stopId")
    @Expose
    private Object stopId;
    @SerializedName("bookingRef")
    @Expose
    private String bookingRef;
    @SerializedName("dropActual")
    @Expose
    private Object dropActual;
    @SerializedName("runCode")
    @Expose
    private Object runCode;
    @SerializedName("dhlBatchId")
    @Expose
    private Object dhlBatchId;
    @SerializedName("dhlImportId")
    @Expose
    private Object dhlImportId;
    @SerializedName("importId")
    @Expose
    private Object importId;
    @SerializedName("customerImportId")
    @Expose
    private Object customerImportId;
    @SerializedName("customerId")
    @Expose
    private String customerId;
    @SerializedName("additionalData")
    @Expose
    private Object additionalData;
    @SerializedName("price")
    @Expose
    private Object price;
    @SerializedName("courierPayment")
    @Expose
    private Object courierPayment;
    @SerializedName("isValid")
    @Expose
    private Boolean isValid;
    @SerializedName("distanceFromLastStop")
    @Expose
    private Object distanceFromLastStop;
    @SerializedName("shipmentId")
    @Expose
    private Object shipmentId;
    @SerializedName("additionalRunOptions")
    @Expose
    private Object additionalRunOptions;
    @SerializedName("validationErrors")
    @Expose
    private List<Object> validationErrors = null;
    @SerializedName("errorList")
    @Expose
    private List<Object> errorList = null;
    @SerializedName("warningList")
    @Expose
    private List<Object> warningList = null;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Object getGeocodingResultCode() {
        return geocodingResultCode;
    }

    public void setGeocodingResultCode(Object geocodingResultCode) {
        this.geocodingResultCode = geocodingResultCode;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Boolean getAuthorityToLeave() {
        return authorityToLeave;
    }

    public void setAuthorityToLeave(Boolean authorityToLeave) {
        this.authorityToLeave = authorityToLeave;
    }

    public Boolean getOfferAuthorityToLeave() {
        return offerAuthorityToLeave;
    }

    public void setOfferAuthorityToLeave(Boolean offerAuthorityToLeave) {
        this.offerAuthorityToLeave = offerAuthorityToLeave;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public Object getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(Object contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getDropEta() {
        return dropEta;
    }

    public void setDropEta(String dropEta) {
        this.dropEta = dropEta;
    }

    public Object getRoutePolyline() {
        return routePolyline;
    }

    public void setRoutePolyline(Object routePolyline) {
        this.routePolyline = routePolyline;
    }

    public Object getRunNumber() {
        return runNumber;
    }

    public void setRunNumber(Object runNumber) {
        this.runNumber = runNumber;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Object getStopId() {
        return stopId;
    }

    public void setStopId(Object stopId) {
        this.stopId = stopId;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public Object getDropActual() {
        return dropActual;
    }

    public void setDropActual(Object dropActual) {
        this.dropActual = dropActual;
    }

    public Object getRunCode() {
        return runCode;
    }

    public void setRunCode(Object runCode) {
        this.runCode = runCode;
    }

    public Object getDhlBatchId() {
        return dhlBatchId;
    }

    public void setDhlBatchId(Object dhlBatchId) {
        this.dhlBatchId = dhlBatchId;
    }

    public Object getDhlImportId() {
        return dhlImportId;
    }

    public void setDhlImportId(Object dhlImportId) {
        this.dhlImportId = dhlImportId;
    }

    public Object getImportId() {
        return importId;
    }

    public void setImportId(Object importId) {
        this.importId = importId;
    }

    public Object getCustomerImportId() {
        return customerImportId;
    }

    public void setCustomerImportId(Object customerImportId) {
        this.customerImportId = customerImportId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }

    public Object getPrice() {
        return price;
    }

    public void setPrice(Object price) {
        this.price = price;
    }

    public Object getCourierPayment() {
        return courierPayment;
    }

    public void setCourierPayment(Object courierPayment) {
        this.courierPayment = courierPayment;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Object getDistanceFromLastStop() {
        return distanceFromLastStop;
    }

    public void setDistanceFromLastStop(Object distanceFromLastStop) {
        this.distanceFromLastStop = distanceFromLastStop;
    }

    public Object getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(Object shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Object getAdditionalRunOptions() {
        return additionalRunOptions;
    }

    public void setAdditionalRunOptions(Object additionalRunOptions) {
        this.additionalRunOptions = additionalRunOptions;
    }

    public List<Object> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<Object> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public List<Object> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<Object> errorList) {
        this.errorList = errorList;
    }

    public List<Object> getWarningList() {
        return warningList;
    }

    public void setWarningList(List<Object> warningList) {
        this.warningList = warningList;
    }

}