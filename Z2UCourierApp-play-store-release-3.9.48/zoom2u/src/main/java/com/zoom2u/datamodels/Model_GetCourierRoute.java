package com.zoom2u.datamodels;

import com.zoom2u.slidemenu.CourierRouteDetail;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Model_GetCourierRoute {

    private int bookingId;
    private double latitude;
    private double longitude;
    private String dropETA;
    private String dropOffAddress;
    private String status;
    private String dropDateTime;
    private String dropContactPerson;
    private boolean isRunningLate;
    private int markerCounter;
    private int totalPieceCount;
    private int pickedUpPieceCount;

    public Model_GetCourierRoute(JSONObject jObjOfCourierRoute, int markerCounter){
        try {
            this.bookingId = jObjOfCourierRoute.getInt("bookingId");
            if (!jObjOfCourierRoute.getString("dropLocation").equals("") && !jObjOfCourierRoute.getString("dropLocation").equals("null")) {
                String[] dropLocationDataArray = jObjOfCourierRoute.getString("dropLocation").split(",");
                this.latitude = Double.parseDouble(dropLocationDataArray[0]);
                this.longitude = Double.parseDouble(dropLocationDataArray[1]);
            }

            this.dropETA = jObjOfCourierRoute.getString("dropETA");
            this.dropOffAddress = jObjOfCourierRoute.getString("dropAddress");
            this.dropContactPerson = jObjOfCourierRoute.getString("dropContactPerson");
            this.dropDateTime = jObjOfCourierRoute.getString("dropDateTime");

            this.status = jObjOfCourierRoute.getString("status");
            this.totalPieceCount = jObjOfCourierRoute.getInt("totalPieceCount");
            this.pickedUpPieceCount = jObjOfCourierRoute.getInt("pickedUpPieceCount");
            this.markerCounter = markerCounter;

            if (!this.dropETA.equals("") && !this.dropETA.equals("null"))
                this.isRunningLate = checkForRunningLateDHL(this.dropETA);
            else if (!this.dropDateTime.equals("") && !this.dropDateTime.equals("null"))
                this.isRunningLate = checkForRunningLateDHL(this.dropDateTime);
            else
                this.isRunningLate = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Model_GetCourierRoute(JSONObject jObjOfCourierRoute, int markerCounter, int tempCount){
        try {
            this.bookingId = jObjOfCourierRoute.getInt("BookingId");

            if (jObjOfCourierRoute.getString("Status").equals("Accepted") || jObjOfCourierRoute.getString("Status").equals("On Route to Pickup")){
                if (!jObjOfCourierRoute.getString("PickupLocation").equals("") && !jObjOfCourierRoute.getString("PickupLocation").equals("null")) {
                    String[] dropLocationDataArray = jObjOfCourierRoute.getString("PickupLocation").split(",");
                    this.latitude = Double.parseDouble(dropLocationDataArray[0]);
                    this.longitude = Double.parseDouble(dropLocationDataArray[1]);
                }
                this.dropETA = jObjOfCourierRoute.getString("PickupETA");
                this.dropOffAddress = jObjOfCourierRoute.getString("PickupAddress");
                this.dropContactPerson = jObjOfCourierRoute.getString("PickupContactPerson");
                this.dropDateTime = jObjOfCourierRoute.getString("PickupDateTime");
            } else {
                if (!jObjOfCourierRoute.getString("DropLocation").equals("") && !jObjOfCourierRoute.getString("DropLocation").equals("null")) {
                    String[] dropLocationDataArray = jObjOfCourierRoute.getString("DropLocation").split(",");
                    this.latitude = Double.parseDouble(dropLocationDataArray[0]);
                    this.longitude = Double.parseDouble(dropLocationDataArray[1]);
                }

                this.dropETA = jObjOfCourierRoute.getString("DropETA");
                this.dropOffAddress = jObjOfCourierRoute.getString("DropAddress");
                this.dropContactPerson = jObjOfCourierRoute.getString("DropContactPerson");
                this.dropDateTime = jObjOfCourierRoute.getString("DropDateTime");
            }

            this.status = jObjOfCourierRoute.getString("Status");
            this.markerCounter = markerCounter;

            if (!this.dropETA.equals("") && !this.dropETA.equals("null"))
                this.isRunningLate = getCurrentTime(this.dropETA);
            else if (!this.dropDateTime.equals("") && !this.dropDateTime.equals("null"))
                this.isRunningLate = getCurrentTime(this.dropDateTime);
            else
                this.isRunningLate = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*********** Check for late or on-time
    private boolean getCurrentTime(String pickOrDropEtaTime){
        boolean isRunningLateFromETATime = false;
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentTime = format.format(calendar.getTime());
            Date dropETADateTime = format.parse(pickOrDropEtaTime);
            Date currentDateTime = format.parse(currentTime);

            if (currentDateTime.after(dropETADateTime))
                isRunningLateFromETATime = true;
            else
                isRunningLateFromETATime = false;

            calendar = null;
            format = null;
            dropETADateTime  = null;
            currentDateTime = null;
        } catch (Exception e) {
            e.printStackTrace();
            isRunningLateFromETATime = true;
        }
        return isRunningLateFromETATime;
    }

    //*********** Check for late or on-time
    private boolean checkForRunningLateDHL(String pickOrDropEtaTime){
        boolean isRunningLateFromETATime = false;
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            String currentTime = format.format(calendar.getTime());
            Date dropETADateTime = format.parse(pickOrDropEtaTime);
            Date currentDateTime = format.parse(currentTime);

            if (currentDateTime.after(dropETADateTime))
                isRunningLateFromETATime = true;
            else
                isRunningLateFromETATime = false;

            calendar = null;
            format = null;
            dropETADateTime  = null;
            currentDateTime = null;
        } catch (Exception e) {
            e.printStackTrace();
            isRunningLateFromETATime = true;
        }
        return isRunningLateFromETATime;
    }


    public int getMarkerCounter() {
        return markerCounter;
    }

    public int getBookingId() {
        return bookingId;
    }

    public boolean isRunningLate() {
        return isRunningLate;
    }

    public String getDropDateTime() {
        return dropDateTime;
    }

    public String getDropContactPerson() {
        return dropContactPerson;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDropETA() {
        return dropETA;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public String getStatus() {
        return status;
    }

}
