package com.example.aryobee.Feedback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aryobee.MapActivity.CustomerMapActivity;
import com.example.aryobee.Models.RatingModel;
import com.example.aryobee.R;
import com.example.aryobee.UsersAccountManagment.Ratings;
import com.example.aryobee.UsersAccountManagment.User;
import com.example.aryobee.UsersAccountManagment.commons;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RateActivity_of_Customer extends AppCompatActivity {
    Button btnSubmit;
    MaterialRatingBar ratingBar;
    MaterialEditText etComment;
    User user;
    Ratings mratings;

    double ratingStars=0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        btnSubmit=findViewById(R.id.btnSubmit);
        ratingBar=(MaterialRatingBar)findViewById(R.id.ratingBar);
        etComment=(MaterialEditText) findViewById(R.id.etComment);
        mratings=new Ratings();

        ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
               ratingStars=rating;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etComment.getText().equals("")) {
                    submitRateDetails(etComment.getText().toString(), ratingStars);
                    StoreRating(etComment.getText().toString(),ratingStars);
                }
                else{
                    Toast.makeText(RateActivity_of_Customer.this, "Please provide comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void submitRateDetails(String comment, double rate) {
        Map<String,Object>mratingMap=new HashMap<>();
        commons.Mechanics.child(commons.mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    user=task.getResult().getValue(User.class);
                    if(user!=null && user.getRatings()!=null){
                       mratings=user.getRatings();
                        mratingMap.put("totalRating",mratings.getTotalRating()+rate);
                        mratingMap.put("totalCount",mratings.getTotalCount()+1);
                        commons.Mechanics.child(commons.mAuth.getCurrentUser().getUid())
                                .updateChildren(mratingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RateActivity_of_Customer.this, "Rating updated to user", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        mratingMap.put("totalRating",rate);
                        mratingMap.put("totalCount",1);
                        commons.Mechanics.child(commons.mAuth.getCurrentUser().getUid())
                                .updateChildren(mratingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Toast.makeText(RateActivity_of_Customer.this, "Rating added to user", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            }
        });
    }

    public void StoreRating(String comment,double rate){
        RatingModel ratingModel=new RatingModel();
        ratingModel.setMechanicID(commons.assignedMechanicID);
        ratingModel.setRate(rate);
        ratingModel.setCustomerID(commons.mAuth.getCurrentUser().getUid());
        ratingModel.setComment(comment);

        commons.RatingRef.child(commons.mAuth.getCurrentUser().getUid()).setValue(ratingModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(RateActivity_of_Customer.this, "Rating added to rating history", Toast.LENGTH_SHORT).show();
               Map<String,Object>map=new HashMap<>();
               map.put("status",0);
               commons.users.child(commons.mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(getApplicationContext(),CustomerMapActivity.class));
                    }
                });
            }
        });
    }

}
