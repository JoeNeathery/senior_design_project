package com.example.g29.msbandapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class LoginActivity extends AppCompatActivity {

    private EditText LoginEmail;
    private EditText LoginPassword;
    private Button LoginBtn;
    private Button RegisterBtn;
    private TextView LoginErrorMsg;

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
                if(checkInternetConnection()){
                    Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(regIntent);
                }else{
                    errorLogin("%%%3");
                }

            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()){
                    validate(LoginEmail.getText().toString(), LoginPassword.getText().toString());
                }else{
                    errorLogin("%%%3");
                }

            }
        });

        //DEV PURPOSES: remove before submission
        DevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()){
                    validate("DEV@DEV.com", "dev123");
                }else{
                    errorLogin("%%%3");
                }

            }
        });


    }

    private void setupUIViews(){
        LoginEmail = findViewById(R.id.loginEmail);
        LoginPassword = findViewById(R.id.loginPassword);
        LoginBtn = findViewById(R.id.loginBtn);
        RegisterBtn = findViewById(R.id.registerBtn);
        LoginErrorMsg = findViewById(R.id.loginErrorMsg);
        DevBtn = findViewById(R.id.devBtn);     //delete before submission
    }

    private void validate(String uEmail, String uPassword){

        //check that all fields were filled in
        if((uEmail != null && !uEmail.isEmpty()) && (uPassword != null && !uPassword.isEmpty())){
            new Login().execute(uEmail, uPassword);
        }else{
            LoginErrorMsg.setText("Please fill in all fields");
            LoginErrorMsg.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkInternetConnection(){
        //check for internet connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            //connected to internet
            return true;
        }else{
            //no internet connection
            return false;
        }

    }

    private void errorLogin(String value){
        if(value.contains("%%%1")){
            LoginErrorMsg.setText("Incorrect Password");
            LoginErrorMsg.setVisibility(View.VISIBLE);
            LoginPassword.getText().clear();
        }else if(value.contains("%%%2")){
            LoginErrorMsg.setText("Account does not exist.");
            LoginErrorMsg.setVisibility(View.VISIBLE);
            LoginEmail.getText().clear();
            LoginPassword.getText().clear();
        }else if(value.contains("%%%3")){
            LoginErrorMsg.setText("Internet connection required to Login.");
            LoginErrorMsg.setVisibility(View.VISIBLE);
        }
    }

    private class Login extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String loginUrl = "http://ec2-18-207-221-13.compute-1.amazonaws.com/login.php";
            try{
                URL rUrl = new URL(loginUrl);
                HttpURLConnection httpUrlConnection = (HttpURLConnection)rUrl.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setDoInput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(params[0], "UTF-8")+"&"
                        +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(params[1], "UTF-8");
                bufferedWrite.write(post_data);
                bufferedWrite.flush();
                bufferedWrite.close();
                outputStream.close();

                InputStream inputStream = httpUrlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line;
                while((line = bufferedReader.readLine()) != null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpUrlConnection.disconnect();
                if(result.contains("connection successful")){
                    return result;
                }else{
                    return "%%%3";
                }
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String value){
            if(value.contains("%%%1") || value.contains("%%%2")){
                String numberOnly = value.replaceAll("connection successful", "");
                errorLogin(numberOnly);
            }else if(value.contains("%%%3")){
                errorLogin("%%%3");
            }else{
                String newValue =   value.replaceAll("connection successful ", "");
                String segments[] = newValue.split(", ");
                int userId = Integer.parseInt(segments[0].replaceAll("id = ", ""));
                String name = segments[1].replaceAll("name = ", "");
                String dob = segments[2].replaceAll("dob = ", "");
                float height = Float.parseFloat(segments[3] .replaceAll("height = ", ""));
                int weight = Integer.parseInt(segments[4].replaceAll("weight = ", ""));
                int activityLevelRating = Integer.parseInt(segments[5].replaceAll("activityLevelRating = ", ""));
                int healthRating = Integer.parseInt(segments[6].replaceAll("healthRating = ", ""));
                //send to home screen
                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                homeIntent.putExtra("userId", userId);
                homeIntent.putExtra("name", name);
                homeIntent.putExtra("dob", dob);
                homeIntent.putExtra("height", height);
                homeIntent.putExtra("weight", weight);
                homeIntent.putExtra("activityLevelRating", activityLevelRating);
                homeIntent.putExtra("healthRating", healthRating);
                startActivity(homeIntent);
            }
        }

    }




}


