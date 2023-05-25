package com.suggestprice_team.courier_team.community;

public class Communitymemberlist {

    String Name;
    String Mobile;
    String Email;

    public Communitymemberlist(String name, String mobile, String email) {
        Name = name;
        Mobile = mobile;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
