package com.example.aryobee.UsersAccountManagment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class UsersLogin extends AppCompatActivity {
    SharedPreferences cache;
    EditText txtEmail, txtPassword;
    Button btn_login,btn_signup;
    Button forgot;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    public UsersLogin() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_login);
//        getSupportActionBar().setTitle("Login");

        cache = getApplicationContext().getSharedPreferences(commons.cache, 0);

        txtEmail = (EditText) findViewById(R.id.customer_email);
        txtPassword = (EditText) findViewById(R.id.customer_password);
        btn_login = (Button) findViewById(R.id.ButtonLogin_customer);
        btn_signup = findViewById(R.id.BottonSignUp_customer);
        forgot = (Button) findViewById(R.id.button2);

        btn_signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btn_signupForm();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(UsersLogin.this, "Please Enter Email", Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(UsersLogin.this, "Please Enter Password", Toast.LENGTH_SHORT);
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(UsersLogin.this, "Password Too Short", Toast.LENGTH_SHORT).show();
                }

                commons.mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(UsersLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    updateUI();
                                } else {

                                    Toast.makeText(UsersLogin.this, "Login Failed or User not Available", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), forgotpass.class));

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

            public void btn_signupForm() {
                startActivity(new Intent(getApplicationContext(), UsersSignUp.class));
            }

        }
