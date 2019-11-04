package com.example.smarttalk.modelclass;

public class Chat {
    public int unseencount;
    public int chatID;
    public Message message ;
    public User user;


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

}
