package com.example.g29.msbandapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements HealthQuestionDialog.HealthQuestionDialogListener {
    private EditText RegEmail;
    private EditText RegName;
    private EditText RegPassword;
    private EditText ConfirmPassword;
    private EditText DOB;
    private EditText HeightFt, HeightIn, Weight;
    private Float Height;
    private TextView RegErrorMsg;
    private Button CreateBtn;
    private Button HealthQuestionsBtn;
    private Integer ActivityLevelRating = 5, HealthRating = 5;

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

/*        HealthQuestionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthQuestionDialog healthQuestionDialog = new HealthQuestionDialog();
                healthQuestionDialog.show(getSupportFragmentManager(), "health question dialog");
            }
        });*/
    }

    @Override
    public void sendData(String activityLevelRating, String healthRatingString){
        ActivityLevelRating = Integer.parseInt(activityLevelRating);
        HealthRating = Integer.parseInt(healthRatingString);
    }

    private void setupUIViews(){
        RegEmail = findViewById(R.id.regEmail);
        RegName = findViewById(R.id.regName);
        RegPassword = findViewById(R.id.regPassword);
        ConfirmPassword = findViewById(R.id.confirmPassword);
        HeightFt = findViewById(R.id.heightFt);
        HeightIn = findViewById(R.id.heightIn);
        DOB = findViewById(R.id.dob);
        Weight = findViewById(R.id.weight);
        RegErrorMsg = findViewById(R.id.regErrorMsg);
        CreateBtn = findViewById(R.id.createBtn);
        //HealthQuestionsBtn = findViewById(R.id.healthQuestionsBtn);
    }

    private void validate(){
        final String uName, uEmail, uPassword, cPassword, heightFtStr, heightInStr, weightStr, dobStr;



        uName = RegName.getText().toString();
        uEmail = RegEmail.getText().toString();
        uPassword = RegPassword.getText().toString();
        cPassword = ConfirmPassword.getText().toString();
        weightStr = Weight.getText().toString();
        heightFtStr = HeightFt.getText().toString();
        heightInStr = HeightIn.getText().toString();
        dobStr = DOB.getText().toString();


        //validate that all fields have been filled
        if((uName != null && !uName.isEmpty()) && (uEmail != null && !uEmail.isEmpty()) &&
                (uPassword != null && !uPassword.isEmpty()) && (cPassword != null && !cPassword.isEmpty()) && (weightStr != null && !weightStr.isEmpty()) &&
                (heightFtStr != null && !heightFtStr.isEmpty()) && (heightInStr != null && !heightInStr.isEmpty()) && (dobStr != null && !dobStr.isEmpty()) && (HealthRating != null && ActivityLevelRating != null)){
            //check if passwords match
            if(uPassword.equals(cPassword)){
                //create account
                String type = "register";
                String heightStr = heightFtStr + "." + heightInStr;
                Height = Float.parseFloat(heightStr);
                new CreateAccount().execute(type, uName, uEmail, uPassword, dobStr, weightStr, heightStr, ActivityLevelRating.toString(), HealthRating.toString());
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

            //clear passwords
            RegPassword.getText().clear();
            ConfirmPassword.getText().clear();
        }
    }

    private void accountExists(){
        RegErrorMsg.setText("Account already exists with that email.");
        RegErrorMsg.setVisibility(View.VISIBLE);

        //clear fields
        RegName.getText().clear();
        RegEmail.getText().clear();
        RegPassword.getText().clear();
        ConfirmPassword.getText().clear();
        DOB.getText().clear();
        HeightIn.getText().clear();
        HeightFt.getText().clear();
        Weight.getText().clear();
    }

    private class CreateAccount extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {

            if(params[0].equals("register")){
                String regUrl = "http://ec2-18-207-221-13.compute-1.amazonaws.com/register.php";
                try{
                    URL rUrl = new URL(regUrl);
                    HttpURLConnection rhttpUrlConnection = (HttpURLConnection)rUrl.openConnection();
                    rhttpUrlConnection.setRequestMethod("POST");
                    rhttpUrlConnection.setDoOutput(true);
                    rhttpUrlConnection.setDoInput(true);
                    OutputStream rOutputStream = rhttpUrlConnection.getOutputStream();
                    BufferedWriter rBufferedWrite = new BufferedWriter(new OutputStreamWriter(rOutputStream, "UTF-8"));
                    String r_post_data = URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(params[1], "UTF-8")+"&"
                            +URLEncoder.encode("email", "UTF-8")+"="+URLEncoder.encode(params[2], "UTF-8")+"&"
                            +URLEncoder.encode("password", "UTF-8")+"="+URLEncoder.encode(params[3], "UTF-8")+"&"
                            +URLEncoder.encode("dob", "UTF-8")+"="+URLEncoder.encode(params[4], "UTF-8")+"&"
                            +URLEncoder.encode("weight", "UTF-8")+"="+URLEncoder.encode(params[5], "UTF-8")+"&"
                            +URLEncoder.encode("height", "UTF-8")+"="+URLEncoder.encode(params[6], "UTF-8")+"&"
                            +URLEncoder.encode("activityLevelRating", "UTF-8")+"="+URLEncoder.encode(params[7], "UTF-8")+"&"
                            +URLEncoder.encode("healthRating", "UTF-8")+"="+URLEncoder.encode(params[8], "UTF-8");
                    rBufferedWrite.write(r_post_data);
                    rBufferedWrite.flush();
                    rBufferedWrite.close();
                    rOutputStream.close();

                    InputStream rInputStream = rhttpUrlConnection.getInputStream();
                    BufferedReader rBufferedReader = new BufferedReader(new InputStreamReader(rInputStream, "iso-8859-1"));
                    String rResult = "";
                    String rLine;
                    while((rLine = rBufferedReader.readLine()) != null){
                        rResult+=rLine + " ";
                    }
                    rBufferedReader.close();
                    rInputStream.close();
                    rhttpUrlConnection.disconnect();
                    if(rResult.contains("connection successful")){
                        String numberOnly = rResult.replaceAll("[^-1-9]", "");
                        return numberOnly;
                    }else{
                        return rResult;
                    }
                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String id){
            if(id.contains("-1")){
                //account already exists
                accountExists();
            }else{
                //login
                Intent logIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                int userId = Integer.parseInt(id);
                logIntent.putExtra("name", RegName.getText().toString());
                logIntent.putExtra("userId", userId);
                logIntent.putExtra("dob", DOB.getText().toString());
                logIntent.putExtra("height", Height);
                logIntent.putExtra("weight", Integer.parseInt(Weight.getText().toString()));
                logIntent.putExtra("activityLevelRating", ActivityLevelRating);
                logIntent.putExtra("healthRating", HealthRating);
                startActivity(logIntent);
            }

        }

    }

}
