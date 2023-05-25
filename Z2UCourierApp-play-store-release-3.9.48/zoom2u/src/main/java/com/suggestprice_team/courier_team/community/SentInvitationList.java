package com.suggestprice_team.courier_team.community;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SentInvitationList {

    @SerializedName("$id")
    @Expose
    private String $id;
    @SerializedName("$type")
    @Expose
    private String $type;
    @SerializedName("list")
    @Expose
    private List<Result> result;
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("Exception")
    @Expose
    private Object exception;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("IsCanceled")
    @Expose
    private Boolean isCanceled;
    @SerializedName("IsCompleted")
    @Expose
    private Boolean isCompleted;
    @SerializedName("CreationOptions")
    @Expose
    private String creationOptions;
    @SerializedName("AsyncState")
    @Expose
    private Object asyncState;
    @SerializedName("IsFaulted")
    @Expose
    private Boolean isFaulted;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getException() {
        return exception;
    }

    public void setException(Object exception) {
        this.exception = exception;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(Boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getCreationOptions() {
        return creationOptions;
    }

    public void setCreationOptions(String creationOptions) {
        this.creationOptions = creationOptions;
    }

    public Object getAsyncState() {
        return asyncState;
    }

    public void setAsyncState(Object asyncState) {
        this.asyncState = asyncState;
    }

    public Boolean getIsFaulted() {
        return isFaulted;
    }

    public void setIsFaulted(Boolean isFaulted) {
        this.isFaulted = isFaulted;
    }

    public static class Result {

        @SerializedName("$id")
        @Expose
        private String $id;
        @SerializedName("$type")
        @Expose
        private String $type;
        @SerializedName("Id")
        @Expose
        private Integer id;
        @SerializedName("CourierId")
        @Expose
        private String courierId;
        @SerializedName("UserId")
        @Expose
        private String userId;
        @SerializedName("CreatedDateTime")
        @Expose
        private Object createdDateTime;
        @SerializedName("NickName")
        @Expose
        private String nickName;
        @SerializedName("FirstName")
        @Expose
        private String firstName;
        @SerializedName("LastName")
        @Expose
        private String lastName;
        @SerializedName("Mobile")
        @Expose
        private Object mobile;
        @SerializedName("Email")
        @Expose
        private String email;
        @SerializedName("Photo")
        @Expose
        private String photo;
        @SerializedName("ActiveBookings")
        @Expose
        private Integer activeBookings;

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

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCourierId() {
            return courierId;
        }

        public void setCourierId(String courierId) {
            this.courierId = courierId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Object getCreatedDateTime() {
            return createdDateTime;
        }

        public void setCreatedDateTime(Object createdDateTime) {
            this.createdDateTime = createdDateTime;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
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

        public Object getMobile() {
            return mobile;
        }

        public void setMobile(Object mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public Integer getActiveBookings() {
            return activeBookings;
        }

        public void setActiveBookings(Integer activeBookings) {
            this.activeBookings = activeBookings;
        }

    }

}
