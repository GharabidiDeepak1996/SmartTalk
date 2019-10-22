package com.example.smarttalk.database.model;

public class Contact {
    private String UserID;
    private String FirstName;
    private String LastName;
    private String MobileNmuber;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobileNmuber() {
        return MobileNmuber;
    }

    public void setMobileNmuber(String mobileNmuber) {
        MobileNmuber = mobileNmuber;
    }
}
