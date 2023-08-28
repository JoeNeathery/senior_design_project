package com.example.g29.msbandapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GraphActivity extends AppCompatActivity {

    private long todayDate = 0, targetDate = 0;
    private int userId, graphValue;
    private Button OneDayBtn, OneWeekBtn, OneMonthBtn;
    private TextView ErrorText;
    LineGraphSeries<DataPoint> cal_series, hr_series, steps_series;
    GraphView Graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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

    private void setupUIViews() {
        OneDayBtn = findViewById(R.id.oneDayBtn);
        OneWeekBtn = findViewById(R.id.oneWeekBtn);
        OneMonthBtn = findViewById(R.id.oneMonthBtn);
        Graph = findViewById(R.id.graph);
        ErrorText = findViewById(R.id.errorTxt);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);

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
        Graph.removeAllSeries();
        if(result.equals("connection successful[][][]")){
            //no data
            ErrorText.setText("No data for that Time Period. Record some data or expand your search.");
        }else{
            ErrorText.setText("");
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

            graphData(cals, cal_dates, hrs, hr_dates, steps, steps_dates);
        }


        return;
    }

    private void graphData(int[] cals, String[] cal_date_strings, int[] hrs, String[] hr_date_strings, int[] steps, String[] step_date_strings) {
        DataPoint[] cal_dp = new DataPoint[cal_date_strings.length];
        DataPoint[] hr_dp = new DataPoint[hr_date_strings.length];
        DataPoint[] steps_dp = new DataPoint[step_date_strings.length];
        int size = 0;
        Context mContext = this;

        for (int i = 0; i < cal_date_strings.length; i++) {
            try {
                Date cal_date = new SimpleDateFormat("yyyy-MM-dd").parse(cal_date_strings[i]);
                cal_dp[i] = new DataPoint(cal_date, cals[i]);
                size = i;
            } catch (Exception e) {

            }
        }

        for (int i = 0; i < hr_date_strings.length; i++) {
            try {
                Date hr_date = new SimpleDateFormat("yyyy-MM-dd").parse(hr_date_strings[i]);
                hr_dp[i] = new DataPoint(hr_date, hrs[i]);
            } catch (Exception e) {

            }
        }

        for(int i=0;i<step_date_strings.length;i++){
            try{
                Date step_date = new SimpleDateFormat("yyyy-MM-dd").parse(step_date_strings[i]);
                steps_dp[i] = new DataPoint(step_date, steps[i]);
            }catch(Exception e){

            }
        }


        cal_series = new LineGraphSeries<>(cal_dp);
        hr_series = new LineGraphSeries<>(hr_dp);
        steps_series = new LineGraphSeries<>(steps_dp);
        //Graph.addSeries(cal_series);
        hr_series.setTitle("Heart Rate");
        steps_series.setTitle("Steps");
        Graph.addSeries(hr_series);
        Graph.addSeries(steps_series);
        steps_series.setColor(Color.RED);
        Graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(mContext));
        Graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
        Graph.getViewport().setScalableY(true);
        Graph.getLegendRenderer().setVisible(true);
        Graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        //Graph.getGridLabelRenderer().setNumHorizontalLabels(size); // only 4 because of the space



    }



}
/*
        for(int i=0;i<cal_length;i++){
            //Date cal_date = new SimpleDateFormat("yyyy-dd-MM ")
            //cal_dp[i] = new DataPoint(cal_dates[i], cals[i]);
        }


    }


}
*/
