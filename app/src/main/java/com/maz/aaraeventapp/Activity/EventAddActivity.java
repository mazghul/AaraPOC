package com.maz.aaraeventapp.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.maz.aaraeventapp.R;

import java.util.Calendar;

import Model.AbstractResponse;
import Model.Event;
import Provider.VolleyReq;

import static java.security.AccessController.getContext;

public class EventAddActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton sDatePicker, eDatePicker;
    Button save;
    EditText sDate, eDate, attach, sub, email;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RequestQueue mRequestQueue;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        activity = this;
        sDatePicker = findViewById(R.id.sBtn);
        eDatePicker = findViewById(R.id.eBtn);
        sDate = findViewById(R.id.sDate);
        eDate = findViewById(R.id.eDate);
        save = findViewById(R.id.add);
        email = findViewById(R.id.email);
        attach = findViewById(R.id.attach);
        sub = findViewById(R.id.sub);
        save.setOnClickListener(this);
        sDatePicker.setOnClickListener(this);
        eDatePicker.setOnClickListener(this);
        mRequestQueue = Volley.newRequestQueue(this);


    }

    @Override
    public void onClick(View v) {

        if (v == sDatePicker || v == eDatePicker) {
            handleDateTime(v);

        } else if (v == save) {
            save();
        }
    }

    private void handleDateTime(final View v) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        final Activity activity = this;


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, final int year,
                                          final int monthOfYear, final int dayOfMonth) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        if (v == sDatePicker)
                                            sDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth + " " + hourOfDay + ":" + minute);
                                        else if (v == eDatePicker)
                                            eDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth + " " + hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void save() {
        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        if( TextUtils.isEmpty(sDate.getText())){
            sDate.setError( "Start Date is required!" );
        } else if( TextUtils.isEmpty(sDate.getText())){
            eDate.setError( "End Date is required!" );
        } else if( TextUtils.isEmpty(eDate.getText())){
            eDate.setError( "End Date is required!" );
        } else if( TextUtils.isEmpty(sub.getText())){
            sub.setError( "Subject is required!" );
        } else {
            Event event = new Event();
            event.setStartDate(sDate.getText().toString());
            event.setEndDate(eDate.getText().toString());
            event.setToEmailId(email.getText().toString());
            event.setAttachment(attach.getText().toString());
            event.setSubject(sub.getText().toString());
            VolleyReq.add_event(event, new Response.Listener<AbstractResponse>() {
                @Override
                public void onResponse(AbstractResponse response) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(activity, response.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }).enqueue(mRequestQueue);
        }
    }
}


