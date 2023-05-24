package com.z2u.chat;

public class Chat {

    private String sender;
    private String message;
    private int receipt;
    private String timestamp;
    private String user;
    private String type;
    private String thumbnail;

	// Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    public Chat(String message, String sender, int receipt, String timestamp, String type) {
        this.message = message;
        this.sender = sender;
        this.receipt = receipt;
        this.timestamp = timestamp;
        if(type.equals("audio")){
            this.type=type;
        }else{
            this.user=type;
        }
    }

    public Chat(String message, String sender, int receipt, String timestamp, String type, String thumbnail) {
        this.message = message;
        this.sender = sender;
        this.receipt = receipt;
        this.timestamp = timestamp;
        this.type = type;
        this.thumbnail = thumbnail;
    }

    public Chat(String message, String sender, int receipt, String timestamp, String user, String type, String thumbnail) {
        this.message = message;
        this.sender = sender;
        this.receipt = receipt;
        this.timestamp = timestamp;
        this.user = user;
        this.type = type;
        this.thumbnail = thumbnail;
    }

    public int getReceipt() {
		return receipt;
	}

    public String getTimestamp() {
		return timestamp;
	}

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getUser() {
        return user;
    }

    public String getType() {
        return type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

}
