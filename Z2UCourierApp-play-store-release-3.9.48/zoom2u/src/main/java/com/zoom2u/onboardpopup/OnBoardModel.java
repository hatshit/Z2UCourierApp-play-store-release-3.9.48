package com.zoom2u.onboardpopup;

public class OnBoardModel {
    int imageId;
    String heading;
    String msg;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public OnBoardModel(int imageId, String heading, String msg) {
        this.imageId = imageId;
        this.heading = heading;
        this.msg = msg;
    }
}
