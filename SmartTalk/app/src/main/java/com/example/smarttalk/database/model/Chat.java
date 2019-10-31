package com.example.smarttalk.database.model;

public class Chat {
    public int unseencount;
    public int chatID;
    public Message message ;
    public Contact contact;
    public String ConversionID,MessageId;


    public int getUnseencount() {
        return unseencount;
    }

    public void setUnseencount(int unseencount) {
        this.unseencount = unseencount;
    }

    public int getChatID() {
        return chatID;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public String getConversionID() {
        return ConversionID;
    }

    public void setConversionID(String conversionID) {
        ConversionID = conversionID;
    }

    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }
}
