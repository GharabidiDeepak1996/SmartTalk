package com.example.smarttalk.group.pojo;

import android.util.Log;

import com.example.smarttalk.modelclass.Message;
import com.example.smarttalk.modelclass.User;

import java.io.Serializable;
import java.util.List;

public class GroupModel implements Serializable {
    public String groupID;
    public String groupName;
    public String groupImage;
   // public Message message ;
    public List<User> members;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
