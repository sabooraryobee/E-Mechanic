package com.example.aryobee.Message;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage implements Parcelable {

    String senderID;
    String receiverID;
    String receiverFCMid;
    @ServerTimestamp
    Date sendAt;
    @ServerTimestamp
    Date receivedAt;
    String status;
    String message;
    String title;


    public ChatMessage(){}

    public ChatMessage(String senderID, String receiverID, String receiverFCMid, Date sendAt, Date receivedAt, String status, String message, String title) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.receiverFCMid = receiverFCMid;
        this.sendAt = sendAt;
        this.receivedAt = receivedAt;
        this.status = status;
        this.message = message;
        this.title = title;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getReceiverFCMid() {
        return receiverFCMid;
    }

    public void setReceiverFCMid(String receiverFCMid) {
        this.receiverFCMid = receiverFCMid;
    }

    public Date getSendAt() {
        return sendAt;
    }

    public void setSendAt(Date sendAt) {
        this.sendAt = sendAt;
    }

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeString(this.receiverFCMid);
        dest.writeString(this.senderID);
        dest.writeSerializable(this.sendAt);
        dest.writeSerializable(this.receivedAt);
        dest.writeString(this.status);
        dest.writeString(this.receiverID);
        dest.writeString(this.title);
    }

    public ChatMessage(Parcel in) {
        this.message=in.readString();
        this.status=in.readString();
        this.receivedAt=(Date) in.readSerializable();
        this.senderID=in.readString();
        this.receiverFCMid=in.readString();
        this.sendAt=(Date) in.readSerializable();
        this.receiverID=in.readString();
        this.title=in.readString();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}
