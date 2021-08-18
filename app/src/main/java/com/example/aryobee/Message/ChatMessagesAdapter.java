package com.example.aryobee.Message;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aryobee.R;
import com.example.aryobee.UsersAccountManagment.commons;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

public class ChatMessagesAdapter extends BaseAdapter {

    Context context;
    List<ChatMessage> messages;
    String AMPM;
    FirebaseUser user;
    String chatRoom;

    public ChatMessagesAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
        this.user= commons.mAuth.getCurrentUser();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ChatMessage message = messages.get(i);

        if (message.getSenderID().equals(user.getUid())) {
            view = messageInflater.inflate(R.layout.my_message_chatholder, null);
            holder.messageBody = (TextView) view.findViewById(R.id.my_message_body);
            holder.timestamp = view.findViewById(R.id.my_message_time);
            view.setTag(holder);
            holder.messageBody.setText(message.getMessage());
            int hours = message.getSendAt().getHours();
            int min = message.getSendAt().getMinutes();
            if (hours > 12) {
                AMPM = "PM";
            } else {
                AMPM = "AM";
            }
            int difference = (int)getdifferenceinDays(message.getSendAt(), Timestamp.now().toDate());
            switch (difference){
                case 0:{
                    holder.timestamp.setText(hours + " : " + min + " " + AMPM);
                }
                case 1:{
                    holder.timestamp.setText("Yesterday "+hours + " : " + min + " " + AMPM);
                }
                default:{
                    holder.timestamp.setText(message.getSendAt().getDate()+"/"
                            +message.getSendAt().getMonth()+"/"+message.getSendAt().getYear()
                            +" "+hours + " : " + min + " " + AMPM);
                }
            }
        }
        else {
            view = messageInflater.inflate(R.layout.others_chatholder, null);
            holder.messageBody = (TextView) view.findViewById(R.id.others_message_body);
            holder.timestamp = view.findViewById(R.id.other_message_time);
            view.setTag(holder);

            holder.messageBody.setText(message.getMessage());
            int hours = message.getSendAt().getHours();
            int min = message.getSendAt().getMinutes();
            if (hours > 12) {
                AMPM = "PM";
            } else {
                AMPM = "AM";
            }
            int difference2= (int) getdifferenceinDays(message.getSendAt(),Timestamp.now().toDate());
            switch (difference2){
                case 0:{
                    holder.timestamp.setText(hours + " : " + min + " " + AMPM);
                }
                case 1:{
                    holder.timestamp.setText("Yesterday "+hours + " : " + min + " " + AMPM);
                }
                default:{
                    holder.timestamp.setText(message.getSendAt().getDate()+"/"
                            +message.getSendAt().getMonth()+"/"+message.getSendAt().getYear()
                            +" "+hours + " : " + min + " " + AMPM);
                }
            }
        }

        return view;
    }

    class MessageViewHolder {
        public TextView messageBody;
        public TextView timestamp;
    }

    private long getdifferenceinDays(Date pickUptimestamp, Date dropOfftimestamp) {
        long difference_In_Time= pickUptimestamp.getTime()-dropOfftimestamp.getTime();
        long differencedays= (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;
        return differencedays;
    }

}