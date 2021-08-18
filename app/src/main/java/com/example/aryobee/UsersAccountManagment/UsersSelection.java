package com.example.aryobee.UsersAccountManagment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aryobee.R;

public class UsersSelection extends AppCompatActivity {
    private Button mMechanic, mCustomer;
    SharedPreferences cache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_selection);
        cache=getApplicationContext().getSharedPreferences(commons.cache,0);

        mMechanic = (Button) findViewById(R.id.mechanic);
        mCustomer = (Button) findViewById(R.id.Customer);

        mMechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserRole(2);
            }
        });
        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserRole(1);
            }
        });
    }

    public void setUserRole(int role){
       SharedPreferences.Editor edit =cache.edit();
       edit.putInt("Role",role);
       edit.apply();
       updateUI();
    }

    public void updateUI(){
                Intent intent = new Intent(UsersSelection.this, UsersLogin.class);
                startActivity(intent);
                finish();
    }
}

