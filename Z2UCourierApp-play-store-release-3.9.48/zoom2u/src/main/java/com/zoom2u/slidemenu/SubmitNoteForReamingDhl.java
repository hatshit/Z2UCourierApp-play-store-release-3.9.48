package com.zoom2u.slidemenu;

import android.os.Parcel;
import android.os.Parcelable;

import com.suggestprice_team.courier_team.Address_Model;
import com.suggestprice_team.courier_team.MyTeamList_Model;

public class SubmitNoteForReamingDhl implements Parcelable {

    private int runId;
    private String barcode;
    private String notes;
    private String remainingPickup;
    private String depotLocation;

    public SubmitNoteForReamingDhl(int runId, String barcode, String notes, String remainingPickup, String depotLocation) {
        this.runId = runId;
        this.barcode = barcode;
        this.notes = notes;
        this.remainingPickup = remainingPickup;
        this.depotLocation = depotLocation;
    }

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRemainingPickup() {
        return remainingPickup;
    }

    public void setRemainingPickup(String remainingPickup) {
        this.remainingPickup = remainingPickup;
    }

    public String getDepotLocation() {
        return depotLocation;
    }

    public void setDepotLocation(String depotLocation) {
        this.depotLocation = depotLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(runId);
        dest.writeString(barcode);
        dest.writeString(notes);
        dest.writeString(remainingPickup);
        dest.writeString(depotLocation);
    }

    public static final Parcelable.Creator<SubmitNoteForReamingDhl> CREATOR  = new Parcelable.Creator<SubmitNoteForReamingDhl>() {
        public SubmitNoteForReamingDhl createFromParcel(Parcel in) {
            return new SubmitNoteForReamingDhl(in);
        }

        public SubmitNoteForReamingDhl[] newArray(int size) {
            return new SubmitNoteForReamingDhl[size];
        }
    };

    private SubmitNoteForReamingDhl(Parcel in) {
        runId = in.readInt();
        barcode = in.readString();
        notes = in.readString();
        remainingPickup = in.readString();
        depotLocation = in.readString();

    }
}
