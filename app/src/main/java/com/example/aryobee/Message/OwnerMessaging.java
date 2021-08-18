package com.example.aryobee.Message;


import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.aryobee.R;
import com.example.aryobee.UsersAccountManagment.User;
import com.example.aryobee.UsersAccountManagment.commons;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OwnerMessaging extends Activity {
    FirebaseUser Fuser;
    String recieverID;
    List<ChatMessage> chatlist;
    ListView listView;
    User user;
    TextView inputMessage;
    ImageButton send1,send2;
    String chatRoomID;
    ChatMessagesAdapter adapter;
    ListenerRegistration listenerRegistration;
    HashMap<String,String> bundle;
    ChatRoom chatroom;
    LinearLayout chatting_layout_input;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatmessages_layout);

    inputMessage=findViewById(R.id.send_message_input);
        send1=findViewById(R.id.send_message_chatM1);
        send2=findViewById(R.id.send_message_chatM2);
        listView=(ListView) findViewById(R.id.messages_listview);
        Fuser= commons.mAuth.getCurrentUser();
        chatting_layout_input=findViewById(R.id.chatting_input);
        //this.bundle= (HashMap<String, String>) getArguments().getSerializable("bundle");

//        this.chatRoomID=bundle.get("chatroomId");
//        this.recieverID =bundle.get("recieverID");

        send2.setOnClickListener(v -> sendMessage());

        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    send1.setVisibility(View.GONE);
                    send2.setVisibility(View.VISIBLE);
                }
                else{
                    send1.setVisibility(View.VISIBLE);
                    send2.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeChatStatus();
            }
        });
    }

    private void changeChatStatus() {

    }

    public void sendMessage(){
        ChatMessage message =new ChatMessage();
        message.setMessage(inputMessage.getText().toString());
        commons.users.child(recieverID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                user=task.getResult().getValue(User.class);
                message.setReceivedAt(Timestamp.now().toDate());
                message.setReceiverFCMid(user.getFCMID());
                message.setReceiverID(user.getuID());
                message.setSendAt(Timestamp.now().toDate());
                message.setSenderID(Fuser.getUid());
                message.setTitle("ChatMessage");
                Log.i("message",message.toString());
                commons.Chatrooms.child(this.chatRoomID).child("Messages").setValue(message).addOnCompleteListener(value -> {
                    inputMessage.setText("");
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        checkChatRoomExist();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerRegistration.remove();
    }

    public void checkChatRoomExist(){
        if(!commons.customerID.equals("")) {
            commons.users.child(Fuser.getUid()).child("chatrooms").equalTo("members", commons.customerID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if ((task.isComplete()) && (task.getResult().exists())) {
                            chatroom = task.getResult().getValue(ChatRoom.class);
                            Log.i("sssssssssssssss", chatroom.toString() + "");
                        } else {
                            createChatRoom(commons.customerID);
                        }
                    });
        }else{
            Toast.makeText(OwnerMessaging.this, "no client", Toast.LENGTH_SHORT).show();
        }
    }

    private void recieverEndChatRoom(ChatRoom chatRoom,String clientID) {
        if(!commons.customerID.equals("")){
        List<String> members=new ArrayList<>();
        members.add(Fuser.getUid());
        chatRoom.setMembers(members);
        commons.users.child(clientID).child("chatrooms").child(chatRoom.getChatroomID()).setValue(chatRoom)
                .addOnCompleteListener(task -> {
                    this.chatting_layout_input.setVisibility(View.VISIBLE);
                });
        }else{
            Toast.makeText(OwnerMessaging.this, "no client", Toast.LENGTH_SHORT).show();
        }
    }

    public void createChatRoom(String clientId){
        ChatRoom chatRoom=new ChatRoom();
        DatabaseReference docRef =commons.Chatrooms.push();
        chatRoom.setChatroomID(docRef.getKey());
        List<String> members=new ArrayList<>();
        members.add(clientId);
        chatRoom.setMembers(members);
        docRef.setValue(chatRoom).addOnCompleteListener(task -> {
            recieverEndChatRoom(chatRoom,clientId);
        });
    }
}


