package com.example.smarttalk.database.model;

public class Message {
    private String SenderID;
    private String conversionID;
    private String MessageID;
    private String Body;
    private String TimeStamp;



    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }


    public String getMessageID() {
        return MessageID;
    }

    public void setMessageID(String messageID) {
        MessageID = messageID;
    }

    public String getConversionID() {
        return conversionID;
    }

    public void setConversionID(String conversionID) {
        this.conversionID = conversionID;
    }
}
