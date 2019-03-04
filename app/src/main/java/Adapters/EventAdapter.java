package Adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.api.services.calendar.model.Event;
import com.maz.aaraeventapp.Activity.EventEditActivity;
import com.maz.aaraeventapp.R;
import java.util.List;

import Model.AbstractResponse;
import Model.EventResponse;
import Provider.VolleyReq;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = EventAdapter.class.getSimpleName();

    private List<Event> mDataset;
    private List<Model.Event> mGDataset;
    private Activity activity;
    private int calender = 0;
    private RequestQueue mRequestQueue;

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(activity, "No item present", Toast.LENGTH_LONG).show();
            // progressBar.setVisibility(View.GONE);
            //Log.e(TAG, "Error in Logging In", error);
            //UIUtils.showToast(context, "Error in Logging In");
        }
    };

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "video presenter oncreate");
        // create a new view
        View root = LayoutInflater.from(activity).inflate(R.layout.event_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(root);
        mRequestQueue = Volley.newRequestQueue(activity);
        return vh;
    }


    public EventAdapter(List<Event> items, Activity mActivity) {
        mDataset = items;
        activity = mActivity;
    }

    public EventAdapter(List<Model.Event> items, Activity mActivity, int cal) {
        mGDataset = items;
        activity = mActivity;
        calender = cal;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return calender == 0 ? mDataset.size() : mGDataset.size();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.i(TAG, "episode presenter on bind:" + position);
        if (mDataset != null && calender == 0) {
            final Event item = mDataset.get(holder.getAdapterPosition());
            holder.summary.setText(item.getSummary());

            String dtStr = item.getStart().getDateTime() == null ? (item.getStart().getDate()).toString() : item.getStart().getDateTime().toString();
            String edStr = item.getEnd().getDateTime() == null ? (item.getEnd().getDate()).toString() : item.getEnd().getDateTime().toString();
            holder.sDate.setText(dtStr);
            holder.eDate.setText(edStr);
        } else if (mGDataset != null && calender == 1) {
            final Model.Event item = mGDataset.get(holder.getAdapterPosition());
            holder.summary.setText(item.getSubject());

            String dtStr = item.getStartDate();
            String edStr = item.getEndDate();

            holder.sDate.setText(dtStr);
            holder.eDate.setText(edStr);
            holder.delete.setTag(holder);
            holder.edit.setTag(holder);
            holder.edit.setOnClickListener(this);
            holder.delete.setOnClickListener(this);
        }
    }

    {
    }

    @Override
    public void onClick(View v) {
        final ViewHolder position = (ViewHolder) v.getTag();
        switch (v.getId()) {
            case R.id.delete:
                //v.getParent()
                VolleyReq.delete(mGDataset.get(position.getAdapterPosition()).getId(), new Response.Listener<AbstractResponse>() {

                    @Override
                    public void onResponse(AbstractResponse response) {
                        Toast.makeText(activity, response.getMessage(), Toast.LENGTH_LONG).show();
                        this.notify();
                    }
                }, errorListener)
                        .enqueue(mRequestQueue);
                break;

            case R.id.edit:
                Intent intent = new Intent(activity, EventEditActivity.class).putExtra("id", mGDataset.get(position.getAdapterPosition()).getId());
                activity.startActivity(intent);


                break;
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View root;
        TextView summary, sDate, eDate;
        Button edit, delete;

        public ViewHolder(View root) {
            super(root);
            this.root = root;

            summary = (TextView) root.findViewById(R.id.summary);
            sDate = (TextView) root.findViewById(R.id.start);
            eDate = (TextView) root.findViewById(R.id.end);
            edit = (Button) root.findViewById(R.id.edit);
            delete = (Button) root.findViewById(R.id.delete);
        }
    }
}
