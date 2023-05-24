package com.zoom2u.utility.signaturefail;

import android.graphics.Bitmap;

/**
 * Created by Arun on 30/3/17.
 */

public class SignatureImg_DB_Model {

    private String bookingIdSignatureFailImg;
    private Bitmap signatureImgBitmap;

    public SignatureImg_DB_Model (String bookingIdSignatureFailImg, Bitmap signatureImgBitmap){
        this.bookingIdSignatureFailImg = bookingIdSignatureFailImg;
        this.signatureImgBitmap = signatureImgBitmap;
    }

    public String getBookingIdSignatureFailImg() {
        return bookingIdSignatureFailImg;
    }

    public Bitmap getSignatureImgBitmap() {
        return signatureImgBitmap;
    }
}
