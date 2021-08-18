package com.example.aryobee.UsersAccountManagment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.*;

public class commons {

    public static String cache = "cache";
    public static DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static  DatabaseReference  users= db.child("Users");
    public static  DatabaseReference  Chatrooms= db.child("ChatRooms");
    public static  DatabaseReference  Customers= db.child("Users").child("Customers");
    public static  DatabaseReference  Mechanics= db.child("Users").child("Mechanics");
    public static  DatabaseReference  mechanicsWorking=db.child("mechanicsWorking");
    public static  DatabaseReference  mechanicsAvailable=db.child("mechanicsAvailable");
    public static  DatabaseReference  customerRequest=db.child("customerRequest");
    public static  DatabaseReference  history=db.child("history");
    public static  DatabaseReference  RatingRef=db.child("Rating");
    public static String assignedMechanicID = "";
    public static String customerID = "";

}
