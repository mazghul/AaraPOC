package Provider;

import android.util.Log;


import com.android.volley.BuildConfig;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.maz.aaraeventapp.Activity.EventAddActivity;


import java.io.UnsupportedEncodingException;

import Model.AbstractResponse;
import Model.Event;
import Model.EventResponse;

public class VolleyReq<T> extends JsonRequest<T> {
    protected static final String TAG = VolleyReq.class.getSimpleName();
    private final static Gson GSON = new Gson();
    private static final int MAX_NUM_RETRIES = 3;
    private static final int INITIAL_TIMEOUT_MS = 5000;
    private static final float BACKOFF_MULTIPLIER = 1.0f;
    private static final String HOST_NAME = "http://13.127.19.78:8080/";
    private static final String URL_PREFIX = HOST_NAME + "aara/event/";
    private static final String ADD_EVENT = URL_PREFIX + "load";
    private static final String GET_EVENTS_FOR_DATE = URL_PREFIX + "getEventsByPeriod?startDate=%s&endDate=%s";
    private static final String GET_ALL = URL_PREFIX + "getAllEvents";
    private static final String DELETE = URL_PREFIX + "deleteEventById?id=%d";
    private static final String EDIT = URL_PREFIX + "updateEventsByEmail";

    private Class<T> responseClass;

    public VolleyReq(String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    public VolleyReq(int httpMethod, String url, Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        this(httpMethod, url, null, listener, errorListener);
    }

    public VolleyReq(int httpMethod, String url, Object requestBody, Class<T> responseClass, Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        this(httpMethod, url, requestBody, listener, errorListener);
        this.responseClass = responseClass;
    }


    public VolleyReq(int httpMethod, String url, Object requestBody, Response.Listener<T> listener,
                                  Response.ErrorListener errorListener) {
        super(httpMethod, url, GSON.toJson(requestBody), listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, BACKOFF_MULTIPLIER));
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Method:" + httpMethod + " URL=" + url);
        }
        setShouldCache(false); // do not cache any request.
    }


    public static VolleyReq add_event(Event event, Response.Listener<AbstractResponse> listener,
                                      Response.ErrorListener errorListener) {
        return new VolleyReq(Method.POST, ADD_EVENT, event, listener, errorListener)
                .setTag(EventAddActivity.class.getSimpleName())
                .setClass(AbstractResponse.class);
    }

    public static VolleyReq get_events(String startDate, String endDate, Response.Listener<EventResponse> listener,
                                       Response.ErrorListener errorListener) {
        String url = String.format(GET_EVENTS_FOR_DATE, startDate, endDate);
        return new VolleyReq(url, listener, errorListener)
                .setTag("Get Events")
                .setClass(EventResponse.class);
    }

    public static VolleyReq get_all_events(Response.Listener<EventResponse> listener,
                                       Response.ErrorListener errorListener) {
        String url = String.format(GET_ALL);
        return new VolleyReq(url, listener, errorListener)
                .setTag("Get Events")
                .setClass(EventResponse.class);
    }

    public static VolleyReq delete(int id, Response.Listener<AbstractResponse> listener,
                                   Response.ErrorListener errorListener) {
        String url = String.format(DELETE, id);
        return new VolleyReq(url, listener, errorListener)
                .setTag("Get Events")
                .setClass(AbstractResponse.class);
    }

    public static VolleyReq edit(Event event, Response.Listener<AbstractResponse> listener,
                                      Response.ErrorListener errorListener) {
        return new VolleyReq(Method.POST, EDIT, event, listener, errorListener)
                .setTag(EventAddActivity.class.getSimpleName())
                .setClass(AbstractResponse.class);
    }



    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            T jsonResponse = GSON.fromJson(json, responseClass);
            return Response.success(
                    jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    public VolleyReq setTag(String tag) {
        super.setTag(tag);
        return this;
    }

    public VolleyReq setClass(Class<T> c) {
        responseClass = c;
        return this;
    }

    public void enqueue(RequestQueue requestQueue) {
        requestQueue.cancelAll(this.getTag());
        requestQueue.add(this);
    }
}
