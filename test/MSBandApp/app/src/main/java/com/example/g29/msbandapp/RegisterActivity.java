package com.example.g29.msbandapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText RegEmail;
    private EditText RegName;
    private EditText RegPassword;
    private EditText ConfirmPassword;
    private TextView RegErrorMsg;
    private Button CreateBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUIViews();

        CreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void setupUIViews(){
        RegEmail = findViewById(R.id.regEmail);
        RegName = findViewById(R.id.regName);
        RegPassword = findViewById(R.id.regPassword);
        ConfirmPassword = findViewById(R.id.confirmPassword);
        RegErrorMsg = findViewById(R.id.regErrorMsg);
        CreateBtn = findViewById(R.id.createBtn);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void validate(){
        final String uName, uEmail, uPassword, cPassword;

        uName = RegName.getText().toString();
        uEmail = RegEmail.getText().toString();
        uPassword = RegPassword.getText().toString();
        cPassword = ConfirmPassword.getText().toString();

        //validate that all fields have been filled
        if((uName != null && !uName.isEmpty()) && (uEmail != null && !uEmail.isEmpty()) &&
                (uPassword != null && !uPassword.isEmpty()) && (cPassword != null && !cPassword.isEmpty())){
            //check if passwords match
            if(uPassword.equals(cPassword)){
                //create account
                firebaseAuth.createUserWithEmailAndPassword(uEmail, uPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(e instanceof FirebaseAuthWeakPasswordException){
                                    RegErrorMsg.setText(((FirebaseAuthWeakPasswordException) e).getReason());
                                }else if(e instanceof FirebaseAuthInvalidUserException){
                                    String errorCode = ((FirebaseAuthInvalidUserException) e).getErrorCode();

                                    if(errorCode.equals("ERROR_CREDENTIAL_ALREADY_IN_USE ")){
                                        RegErrorMsg.setText("Account Already in Use");
                                    }else{
                                        RegErrorMsg.setText(e.getLocalizedMessage());
                                    }
                                }else{
                                    RegErrorMsg.setText(e.getLocalizedMessage());
                                }
                                RegErrorMsg.setVisibility(View.VISIBLE);
                            }
                        });

                        //successfully created account
                        if(task.isSuccessful()){
                            //update user info with user name
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(uName)
                                    .build();

                            user.updateProfile(profileUpdates);

                            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    }
                });

            }else{
                //passwords do not match
                RegErrorMsg.setText("Passwords Do Not Match");
                RegErrorMsg.setVisibility(View.VISIBLE);

                //clear passwords
                RegPassword.getText().clear();
                ConfirmPassword.getText().clear();
            }
        }else{
            //fields are blank
            RegErrorMsg.setText("Please Do Not Leave Any Fields Blank");
            RegErrorMsg.setVisibility(View.VISIBLE);

            //clear fields
            RegName.getText().clear();
            RegEmail.getText().clear();
            RegPassword.getText().clear();
            ConfirmPassword.getText().clear();
        }
    }
}
