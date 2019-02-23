package Provider;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;
import com.maz.aaraeventapp.Activity.MainActivity;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.Z;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */

/**
 * Created by miguel on 5/29/15.
 */

public class ApiAsyncTask extends AsyncTask<String, Void, List<Event>> {
    private MainActivity mActivity;

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    public ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<Event> doInBackground(String... params) {
        try {
            List<Event> eventList = getDataFromApi(params[0],params[1]);
            return eventList;
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);

        } catch (IOException e) {
            Toast.makeText(mActivity, "The following error occurred: " +
                    e.getMessage(), Toast.LENGTH_LONG).show();

        }
        return null;
    }


    @Override
    protected void onPostExecute(List<Event> events) {
        super.onPostExecute(events);
        mActivity.initializeRecycler(events);
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<Event> getDataFromApi(String end, String start) throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Event> items = new ArrayList<>();
        try {
            Date sdate = sdf.parse(start);
            Date eDate = sdf.parse(end);
            DateTime sDT = new DateTime(sdate);
            DateTime eDT = new DateTime(eDate);


            Events events = mActivity.mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(sDT)
                    .setTimeMax(eDT)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
           items = events.getItems();
            Log.d("items",items.toString());

        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());

        }
        return items;
    }

}