package com.maz.aaraeventapp.Activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.maz.aaraeventapp.R;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Arrays;

import Adapters.EventAdapter;
import Model.AbstractResponse;
import Model.EventResponse;
import Provider.ApiAsyncTask;
import Provider.VolleyReq;

public class MainActivity extends AppCompatActivity {

    public Calendar mService;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
    GoogleAccountCredential credential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    String startDate, endDate;
    ProgressBar progressBar3, progressBar4;
    private RequestQueue mRequestQueue;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;
        mRequestQueue = Volley.newRequestQueue(this);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(dtf.format(now));
        startDate = now + " 00:00";
        endDate = now + " 23:59";
        getLocalData(endDate, startDate);
        final DecimalFormat formatter = new DecimalFormat("00");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EventAddActivity.class);
                startActivity(intent);
            }
        });
        initializeCalender();
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                month = month == 12 ? 1 : month + 1;
                Toast.makeText(getApplicationContext(), "" + formatter.format(dayOfMonth) + "/" + formatter.format(month) + "/" + year, Toast.LENGTH_SHORT).show();// TODO Auto-generated method stub
                startDate = year + "-" + formatter.format(month) + "-" + formatter.format(dayOfMonth) + " 00:00";
                endDate = year + "-" + formatter.format(month) + "-" + formatter.format(dayOfMonth) + " 23:59";
                refreshResults(endDate, startDate);
                getLocalData(endDate, startDate);
            }
        });
    }


    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults(endDate, startDate);
            getLocalData(endDate, startDate);
        } else {
            Toast.makeText(this, "Google Play Services required: " +
                    "after installing, close and relaunch this app.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeCalender() {

        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));


        mService = new Calendar.Builder(transport, jsonFactory, credential)
                .setApplicationName("Aara Event").build();
    }


    public void initializeRecyclerG(List<Event> events) {
        progressBar3.setVisibility(View.GONE);
        TextView no_data = findViewById(R.id.no_g_data);
        RecyclerView gRecycler = findViewById(R.id.gRecycler);
        if (events == null || events.size() == 0) {
            no_data.setVisibility(View.VISIBLE);
            gRecycler.setVisibility(View.INVISIBLE);
        } else {
            no_data.setVisibility(View.GONE);
            gRecycler.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            gRecycler.setLayoutManager(layoutManager);
            EventAdapter eventAdapter = new EventAdapter(events, this);
            gRecycler.setAdapter(eventAdapter);
        }
    }


    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    refreshResults(endDate, startDate);
                } else {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        refreshResults(endDate, startDate);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Account unspecified.", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    refreshResults(endDate, startDate);
                } else {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private void refreshResults(String end, String start) {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {
                progressBar3.setVisibility(View.VISIBLE);
                new ApiAsyncTask(this).execute(end, start);
            } else {
                Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }


    public void getLocalData(String endDate, String startDate) {
        progressBar4.setVisibility(View.VISIBLE);
        VolleyReq.get_all_events(new Response.Listener<EventResponse>() {
            @Override
            public void onResponse(EventResponse response) {
                progressBar4.setVisibility(View.GONE);
                initializeRecyclerL(response.getResponse());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }).enqueue(mRequestQueue);
    }

    public void initializeRecyclerL(List<Model.Event> events) {
        TextView no_data = findViewById(R.id.no_a_data);
        RecyclerView lRecycler = findViewById(R.id.aaraRecycler);
        progressBar4.setVisibility(View.GONE);
        if (events.size() == 0) {
            no_data.setVisibility(View.VISIBLE);
            lRecycler.setVisibility(View.GONE);
        } else {
            no_data.setVisibility(View.GONE);
            lRecycler.setVisibility(View.VISIBLE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            lRecycler.setLayoutManager(layoutManager);
            EventAdapter eventAdapter = new EventAdapter(events, this, 1);
            lRecycler.setAdapter(eventAdapter);
        }

    }


}
