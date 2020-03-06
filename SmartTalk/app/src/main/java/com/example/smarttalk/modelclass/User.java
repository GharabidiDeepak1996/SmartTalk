package com.example.smarttalk.modelclass;

import java.io.Serializable;

public class User implements Serializable {
    private static final String TAG = "User";
    private String userId;
    private String firstname;
    private String lastname;
    private String mobilenumber;
    private String ProfileImageURI;
    private String Status;


public User(){

}

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getProfileImageURI() {
        return ProfileImageURI;
    }

    public void setProfileImageURI(String profileImageURI) {
        ProfileImageURI = profileImageURI;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    @Override
    public String toString() {
        return this.firstname + " " + this.lastname;
    }
}
