package com.zoom2u.datamodels;

public class PreferenceImgUpload {

	private String bookingIdPP_DP;
	private String bookingIdPS_DS;
    private String pickDropPkgImgPath;
    private String signatureImgPath;
    private int isSignatureImageFromDB;
 
    public PreferenceImgUpload() {
        super();
    }
 
    public PreferenceImgUpload(String bookingIdPP_DP, String bookingIdPS_DS, String pickDropPkgImgPath, String signatureImgPath, int isSignatureImageFromDB) {
        super();
        this.bookingIdPP_DP = bookingIdPP_DP;
        this.bookingIdPS_DS = bookingIdPS_DS;
        this.pickDropPkgImgPath = pickDropPkgImgPath;
        this.signatureImgPath = signatureImgPath;
        this.isSignatureImageFromDB = isSignatureImageFromDB;
    }
    public String getBookingIdPP_DP() {
        return bookingIdPP_DP;
    }
 
    public void setBookingIdPP_DP(String bookingIdPP_DP) {
        this.bookingIdPP_DP = bookingIdPP_DP;
    }
    
    public String getBookingIdPS_DS() {
		return bookingIdPS_DS;
	}

	public void setBookingIdPS_DS(String bookingIdPS_DS) {
		this.bookingIdPS_DS = bookingIdPS_DS;
	}    
 
    public String getPickDropPkgImgPath() {
        return pickDropPkgImgPath;
    }

    public int getIsSignatureImageFromDB() {
        return isSignatureImageFromDB;
    }

    public void setIsSignatureImageFromDB(int isSignatureImageFromDB) {
        this.isSignatureImageFromDB = isSignatureImageFromDB;
    }

    public void setPickDropPkgImgPath(String pickDropPkgImgPath) {

        this.pickDropPkgImgPath = pickDropPkgImgPath;
    }
 
    public String getSignatureImgPath() {
        return signatureImgPath;
    }
 
    public void setSignatureImgPath(String signatureImgPath) {
        this.signatureImgPath = signatureImgPath;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PreferenceImgUpload other = (PreferenceImgUpload) obj;
        if (bookingIdPP_DP != other.bookingIdPP_DP)
            return false;
        return true;
    }
 
    @Override
    public String toString() {
        return "PreferenceImgUpload [bookingIdPP_DP=" + bookingIdPP_DP + ",bookingIdPS_DS=" + bookingIdPS_DS + ", pickDropPkgImgPath=" + pickDropPkgImgPath + ", signatureImgPath="
                + signatureImgPath + ", isSignatureImageFromDB=" + isSignatureImageFromDB + "]";
    }
}
