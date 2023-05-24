package com.zoom2u.offer_run_batch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mahendra Dabi on 09-08-2021.
 */
public class Customer implements Serializable {
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("contact")
    @Expose
    private String contact;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}
