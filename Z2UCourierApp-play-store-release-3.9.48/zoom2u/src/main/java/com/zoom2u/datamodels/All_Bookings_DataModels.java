package com.zoom2u.datamodels;

import android.os.Parcel;
import android.os.Parcelable;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class All_Bookings_DataModels implements Parcelable, DHL_SectionInterface{

	private int BookingId;
	private int OfferId;
	private String CustomerId;
	private boolean isNewCustomer;
	private boolean IsAutoReturn;
	private String CustomerName;
	private String CustomerCompany;
	private String CustomerContact;
	private String BookingRefNo;
	private String PickupDateTime;
	private String Pick_Address;
	private String Pick_ContactName;
	private String Pick_GPSX;
	private String Pick_GPSY;
	private String Pick_Notes;
	private String Pick_Phone;
	private String Pick_Company;
	private String Pick_StreetNo;
	private String Pick_StreetName;
	private String Pick_Suburb;
	private String DropDateTime;
	private String Drop_Address;
	private String Drop_ContactName;
	private String Drop_GPSX;
	private String Drop_GPSY;
	private String Drop_Notes;
	private String Drop_Phone;
	private String Drop_Company;
	private String Drop_StreetNo;
	private String Drop_StreetName;
	private String Drop_Suburb;
	private String CreatedDateTime;
	private String DeliverySpeed;
	private String Distance;
	private String Notes;
	private String Package;
	private double Price;
	private String Source;
	private String Vehicle;
	private String Status;
	private String PickupETA;
	private String DropETA;
	private String DropActual;
	private String DropPerson;
	private String PickupActual;
	private String PickupPerson;
	private String DropSignature;
	private String PickupSignature;
	private String pickupCompanyName;
	private String DistanceFromCurrentLocation;
	private String OrderNumber;
	private boolean IsATL;
	private String ATLLeaveAt;
	private String ATLReceiverName;
	private String ATLDoorCode;
	private String ATLInstructions;
	private boolean authorityToLeavePermitted;
	private ArrayList<HashMap<String, Object>> shipmentsArray;
	private int isCakeAndFlower;
	private ArrayList<String> piecesArray;
	private HashMap<String, Boolean> piecesScannedMap;
	private int pickedUpPieceCount;
	private int totalPieceCount;
	private String firstDropAttemptWasLate;
	private String dropIdentityType;
	private String dropIdentityNumber;

	private String courierId;
	private String courierName;
	private String courierMobile;

	//********  12-July-2018 For New booking list suggested price submit by courier **********
//	private boolean isSuggestedPrice;
	private String courierSuggestedPrice;
	//******************************  ************************

	private int runId;
	private int runNumber;
	private int dropSuburbCount;
	private int runTotalDeliveryCount;
	private int runCompletedDeliveryCount;
	private String runType;			//********** SMARTSORT if Non-DHL delivery run (Runs in today tab)
									//********** DHL if DHL delivery run and
									//********** "" for Normal delivery

	private boolean doesAlcoholDeliveries;
	private int lateReasonId;
	private boolean isTTDReasonForAlcoholDelivery;

	private String routePolyline;
	private boolean atlNoContact;

//	private PricingBreakdown_Model pricingBreakdown_model;

	public String getFirstDropAttemptWasLate() {
		return firstDropAttemptWasLate;
	}

	public void setFirstDropAttemptWasLate(String firstDropAttemptWasLate) {
		this.firstDropAttemptWasLate = firstDropAttemptWasLate;
	}

	public boolean isAuthorityToLeavePermitted() {
		return authorityToLeavePermitted;
	}

	public void setAuthorityToLeavePermitted(boolean authorityToLeavePermitted) {
		this.authorityToLeavePermitted = authorityToLeavePermitted;
	}

	public ArrayList<String> getPiecesArray() {
		return piecesArray;
	}

	public void setPiecesArray(ArrayList<String> piecesArray) {
		this.piecesArray = piecesArray;
	}

	public HashMap<String, Boolean> getPiecesScannedMap() {
		return piecesScannedMap;
	}

	public void setPiecesScannedMap(HashMap<String, Boolean> piecesScannedMap) {
		this.piecesScannedMap = piecesScannedMap;
	}


	public int getTotalPieceCount() {
		return totalPieceCount;
	}

	public void setTotalPieceCount(int totalPieceCount) {
		this.totalPieceCount = totalPieceCount;
	}

	public int getPickedUpPieceCount() {
		return pickedUpPieceCount;
	}

	public void setPickedUpPieceCount(int pickedUpPieceCount) {
		this.pickedUpPieceCount = pickedUpPieceCount;
	}

	public ArrayList<HashMap<String, Object>> getShipmentsArray() {
		return shipmentsArray;
	}

	public void setShipmentsArray(ArrayList<HashMap<String, Object>> shipmentsArray) {
		this.shipmentsArray = shipmentsArray;
	}
	
	public All_Bookings_DataModels() {
		super();
	}

	public String getOrderNumber() {
		return OrderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		OrderNumber = orderNumber;
	}

	public String getPickupDateTime() {
		return PickupDateTime;
	}

	public void setPickupDateTime(String pickupDateTime) {
		PickupDateTime = pickupDateTime;
	}

	public String getPick_Address() {
		return Pick_Address;
	}

	public void setPick_Address(String pick_Address) {
		Pick_Address = pick_Address;
	}

	public String getPick_ContactName() {
		return Pick_ContactName;
	}

	public void setPick_ContactName(String pick_ContactName) {
		Pick_ContactName = pick_ContactName;
	}

	public String getPick_GPSX() {
		return Pick_GPSX;
	}

	public void setPick_GPSX(String pick_GPSX) {
		Pick_GPSX = pick_GPSX;
	}

	public String getPick_GPSY() {
		return Pick_GPSY;
	}

	public void setPick_GPSY(String pick_GPSY) {
		Pick_GPSY = pick_GPSY;
	}

	public String getPick_Notes() {
		return Pick_Notes;
	}

	public void setPick_Notes(String pick_Notes) {
		Pick_Notes = pick_Notes;
	}

	public String getPick_Phone() {
		return Pick_Phone;
	}

	public void setPick_Phone(String pick_Phone) {
		Pick_Phone = pick_Phone;
	}

	public String getPick_Suburb() {
		return Pick_Suburb;
	}

	public void setPick_Suburb(String pick_Suburb) {
		Pick_Suburb = pick_Suburb;
	}


	public String getPick_StreetNo() {
		return Pick_StreetNo;
	}

	public void setPick_StreetNo(String pick_StreetNo) {
		Pick_StreetNo = pick_StreetNo;
	}

	public String getPick_StreetName() {
		return Pick_StreetName;
	}

	public void setPick_StreetName(String pick_StreetName) {
		Pick_StreetName = pick_StreetName;
	}

	public String getDrop_StreetNo() {
		return Drop_StreetNo;
	}

	public void setDrop_StreetNo(String drop_StreetNo) {
		Drop_StreetNo = drop_StreetNo;
	}

	public String getDrop_StreetName() {
		return Drop_StreetName;
	}

	public void setDrop_StreetName(String drop_StreetName) {
		Drop_StreetName = drop_StreetName;
	}

	public String getDropDateTime() {
		return DropDateTime;
	}

	public void setDropDateTime(String dropDateTime) {
		DropDateTime = dropDateTime;
	}

	public String getDrop_Address() {
		return Drop_Address;
	}

	public void setDrop_Address(String drop_Address) {
		Drop_Address = drop_Address;
	}

	public String getDrop_ContactName() {
		return Drop_ContactName;
	}

	public void setDrop_ContactName(String drop_ContactName) {
		Drop_ContactName = drop_ContactName;
	}

	public String getDrop_GPSX() {
		return Drop_GPSX;
	}

	public void setDrop_GPSX(String drop_GPSX) {
		Drop_GPSX = drop_GPSX;
	}

	public String getDrop_GPSY() {
		return Drop_GPSY;
	}

	public void setDrop_GPSY(String drop_GPSY) {
		Drop_GPSY = drop_GPSY;
	}

	public String getDrop_Notes() {
		return Drop_Notes;
	}

	public void setDrop_Notes(String drop_Notes) {
		Drop_Notes = drop_Notes;
	}

	public String getDrop_Phone() {
		return Drop_Phone;
	}

	public void setDrop_Phone(String drop_Phone) {
		Drop_Phone = drop_Phone;
	}

	public String getDrop_Suburb() {
		return Drop_Suburb;
	}

	public void setDrop_Suburb(String drop_Suburb) {
		Drop_Suburb = drop_Suburb;
	}

	public int getBookingId() {
		return BookingId;
	}

	public void setBookingId(int bookingId) {
		BookingId = bookingId;
	}

	public int getOfferId() {
		return OfferId;
	}

	public void setOfferId(int offerId) {
		OfferId = offerId;
	}

	public String getCreatedDateTime() {
		return CreatedDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		CreatedDateTime = createdDateTime;
	}

	public String getCustomerId() {
		return CustomerId;
	}

	public void setCustomerId(String customerId) {
		CustomerId = customerId;
	}

	public String getDeliverySpeed() {
		return DeliverySpeed;
	}

	public void setDeliverySpeed(String deliverySpeed) {
		DeliverySpeed = deliverySpeed;
	}

	public String getDistance() {
		return Distance;
	}

	public void setDistance(String distance) {
		Distance = distance;
	}

	public String getNotes() {
		return Notes;
	}

	public void setNotes(String notes) {
		Notes = notes;
	}

	public String getPackage() {
		return Package;
	}

	public void setPackage(String package1) {
		Package = package1;
	}
	public double getPrice() {
		return Price;
	}

	public void setPrice(double price) {
		Price = price;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public String getVehicle() {
		return Vehicle;
	}

	public void setVehicle(String vehicle) {
		Vehicle = vehicle;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getPickupETA() {
		return PickupETA;
	}

	public void setPickupETA(String pickupETA) {
		PickupETA = pickupETA;
	}

	public String getDropETA() {
		return DropETA;
	}

	public void setDropETA(String dropETA) {
		DropETA = dropETA;
	}

	public String getDropActual() {
		return DropActual;
	}

	public void setDropActual(String dropActual) {
		DropActual = dropActual;
	}

	public String getDropPerson() {
		return DropPerson;
	}

	public void setDropPerson(String dropPerson) {
		DropPerson = dropPerson;
	}

	public String getPickupActual() {
		return PickupActual;
	}

	public void setPickupActual(String pickupActual) {
		PickupActual = pickupActual;
	}

	public String getPickupPerson() {
		return PickupPerson;
	}

	public void setPickupPerson(String pickupPerson) {
		PickupPerson = pickupPerson;
	}

	public String getDropSignature() {
		return DropSignature;
	}

	public void setDropSignature(String dropSignature) {
		DropSignature = dropSignature;
	}

	public String getPickupSignature() {
		return PickupSignature;
	}

	public void setPickupSignature(String pickupSignature) {
		PickupSignature = pickupSignature;
	}
	
	public String getDistanceFromCurrentLocation() {
		return DistanceFromCurrentLocation;
	}

	public void setDistanceFromCurrentLocation(String distanceFromCurrentLocation) {
		DistanceFromCurrentLocation = distanceFromCurrentLocation;
	}

//	public PricingBreakdown_Model getPricingBreakdown_model() {
//		return pricingBreakdown_model;
//	}
//
//	public void setPricingBreakdown_model(JSONObject jObjOfPricing) {
//		this.pricingBreakdown_model = new PricingBreakdown_Model(jObjOfPricing);
//	}

	@Override
	public int describeContents() {
		return 0;
	}

	public boolean isNewCustomer() {
		return isNewCustomer;
	}
	public boolean IsAutoReturn() {
		return IsAutoReturn;
	}

	public void setNewCustomer(boolean newCustomer) {
		isNewCustomer = newCustomer;
	}
	public void setAutoReturn(boolean autoReturn) {
		IsAutoReturn = autoReturn;
	}

	public String getPickupCompanyName() {
		return pickupCompanyName;
	}

	public void setPickupCompanyName(String pickupCompanyName) {
		this.pickupCompanyName = pickupCompanyName;
	}

	public boolean isATL() {
		return IsATL;
	}

	public void setATL(boolean ATL) {
		IsATL = ATL;
	}

	public String getATLLeaveAt() {
		return ATLLeaveAt;
	}

	public void setATLLeaveAt(String ATLLeaveAt) {
		this.ATLLeaveAt = ATLLeaveAt;
	}

	public String getATLReceiverName() {
		return ATLReceiverName;
	}

	public void setATLReceiverName(String ATLReceiverName) {
		this.ATLReceiverName = ATLReceiverName;
	}

	public String getATLDoorCode() {
		return ATLDoorCode;
	}

	public void setATLDoorCode(String ATLDoorCode) {
		this.ATLDoorCode = ATLDoorCode;
	}

	public String getATLInstructions() {
		return ATLInstructions;
	}

	public void setATLInstructions(String ATLInstructions) {
		this.ATLInstructions = ATLInstructions;
	}


	public int getIsCakeAndFlower() {
		return isCakeAndFlower;
	}

	public void setIsCakeAndFlower(int isCakeAndFlower) {
		this.isCakeAndFlower = isCakeAndFlower;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getCustomerCompany() {
		return CustomerCompany;
	}

	public void setCustomerCompany(String customerCompany) {
		CustomerCompany = customerCompany;
	}

	public String getCustomerContact() {
		return CustomerContact;
	}

	public void setCustomerContact(String customerContact) {
		CustomerContact = customerContact;
	}

	public String getBookingRefNo() {
		return BookingRefNo;
	}

	public void setBookingRefNo(String bookingRefNo) {
		BookingRefNo = bookingRefNo;
	}

	public String getDropIdentityType() {
		return dropIdentityType;
	}

	public void setDropIdentityType(String dropIdentityType) {
		this.dropIdentityType = dropIdentityType;
	}

	public String getDropIdentityNumber() {
		return dropIdentityNumber;
	}

	public void setDropIdentityNumber(String dropIdentityNumber) {
		this.dropIdentityNumber = dropIdentityNumber;
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

	public String getCourierMobile() {
		return courierMobile;
	}

	public void setCourierMobile(String courierMobile) {
		this.courierMobile = courierMobile;
	}

	//	public boolean isSuggestedPrice() {
//		return isSuggestedPrice;
//	}
//
//	public void setSuggestedPrice(boolean suggestedPrice) {
//		isSuggestedPrice = suggestedPrice;
//	}

	public String getCourierSuggestedPrice() {
		return courierSuggestedPrice;
	}

	public void setCourierSuggestedPrice(String courierSuggestedPrice) {
		this.courierSuggestedPrice = courierSuggestedPrice;
	}

	public int getRunId() {
		return runId;
	}

	public void setRunId(int runId) {
		this.runId = runId;
	}

	public int getDropSuburbCount() {
		return dropSuburbCount;
	}

	public void setDropSuburbCount(int dropSuburbCount) {
		this.dropSuburbCount = dropSuburbCount;
	}

	public int getRunTotalDeliveryCount() {
		return runTotalDeliveryCount;
	}

	public void setRunTotalDeliveryCount(int runTotalDeliveryCount) {
		this.runTotalDeliveryCount = runTotalDeliveryCount;
	}

	public int getRunCompletedDeliveryCount() {
		return runCompletedDeliveryCount;
	}

	public void setRunCompletedDeliveryCount(int runCompletedDeliveryCount) {
		this.runCompletedDeliveryCount = runCompletedDeliveryCount;
	}

	public String getRunType() {
		return runType;
	}

	public void setRunType(String runType) {
		this.runType = runType;
	}

	public int getRunNumber() {
		return runNumber;
	}

	public void setRunNumber(int runNumber) {
		this.runNumber = runNumber;
	}

	public boolean isDoesAlcoholDeliveries() {
		return doesAlcoholDeliveries;
	}

	public void setDoesAlcoholDeliveries(boolean doesAlcoholDeliveries) {
		this.doesAlcoholDeliveries = doesAlcoholDeliveries;
	}

	public boolean isTTDReasonForAlcoholDelivery() {
		return isTTDReasonForAlcoholDelivery;
	}

	public void setTTDReasonForAlcoholDelivery(boolean TTDReasonForAlcoholDelivery) {
		isTTDReasonForAlcoholDelivery = TTDReasonForAlcoholDelivery;
	}

	public int getLateReasonId() {
		return lateReasonId;
	}

	public void setLateReasonId(int lateReasonId) {
		this.lateReasonId = lateReasonId;
	}

	public String getRoutePolyline() {
		return routePolyline;
	}

	public void setRoutePolyline(String routePolyline) {
		this.routePolyline = routePolyline;
	}

	public boolean getNoContactDelivery() {
		return atlNoContact;
	}

	public void setNoContactDelivery(boolean noContactDelivery) {
		this.atlNoContact = noContactDelivery;
	}

	public String getPick_Company() {
		return Pick_Company;
	}

	public void setPick_Company(String pick_Company) {
		Pick_Company = pick_Company;
	}

	public String getDrop_Company() {
		return Drop_Company;
	}

	public void setDrop_Company(String drop_Company) {
		Drop_Company = drop_Company;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(PickupDateTime);
		dest.writeString(Pick_Address);
		dest.writeString(Pick_ContactName);
		dest.writeString(Pick_GPSX);
		dest.writeString(Pick_GPSY);
		dest.writeString(Pick_Notes);
		dest.writeString(Pick_Phone);
		dest.writeString(Pick_StreetNo);
		dest.writeString(Pick_StreetName);
		dest.writeString(Pick_Suburb);
		dest.writeString(DropDateTime);
		dest.writeString(Drop_Address);
		dest.writeString(Drop_ContactName);
		dest.writeString(Drop_GPSX);
		dest.writeString(Drop_GPSY);
		dest.writeString(Drop_Notes);
		dest.writeString(Drop_Phone);
		dest.writeString(Drop_StreetNo);
		dest.writeString(Drop_StreetName);
		dest.writeString(Drop_Suburb);
		dest.writeInt(BookingId);
		dest.writeString(CreatedDateTime);
		dest.writeString(CustomerId);
		dest.writeString(DeliverySpeed);
		dest.writeString(Distance);
		dest.writeString(Notes);
		dest.writeString(Package);
		dest.writeDouble(Price);
		dest.writeString(Source);
		dest.writeString(Vehicle);
		dest.writeString(Status);
		dest.writeString(PickupETA);
		dest.writeString(DropETA);
		dest.writeString(DropActual);
		dest.writeString(DropPerson);
		dest.writeString(PickupActual);
		dest.writeString(PickupPerson);
		dest.writeString(DropSignature);
		dest.writeString(PickupSignature);
		dest.writeString(pickupCompanyName);
		dest.writeString(DistanceFromCurrentLocation);
		dest.writeString(OrderNumber);
		dest.writeByte((byte) (isNewCustomer ? 1 : 0));
		dest.writeByte((byte) (IsAutoReturn ? 1 : 0));
		dest.writeByte((byte) (IsATL ? 1 : 0));
		dest.writeString(ATLLeaveAt);
		dest.writeString(ATLReceiverName);
		dest.writeString(ATLDoorCode);
		dest.writeString(ATLInstructions);
		dest.writeByte((byte) (authorityToLeavePermitted ? 1 : 0));
		dest.writeInt(isCakeAndFlower);
		dest.writeString(CustomerName);
		dest.writeString(CustomerCompany);
		dest.writeString(CustomerContact);
		dest.writeString(BookingRefNo);
		dest.writeList(shipmentsArray);
		dest.writeList(piecesArray);
		dest.writeMap(piecesScannedMap);
		dest.writeInt(totalPieceCount);
		dest.writeInt(pickedUpPieceCount);
		dest.writeString(firstDropAttemptWasLate);
		dest.writeString(dropIdentityType);
		dest.writeString(dropIdentityNumber);

		//********* For courier suggested price 12-July-2018 *********
	//	dest.writeByte((byte) (isSuggestedPrice ? 1 : 0));
		dest.writeString(courierSuggestedPrice);
		dest.writeString(courierId);
		dest.writeString(courierName);
		dest.writeString(courierMobile);

		dest.writeInt(runId);
		dest.writeInt(runNumber);
		dest.writeInt(dropSuburbCount);
		dest.writeInt(runTotalDeliveryCount);
		dest.writeInt(runCompletedDeliveryCount);
		dest.writeString(runType);

		dest.writeByte((byte) (doesAlcoholDeliveries ? 1 : 0));
		dest.writeInt(lateReasonId);
		dest.writeByte((byte) (isTTDReasonForAlcoholDelivery ? 1 : 0));
	//	dest.writeParcelable(pricingBreakdown_model, 0);
		dest.writeString(routePolyline);
		dest.writeString(Pick_Company);
		dest.writeString(Drop_Company);
		dest.writeByte((byte) (atlNoContact ? 1 : 0));
	}
	
	 public static final Parcelable.Creator<All_Bookings_DataModels> CREATOR  = new Parcelable.Creator<All_Bookings_DataModels>() {
		 public All_Bookings_DataModels createFromParcel(Parcel in) {
		     return new All_Bookings_DataModels(in);
		 }

		 public All_Bookings_DataModels[] newArray(int size) {
		     return new All_Bookings_DataModels[size];
		 }
	};

	private All_Bookings_DataModels(Parcel in) {
		PickupDateTime = in.readString();
		Pick_Address  = in.readString();
		Pick_ContactName = in.readString();
		Pick_GPSX = in.readString();
		Pick_GPSY = in.readString();
		Pick_Notes = in.readString();
		Pick_Phone = in.readString();
		Pick_StreetNo = in.readString();
		Pick_StreetName = in.readString();
		Pick_Suburb = in.readString();
		DropDateTime = in.readString();
		Drop_Address = in.readString();
		Drop_ContactName = in.readString();
		Drop_GPSX = in.readString();
		Drop_GPSY = in.readString();
		Drop_Notes = in.readString();
		Drop_Phone = in.readString();
		Drop_StreetNo = in.readString();
		Drop_StreetName = in.readString();
		Drop_Suburb = in.readString();
		BookingId = in.readInt();
		CreatedDateTime = in.readString();
		CustomerId = in.readString();
		DeliverySpeed = in.readString();
		Distance = in.readString();
		Notes = in.readString();
		Package = in.readString();
		Price = in.readDouble();
		Source = in.readString();
		Vehicle = in.readString();
		Status = in.readString();
		PickupETA = in.readString();
		DropETA = in.readString();
		DropActual = in.readString();
		DropPerson = in.readString();
		PickupActual = in.readString();
		PickupPerson = in.readString();
		DropSignature = in.readString();
		PickupSignature = in.readString();
		pickupCompanyName = in.readString();
		DistanceFromCurrentLocation = in.readString();
		OrderNumber = in.readString();
		isNewCustomer = in.readByte() != 0;
		IsAutoReturn = in.readByte() != 0;
		IsATL = in.readByte() != 0;
		ATLLeaveAt = in.readString();
		ATLReceiverName = in.readString();
		ATLDoorCode = in.readString();
		ATLInstructions = in.readString();
		authorityToLeavePermitted = in.readByte() != 0;
		isCakeAndFlower = in.readInt();
		CustomerName = in.readString();
		CustomerCompany = in.readString();
		CustomerContact = in.readString();
		BookingRefNo = in.readString();
		shipmentsArray = in.readArrayList(HashMap.class.getClassLoader());
		piecesArray = in.readArrayList(String.class.getClassLoader());
		piecesScannedMap = in.readHashMap(Boolean.class.getClassLoader());
		totalPieceCount = in.readInt();
		pickedUpPieceCount = in.readInt();
		firstDropAttemptWasLate = in.readString();
		dropIdentityType = in.readString();
		dropIdentityNumber = in.readString();

		//********* For courier suggested price 12-July-2018 *********
	//	isSuggestedPrice = in.readByte() != 0;
		courierSuggestedPrice = in.readString();
		courierId = in.readString();
		courierName = in.readString();
		courierMobile = in.readString();

		runId = in.readInt();
		runNumber = in.readInt();
		dropSuburbCount = in.readInt();
		runTotalDeliveryCount = in.readInt();
		runCompletedDeliveryCount = in.readInt();
		runType = in.readString();

		doesAlcoholDeliveries = in.readByte() != 0;
		lateReasonId = in.readInt();
		isTTDReasonForAlcoholDelivery = in.readByte() != 0;
	//	pricingBreakdown_model = in.readParcelable(PricingBreakdown_Model.class.getClassLoader());
		routePolyline = in.readString();
		Pick_Company = in.readString();
		Drop_Company = in.readString();
		atlNoContact = in.readByte() != 0;
	}

	@Override
	public boolean isSection() {
		return false;
	}

	@Override
	public boolean isRouteSection() {
		return false;
	}

	@Override
	public String toString() {
		return "All_Bookings_DataModels{" +
				"BookingId=" + BookingId +
				", CustomerId='" + CustomerId + '\'' +
				", isNewCustomer='" + isNewCustomer + '\'' +
				", IsAutoReturn='" + IsAutoReturn + '\'' +
				", CustomerName='" + CustomerName + '\'' +
				", CustomerCompany='" + CustomerCompany + '\'' +
				", CustomerContact='" + CustomerContact + '\'' +
				", BookingRefNo=" + BookingRefNo +"PickupDateTime=" + PickupDateTime +
				", Pick_Address='" + Pick_Address + '\'' +
				", Pick_ContactName='" + Pick_ContactName + '\'' +
				", Pick_GPSX='" + Pick_GPSX + '\'' +
				", Pick_GPSY='" + Pick_GPSY + '\'' +
				", Pick_Notes='" + Pick_Notes + '\'' +
				", Pick_Phone=" + Pick_Phone +", Pick_StreetNo=" + Pick_StreetNo +"Pick_StreetName=" + Pick_StreetName +"Pick_Suburb=" + Pick_Suburb +
				", DropDateTime='" + DropDateTime + '\'' +
				", Drop_Address='" + Drop_Address + '\'' +
				", Drop_ContactName='" + Drop_ContactName + '\'' +
				", Drop_GPSX='" + Drop_GPSX + '\'' +
				", Drop_GPSY='" + Drop_GPSY + '\'' +
				", Drop_Notes=" + Drop_Notes +"Drop_Phone=" + Drop_Phone +", Drop_StreetNo=" + Drop_StreetNo +"Drop_StreetName=" + Drop_StreetName +
		", Drop_Suburb='" + Drop_Suburb + '\'' +
				", CreatedDateTime='" + CreatedDateTime + '\'' +
				", DeliverySpeed='" + DeliverySpeed + '\'' +
				", Distance='" + Distance + '\'' +
				", Notes='" + Notes + '\'' +
				", Package=" + Package +"Price=" + Price +
				", Source='" + Source + '\'' +
				", Vehicle='" + Vehicle + '\'' +
				", Status='" + Status + '\'' +
				", PickupETA='" + PickupETA + '\'' +
				", DropETA='" + DropETA + '\'' +
				", DropActual=" + DropActual +"DropPerson=" + DropPerson +
				", PickupActual='" + PickupActual + '\'' +
				", PickupPerson='" + PickupPerson + '\'' +
				", DropSignature='" + DropSignature + '\'' +
				", PickupSignature='" + PickupSignature + '\'' +
				", pickupCompanyName='" + pickupCompanyName + '\'' +
				", DistanceFromCurrentLocation=" + DistanceFromCurrentLocation +"OrderNumber=" + OrderNumber +
				", IsATL='" + IsATL + '\'' +
				", ATLLeaveAt='" + ATLLeaveAt + '\'' +
				", ATLReceiverName='" + ATLReceiverName + '\'' +
				", ATLDoorCode='" + ATLDoorCode + '\'' +
				", ATLInstructions=" + ATLInstructions +", authorityToLeavePermitted='" + authorityToLeavePermitted + '\'' +
				", shipmentsArray='" + shipmentsArray + '\'' +
				", isCakeAndFlower=" + isCakeAndFlower +", piecesArray=" + piecesArray+", piecesScannedMap=" + piecesScannedMap
				+", totalPieceCount='" + totalPieceCount + '\'' +
				", pickedUpPieceCount=" + pickedUpPieceCount +
				", firstDropAttemptWasLate=" + firstDropAttemptWasLate +
				", dropIdentityType=" + dropIdentityType +
				", dropIdentityNumber=" + dropIdentityNumber +
		//		", isSuggestedPrice=" + isSuggestedPrice +
				", courierSuggestedPrice=" + courierSuggestedPrice +
				", courierId=" + courierId +
				", courierName=" + courierName +
				", courierMobile=" + courierMobile +
				", runId=" + runId +
				", runNumber=" + runNumber +
				", dropSuburbCount=" + dropSuburbCount +
				", runTotalDeliveryCount=" + runTotalDeliveryCount +
				", runCompletedDeliveryCount=" + runCompletedDeliveryCount +
				", runType=" + runType +
				", doesAlcoholDeliveries="+ doesAlcoholDeliveries +
				", lateReasonId="+ lateReasonId +
				", isTTDReasonForAlcoholDelivery="+ isTTDReasonForAlcoholDelivery +
				", routePolyline="+ routePolyline +
				", Pick_Company="+ Pick_Company +
				", Drop_Company="+ Drop_Company +
				", atlNoContact="+ atlNoContact +
				'}';
	}
}
