package com.example.aryobee.Message;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ChatRoom implements Serializable {
    @ServerTimestamp
    Date createAt;
    String chatroomID;
    List<String>members;

    public ChatRoom(){}

    public ChatRoom(Date createAt, String chatroomID, List<String> members) {
        this.createAt = createAt;
        this.chatroomID = chatroomID;
        this.members = members;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getChatroomID() {
        return chatroomID;
    }

    public void setChatroomID(String chatroomID) {
        this.chatroomID = chatroomID;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "createAt=" + createAt +
                ", chatroomID='" + chatroomID + '\'' +
                ", members=" + members +
                '}';
    }
}
