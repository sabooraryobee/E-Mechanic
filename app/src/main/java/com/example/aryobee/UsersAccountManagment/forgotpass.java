package com.example.aryobee.UsersAccountManagment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aryobee.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.LoginActivity;

public class forgotpass extends AppCompatActivity {
    private Toolbar forgotpasstoolbar;
    private EditText email;
    private Button submit;
    private FirebaseAuth firebaseAuth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgotpass);
        email = (EditText) findViewById(R.id.editText);
        submit = (Button) findViewById(R.id.button7);
        firebaseAuth = FirebaseAuth.getInstance();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eemail = email.getText().toString();
                if (eemail.isEmpty()) {
                    email.setError("please type your email");
                } else {
                    firebaseAuth.sendPasswordResetEmail(eemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(forgotpass.this, "please check your inbox", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(forgotpass.this, LoginActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(forgotpass.this, "sorry there is a problem please try again", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}