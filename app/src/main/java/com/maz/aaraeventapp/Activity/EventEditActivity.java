package com.maz.aaraeventapp.Activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.maz.aaraeventapp.R;

import Model.AbstractResponse;
import Model.Event;
import Provider.VolleyReq;

public class EventEditActivity extends AppCompatActivity implements View.OnClickListener {

    Button save;
    EditText sDate, eDate, attach, sub, email;
    private int id;
    private RequestQueue mRequestQueue;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        activity = this;
        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        //int id =
        sDate = findViewById(R.id.sDate);
        eDate = findViewById(R.id.eDate);
        save = findViewById(R.id.add);
        email = findViewById(R.id.email);
        attach = findViewById(R.id.attach);
        sub = findViewById(R.id.sub);
        save.setOnClickListener(this);
        mRequestQueue = Volley.newRequestQueue(this);


    }

    @Override
    public void onClick(View v) {

     if (v == save) {
            save();
        }
    }

    private void save() {
        final ProgressBar progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
            Event event = new Event();
            event.setToEmailId(email.getText().toString());
            event.setAttachment(attach.getText().toString());
            event.setSubject(sub.getText().toString());
            event.setId(id);
            VolleyReq.edit(event, new Response.Listener<AbstractResponse>() {
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
