package com.example.smarttalk.ModelClass;

public class RModelClass {
    private String UserID;
    private String Firstname;
    private String Lastname;
    private String Mobilenumber;


    public String getFirstname() {
        return Firstname;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getMobilenumber() {
        return Mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        Mobilenumber = mobilenumber;
    }
}
