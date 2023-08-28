package com.example.g29.msbandapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ReportActivity extends AppCompatActivity {

    private Button OneDayBtn, OneWeekBtn, OneMonthBtn;
    private TextView ErrorText, CalText, HrText, StepsText, AdviceText, DateRangeText;
    private long todayDate = 0, targetDate = 0;
    private int userId, graphValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        setupUIViews();


        OneDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(0);
            }
        });

        OneWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(1);
            }
        });

        OneMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(2);
            }
        });


    }

    private void setupUIViews(){
        OneDayBtn = findViewById(R.id.oneDayBtn);
        OneWeekBtn = findViewById(R.id.oneWeekBtn);
        OneMonthBtn = findViewById(R.id.oneMonthBtn);
        ErrorText = findViewById(R.id.errorTxt);
        CalText = findViewById(R.id.calTxt);
        HrText = findViewById(R.id.hrTxt);
        StepsText = findViewById(R.id.stepsTxt);
        AdviceText = findViewById(R.id.adviceTxt);
        DateRangeText = findViewById(R.id.dateRangeTxt);
    }

    private void getData(int value) {
        Date today = new Date();
        graphValue = value;
        //get data from between today and previous day
        if (value == 0) {
            final Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strTodayDate = df.format(today);
            String strTargetDate = df.format(yesterday.getTime());

            try {
                Date convert_today = df.parse(strTodayDate);
                Date convert_target = df.parse(strTargetDate);
                todayDate = convert_today.getTime();
                targetDate = convert_target.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value == 1) {
            final Calendar weekAgo = Calendar.getInstance();
            weekAgo.add(Calendar.DATE, -7);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strTodayDate = df.format(today);
            String strTargetDate = df.format(weekAgo.getTime());

            try {
                Date convert_today = df.parse(strTodayDate);
                Date convert_target = df.parse(strTargetDate);
                todayDate = convert_today.getTime();
                targetDate = convert_target.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (value == 2) {
            final Calendar monthAgo = Calendar.getInstance();
            monthAgo.add(Calendar.DATE, -31);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strTodayDate = df.format(today);
            String strTargetDate = df.format(monthAgo.getTime());

            try {
                Date convert_today = df.parse(strTodayDate);
                Date convert_target = df.parse(strTargetDate);
                todayDate = convert_today.getTime();
                targetDate = convert_target.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        final String server_url = "http://ec2-18-207-221-13.compute-1.amazonaws.com/get_data.php";
        //volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String result = response;
                        if (result.contains("connection successful")) {
                            parseResult(result);
                        }
                        Log.d("response", result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        error.getMessage();
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String, String>();

                param.put("userId", Integer.toString(userId));
                param.put("today", Long.toString(todayDate));
                param.put("target_date", Long.toString(targetDate));
                return param;
            }
        };

        Vconnection.getnInstance(this).addRequestQue(stringRequest);
    }

    private void parseResult(String result) {
        if(result.equals("connection successful[][][]")){
            ErrorText.setText("No Data Stored for that Time Period. Record some data or expand your search.");
            DateRangeText.setText("");
            CalText.setText("");
            HrText.setText("");
            StepsText.setText("");
            AdviceText.setText("");

        }else{
            String cal_string = result.substring(result.indexOf("[") + 1, result.indexOf("]") - 1);
            result = result.replace(result.substring(result.indexOf("["), result.indexOf("]") + 1), "");
            String hr_string = result.substring(result.indexOf("[") + 1, result.indexOf("]") - 1);
            result = result.replace(result.substring(result.indexOf("["), result.indexOf("]") + 1), "");
            String steps_string = result.substring(result.indexOf("[") + 1, result.indexOf("]") - 1);

            String[] cal_items = cal_string.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            int[] cals = new int[cal_items.length / 2];
            String[] cal_dates = new String[cal_items.length / 2];
            for (int i = 0, j = 0, x = 1; i < cal_items.length / 2; i++, j += 2, x += 2) {

                //calorie at position "i" in cal array corresponds to date in cal_dates array at same position
                try {
                    cals[i] = Integer.parseInt(cal_items[j]);


                } catch (NumberFormatException nfe) {
                    //NOTE: write something here if you need to recover from formatting errors
                }
                cal_dates[i] = cal_items[x];
            }

            String[] hr_items = hr_string.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            int[] hrs = new int[hr_items.length / 2];
            String[] hr_dates = new String[hr_items.length / 2];
            for (int i = 0, j = 0, x = 1; i < hr_items.length / 2; i++, j += 2, x += 2) {

                //hr at position "i" in cal array corresponds to date in hr_dates array at same position
                try {
                    hrs[i] = Integer.parseInt(hr_items[j]);

                } catch (NumberFormatException nfe) {
                    //NOTE: write something here if you need to recover from formatting errors
                }
                hr_dates[i] = hr_items[x];
            }

            String[] steps_items = steps_string.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
            int[] steps = new int[steps_items.length / 2];
            String[] steps_dates = new String[steps_items.length / 2];
            for (int i = 0, j = 0, x = 1; i < steps_items.length / 2; i++, j += 2, x += 2) {

                //steps at position "i" in cal array corresponds to date in steps_dates array at same position
                try {
                    steps[i] = Integer.parseInt(steps_items[j]);

                } catch (NumberFormatException nfe) {
                    //NOTE: write something here if you need to recover from formatting errors
                }
                steps_dates[i] = steps_items[x];
            }
            generateReport(cal_dates, cals, hrs, steps);
        }



        return;
    }

    private void generateReport(String[] cal_dates, int[] cals, int[] hrs, int[] steps){
        String[] advice = {"Drink water regularly!","Be sure to exercise regularly!", "Don't forget to move around after sitting for a while!", "Eat plenty of fruits and vegetables!", "Eat a well-balanced diet."};
        //total calories burnt
        int cals_burnt = cals[cals.length-1] - cals[0];

        int total_hr = 0;
        for(int i = 0;i<hrs.length;i++){
            total_hr = total_hr + hrs[i];
        }
        double avg_hr = total_hr / hrs.length;

        int total_steps = steps[steps.length-1] - steps[0];
        String start_date_string = "";
        String end_date_string = "";
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        try{
            Date start_date = new SimpleDateFormat("yyyy-MM-dd").parse(cal_dates[0]);
            Date end_date = new SimpleDateFormat("yyyy-MM-dd").parse(cal_dates[cal_dates.length-1]);
            start_date_string = df.format(start_date);
            end_date_string = df.format(end_date);
        }catch (Exception e){

        }

        int rand_index = new Random().nextInt(advice.length);

        ErrorText.setText("");
        DateRangeText.setText("Report for " + start_date_string + " - " + end_date_string);
        CalText.setText("Total Calories Burnt: " + cals_burnt);
        HrText.setText("Average Heart Rate: " + avg_hr + "Beats/Min");
        StepsText.setText("Total Steps Taken: " + total_steps);
        AdviceText.setText(advice[rand_index]);

        return;

    }


}
