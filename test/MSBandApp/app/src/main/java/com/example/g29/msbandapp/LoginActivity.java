package com.example.g29.msbandapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText LoginEmail;
    private EditText LoginPassword;
    private Button LoginBtn;
    private Button RegisterBtn;
    private TextView LoginErrorMsg;
    private FirebaseAuth firebaseAuth;

    //DEV PURPOSES, DELETE ALL REFERENCES BEFORE ANY SUBMISSIONS
    private Button DevBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUIViews();

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(LoginEmail.getText().toString(), LoginPassword.getText().toString());
            }
        });

        //DEV PURPOSES: remove before submission
        DevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate("dev@dev.com", "dev12345");
            }
        });


    }

    private void setupUIViews(){
        LoginEmail = findViewById(R.id.loginEmail);
        LoginPassword = findViewById(R.id.loginPassword);
        LoginBtn = findViewById(R.id.loginBtn);
        RegisterBtn = findViewById(R.id.registerBtn);
        LoginErrorMsg = findViewById(R.id.loginErrorMsg);
        firebaseAuth = FirebaseAuth.getInstance();
        DevBtn = findViewById(R.id.devBtn);     //delete before submission
    }

    private void validate(String uEmail, String uPassword){

        //check that all fields were filled in
        if((uEmail != null && !uEmail.isEmpty()) && (uPassword != null && !uPassword.isEmpty())){
            firebaseAuth.signInWithEmailAndPassword(uEmail, uPassword).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LoginErrorMsg.setText(e.getMessage());
                    LoginErrorMsg.setVisibility(View.VISIBLE);
                }
            }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //send to home screen
                    Intent logIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(logIntent);
                }
            });
        }else{
            LoginErrorMsg.setText("Please fill in all fields");
            LoginErrorMsg.setVisibility(View.VISIBLE);
        }
    }
}
