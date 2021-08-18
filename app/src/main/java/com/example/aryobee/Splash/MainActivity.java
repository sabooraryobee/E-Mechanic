package com.example.aryobee.Splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aryobee.MapActivity.CustomerMapActivity;
import com.example.aryobee.MapActivity.MechanicMapActivity;
import com.example.aryobee.R;
import com.example.aryobee.UsersAccountManagment.UsersLogin;
import com.example.aryobee.UsersAccountManagment.UsersSelection;
import com.example.aryobee.UsersAccountManagment.commons;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    SharedPreferences cache;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cache=getApplicationContext().getSharedPreferences(commons.cache,0);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
               updateUI();
            }
        }, SPLASH_TIME_OUT);
    }

    public void updateUI(){
        if(cache.contains("Role")==true){
            int role1=cache.getInt("Role",0);
            Log.i("rrrrrrrrrrrrr",role1+"");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    gotoScreen();
                    } else{
            Intent homeIntent  = new Intent(this, UsersLogin.class);
            startActivity(homeIntent);
            finish();
        }
        }else{
            Log.i("rrrrrrrrrrrrr","no role");
            Intent homeIntent  = new Intent(getApplicationContext(), UsersSelection.class);
            startActivity(homeIntent);
            finish();
        }
    }
    public void gotoScreen(){
        int role=cache.getInt("Role",0);
        Log.i("rrrrrrrrrrrrr",role+"");
          if(role==1){
                Intent intent = new Intent(getApplicationContext(), CustomerMapActivity.class);
                startActivity(intent);
                finish();
            }
            else if(role==2){
                Intent intent = new Intent(getApplicationContext(), MechanicMapActivity.class);
                startActivity(intent);
                finish();
            }
    }
}
