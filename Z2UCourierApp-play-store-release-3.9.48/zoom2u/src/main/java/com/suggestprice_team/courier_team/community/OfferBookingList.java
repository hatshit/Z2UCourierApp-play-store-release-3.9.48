package com.suggestprice_team.courier_team.community;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OfferBookingList {
    @SerializedName("$id")
    @Expose
    private String $id;
    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("list")
    @Expose
    private ArrayList <List> list;

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

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public ArrayList <List> getList() {
        return list;
    }

    public void setList(ArrayList<List> list) {
        this.list = list;
    }

    public static class List {

        @SerializedName("$id")
        @Expose
        private String $id;
        @SerializedName("$type")
        @Expose
        private String $type;
        @SerializedName("OfferId")
        @Expose
        private Integer offerId;
        @SerializedName("BookingId")
        @Expose
        private Integer bookingId;
        @SerializedName("CourierId")
        @Expose
        private String courierId;
        @SerializedName("SenderId")
        @Expose
        private Object senderId;
        @SerializedName("OfferDateTime")
        @Expose
        private String offerDateTime;
        @SerializedName("OfferAcceptedDateTime")
        @Expose
        private Object offerAcceptedDateTime;
        @SerializedName("RejectedDateTime")
        @Expose
        private Object rejectedDateTime;
        @SerializedName("RejectionReason")
        @Expose
        private Object rejectionReason;
        @SerializedName("IsExpired")
        @Expose
        private Boolean isExpired;
        @SerializedName("FirstName")
        @Expose
        private String firstName;
        @SerializedName("LastName")
        @Expose
        private String lastName;
        @SerializedName("Mobile")
        @Expose
        private String mobile;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("CustomerId")
        @Expose
        private Object customerId;
        @SerializedName("CustomerName")
        @Expose
        private String customerName;
        @SerializedName("CustomerCompany")
        @Expose
        private String customerCompany;
        @SerializedName("CustomerContact")
        @Expose
        private String customerContact;
        @SerializedName("BookingRefNo")
        @Expose
        private String bookingRefNo;
        @SerializedName("PickupDateTime")
        @Expose
        private String pickupDateTime;
        @SerializedName("PickupAddress")
        @Expose
        private String pickupAddress;
        @SerializedName("PickupContactName")
        @Expose
        private String pickupContactName;
        @SerializedName("PickupGPSX")
        @Expose
        private String pickupGPSX;
        @SerializedName("PickupGPSY")
        @Expose
        private String pickupGPSY;
        @SerializedName("PickupNotes")
        @Expose
        private String pickupNotes;
        @SerializedName("PickupPhone")
        @Expose
        private String pickupPhone;
        @SerializedName("PickupSuburb")
        @Expose
        private String pickupSuburb;
        @SerializedName("DropDateTime")
        @Expose
        private String dropDateTime;
        @SerializedName("DropAddress")
        @Expose
        private String dropAddress;
        @SerializedName("DropContactName")
        @Expose
        private String dropContactName;
        @SerializedName("DropGPSX")
        @Expose
        private String dropGPSX;
        @SerializedName("DropGPSY")
        @Expose
        private String dropGPSY;
        @SerializedName("DropNotes")
        @Expose
        private String dropNotes;
        @SerializedName("DropPhone")
        @Expose
        private String dropPhone;
        @SerializedName("DropSuburb")
        @Expose
        private String dropSuburb;
        @SerializedName("RoutePolyline")
        @Expose
        private String routePolyline;
        @SerializedName("CreatedDateTime")
        @Expose
        private String createdDateTime;
        @SerializedName("OrderNo")
        @Expose
        private String orderNo;
        @SerializedName("DeliverySpeed")
        @Expose
        private String deliverySpeed;
        @SerializedName("Distance")
        @Expose
        private String distance;
        @SerializedName("Notes")
        @Expose
        private String notes;
        @SerializedName("Vehicle")
        @Expose
        private String vehicle;
        @SerializedName("FoodReadyETA")
        @Expose
        private Object foodReadyETA;
        @SerializedName("Shipments")
        @Expose
        private java.util.List<Shipment> shipments;
        @SerializedName("PickupCompany")
        @Expose
        private String pickupCompany;
        @SerializedName("DropCompany")
        @Expose
        private String dropCompany;
        @SerializedName("RunId")
        @Expose
        private Integer runId;
        @SerializedName("DropSuburbCount")
        @Expose
        private Integer dropSuburbCount;
        @SerializedName("Price")
        @Expose
        private Double price;
        @SerializedName("Source")
        @Expose
        private String source;
        @SerializedName("CutOff")
        @Expose
        private Double cutOff;
        @SerializedName("CourierPrice")
        @Expose
        private Double courierPrice;
        @SerializedName("PricingBreakdowns")
        @Expose
        private PricingBreakdowns pricingBreakdowns;
        @SerializedName("DeliveryRequest")
        @Expose
        private Object deliveryRequest;
        @SerializedName("Courier")
        @Expose
        private Object courier;

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

        public Integer getBookingId() {
            return bookingId;
        }

        public void setBookingId(Integer bookingId) {
            this.bookingId = bookingId;
        }

        public String getCourierId() {
            return courierId;
        }

        public void setCourierId(String courierId) {
            this.courierId = courierId;
        }

        public Object getSenderId() {
            return senderId;
        }

        public void setSenderId(Object senderId) {
            this.senderId = senderId;
        }

        public String getOfferDateTime() {
            return offerDateTime;
        }

        public void setOfferDateTime(String offerDateTime) {
            this.offerDateTime = offerDateTime;
        }

        public Object getOfferAcceptedDateTime() {
            return offerAcceptedDateTime;
        }

        public void setOfferAcceptedDateTime(Object offerAcceptedDateTime) {
            this.offerAcceptedDateTime = offerAcceptedDateTime;
        }

        public Object getRejectedDateTime() {
            return rejectedDateTime;
        }

        public void setRejectedDateTime(Object rejectedDateTime) {
            this.rejectedDateTime = rejectedDateTime;
        }

        public Object getRejectionReason() {
            return rejectionReason;
        }

        public void setRejectionReason(Object rejectionReason) {
            this.rejectionReason = rejectionReason;
        }

        public Boolean getIsExpired() {
            return isExpired;
        }

        public void setIsExpired(Boolean isExpired) {
            this.isExpired = isExpired;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Object getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Object customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerCompany() {
            return customerCompany;
        }

        public void setCustomerCompany(String customerCompany) {
            this.customerCompany = customerCompany;
        }

        public String getCustomerContact() {
            return customerContact;
        }

        public void setCustomerContact(String customerContact) {
            this.customerContact = customerContact;
        }

        public String getBookingRefNo() {
            return bookingRefNo;
        }

        public void setBookingRefNo(String bookingRefNo) {
            this.bookingRefNo = bookingRefNo;
        }

        public String getPickupDateTime() {
            return pickupDateTime;
        }

        public void setPickupDateTime(String pickupDateTime) {
            this.pickupDateTime = pickupDateTime;
        }

        public String getPickupAddress() {
            return pickupAddress;
        }

        public void setPickupAddress(String pickupAddress) {
            this.pickupAddress = pickupAddress;
        }

        public String getPickupContactName() {
            return pickupContactName;
        }

        public void setPickupContactName(String pickupContactName) {
            this.pickupContactName = pickupContactName;
        }

        public String getPickupGPSX() {
            return pickupGPSX;
        }

        public void setPickupGPSX(String pickupGPSX) {
            this.pickupGPSX = pickupGPSX;
        }

        public String getPickupGPSY() {
            return pickupGPSY;
        }

        public void setPickupGPSY(String pickupGPSY) {
            this.pickupGPSY = pickupGPSY;
        }

        public String getPickupNotes() {
            return pickupNotes;
        }

        public void setPickupNotes(String pickupNotes) {
            this.pickupNotes = pickupNotes;
        }

        public String getPickupPhone() {
            return pickupPhone;
        }

        public void setPickupPhone(String pickupPhone) {
            this.pickupPhone = pickupPhone;
        }

        public String getPickupSuburb() {
            return pickupSuburb;
        }

        public void setPickupSuburb(String pickupSuburb) {
            this.pickupSuburb = pickupSuburb;
        }

        public String getDropDateTime() {
            return dropDateTime;
        }

        public void setDropDateTime(String dropDateTime) {
            this.dropDateTime = dropDateTime;
        }

        public String getDropAddress() {
            return dropAddress;
        }

        public void setDropAddress(String dropAddress) {
            this.dropAddress = dropAddress;
        }

        public String getDropContactName() {
            return dropContactName;
        }

        public void setDropContactName(String dropContactName) {
            this.dropContactName = dropContactName;
        }

        public String getDropGPSX() {
            return dropGPSX;
        }

        public void setDropGPSX(String dropGPSX) {
            this.dropGPSX = dropGPSX;
        }

        public String getDropGPSY() {
            return dropGPSY;
        }

        public void setDropGPSY(String dropGPSY) {
            this.dropGPSY = dropGPSY;
        }

        public String getDropNotes() {
            return dropNotes;
        }

        public void setDropNotes(String dropNotes) {
            this.dropNotes = dropNotes;
        }

        public String getDropPhone() {
            return dropPhone;
        }

        public void setDropPhone(String dropPhone) {
            this.dropPhone = dropPhone;
        }

        public String getDropSuburb() {
            return dropSuburb;
        }

        public void setDropSuburb(String dropSuburb) {
            this.dropSuburb = dropSuburb;
        }

        public String getRoutePolyline() {
            return routePolyline;
        }

        public void setRoutePolyline(String routePolyline) {
            this.routePolyline = routePolyline;
        }

        public String getCreatedDateTime() {
            return createdDateTime;
        }

        public void setCreatedDateTime(String createdDateTime) {
            this.createdDateTime = createdDateTime;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getDeliverySpeed() {
            return deliverySpeed;
        }

        public void setDeliverySpeed(String deliverySpeed) {
            this.deliverySpeed = deliverySpeed;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getVehicle() {
            return vehicle;
        }

        public void setVehicle(String vehicle) {
            this.vehicle = vehicle;
        }

        public Object getFoodReadyETA() {
            return foodReadyETA;
        }

        public void setFoodReadyETA(Object foodReadyETA) {
            this.foodReadyETA = foodReadyETA;
        }

        public java.util.List<Shipment> getShipments() {
            return shipments;
        }

        public void setShipments(java.util.List<Shipment> shipments) {
            this.shipments = shipments;
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

        public Integer getRunId() {
            return runId;
        }

        public void setRunId(Integer runId) {
            this.runId = runId;
        }

        public Integer getDropSuburbCount() {
            return dropSuburbCount;
        }

        public void setDropSuburbCount(Integer dropSuburbCount) {
            this.dropSuburbCount = dropSuburbCount;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public Double getCutOff() {
            return cutOff;
        }

        public void setCutOff(Double cutOff) {
            this.cutOff = cutOff;
        }

        public Double getCourierPrice() {
            return courierPrice;
        }

        public void setCourierPrice(Double courierPrice) {
            this.courierPrice = courierPrice;
        }

        public PricingBreakdowns getPricingBreakdowns() {
            return pricingBreakdowns;
        }

        public void setPricingBreakdowns(PricingBreakdowns pricingBreakdowns) {
            this.pricingBreakdowns = pricingBreakdowns;
        }

        public Object getDeliveryRequest() {
            return deliveryRequest;
        }

        public void setDeliveryRequest(Object deliveryRequest) {
            this.deliveryRequest = deliveryRequest;
        }

        public Object getCourier() {
            return courier;
        }

        public void setCourier(Object courier) {
            this.courier = courier;
        }

        public static class Shipment {

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
            private Double lengthCm;
            @SerializedName("WidthCm")
            @Expose
            private Double widthCm;
            @SerializedName("HeightCm")
            @Expose
            private Double heightCm;
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

            public Double getLengthCm() {
                return lengthCm;
            }

            public void setLengthCm(Double lengthCm) {
                this.lengthCm = lengthCm;
            }

            public Double getWidthCm() {
                return widthCm;
            }

            public void setWidthCm(Double widthCm) {
                this.widthCm = widthCm;
            }

            public Double getHeightCm() {
                return heightCm;
            }

            public void setHeightCm(Double heightCm) {
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

        public static class PricingBreakdowns {

            @SerializedName("$id")
            @Expose
            private String $id;
            @SerializedName("$type")
            @Expose
            private String $type;
            @SerializedName("courierPaymentExGst")
            @Expose
            private Double courierPaymentExGst;
            @SerializedName("zoom2uFeeExGst")
            @Expose
            private Double zoom2uFeeExGst;
            @SerializedName("bookingFeeExGst")
            @Expose
            private Double bookingFeeExGst;
            @SerializedName("gst")
            @Expose
            private Double gst;
            @SerializedName("totalPrice")
            @Expose
            private Double totalPrice;

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

            public Double getCourierPaymentExGst() {
                return courierPaymentExGst;
            }

            public void setCourierPaymentExGst(Double courierPaymentExGst) {
                this.courierPaymentExGst = courierPaymentExGst;
            }

            public Double getZoom2uFeeExGst() {
                return zoom2uFeeExGst;
            }

            public void setZoom2uFeeExGst(Double zoom2uFeeExGst) {
                this.zoom2uFeeExGst = zoom2uFeeExGst;
            }

            public Double getBookingFeeExGst() {
                return bookingFeeExGst;
            }

            public void setBookingFeeExGst(Double bookingFeeExGst) {
                this.bookingFeeExGst = bookingFeeExGst;
            }

            public Double getGst() {
                return gst;
            }

            public void setGst(Double gst) {
                this.gst = gst;
            }

            public Double getTotalPrice() {
                return totalPrice;
            }

            public void setTotalPrice(Double totalPrice) {
                this.totalPrice = totalPrice;
            }

        }
    }

}




