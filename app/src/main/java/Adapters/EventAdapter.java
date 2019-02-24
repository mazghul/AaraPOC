package Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.calendar.model.Event;
import com.maz.aaraeventapp.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = EventAdapter.class.getSimpleName();

    private List<Event> mDataset;
    private List<Model.Event> mGDataset;
    private Activity activity;
    private int calender = 0;

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "video presenter oncreate");
        // create a new view
        View root = LayoutInflater.from(activity).inflate(R.layout.event_card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(root);
        return vh;
    }

    public EventAdapter(List<Event> items, Activity mActivity) {
        mDataset =items;
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
        }
        }

     {
    }
}

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View root;
        TextView summary, sDate, eDate;

        public ViewHolder(View root) {
            super(root);
            this.root = root;

            summary = (TextView) root.findViewById(R.id.summary);
            sDate = (TextView) root.findViewById(R.id.start);
            eDate = (TextView) root.findViewById(R.id.end);
        }
}
