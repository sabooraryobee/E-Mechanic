package com.example.aryobee.UsersAccountManagment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aryobee.MapActivity.CustomerMapActivity;
import com.example.aryobee.MapActivity.MechanicMapActivity;
import com.example.aryobee.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class UsersSignUp extends AppCompatActivity {
    SharedPreferences cache;
    EditText txtEmail, txtPassword, txtConfirmPassword;
    Button btn_register;
    ProgressBar progressBar;
    FirebaseUser FUser;
    User user;
    // private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_sign_up);
        cache=getApplicationContext().getSharedPreferences(commons.cache,0);
      //  getSupportActionBar().setTitle("Signup");

        txtEmail = (EditText) findViewById(R.id.txt_email);
        txtPassword = (EditText) findViewById(R.id.txt_password1);
        txtConfirmPassword = (EditText) findViewById(R.id.txt_confirm_password2);
        btn_register = (Button) findViewById(R.id.ButtonRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String confirmPassword = txtConfirmPassword.getText().toString().trim();


                if (TextUtils.isEmpty(email)){
                    Toast.makeText(UsersSignUp.this,"Please Enter Email", Toast.LENGTH_SHORT);
                            return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(UsersSignUp.this,"Please Enter Password", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(UsersSignUp.this,"Please Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length()<6){
                    Toast.makeText(UsersSignUp.this, "Password Too Short", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.VISIBLE);

                if (password.equals(confirmPassword)){
                    commons.mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(UsersSignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        FUser = commons.mAuth.getCurrentUser();
                                        user=new User();
                                        user.setEmail(FUser.getEmail());
                                        user.setName("Customer");
                                        user.setuID(FUser.getUid());

                                        DatabaseReference current_user_db = commons.users.child(FUser.getUid());
                                        current_user_db.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(UsersSignUp.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                                updateUI();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(UsersSignUp.this, "Oops! something went wrong.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else {
                    Toast.makeText(UsersSignUp.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void updateUI(){
        int role=cache.getInt("Role",0);
        Log.i("rrrrrrrrrrrrr",role+"");
        switch (role){
            case 1:{
                Intent intent = new Intent(this, CustomerMapActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case 2:{
                Intent intent = new Intent(this, MechanicMapActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            default:{
                Intent homeIntent  = new Intent(this, UsersSelection.class);
                startActivity(homeIntent);
                finish();
                break;
            }
        }
    }
}