package fr.altoine.jnogging.view.HistoryActivity;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.altoine.jnogging.R;
import fr.altoine.jnogging.utils.Constants;

/**
 * Created by soulierantoine on 03/07/2017.
 */

public class RunsAdapter extends RecyclerView.Adapter<RunsAdapter.RunsAdapterViewHolder> {

//    private String mRunsData[];
    private Cursor mCursor;
    final private RunsAdapterOnClickListener mClickListener;

    /* public void setRunsData(String[] runsData) {
        mRunsData = runsData;
        notifyDataSetChanged();
    } */


    public interface RunsAdapterOnClickListener {
        void onClick(String data);
    }


    public RunsAdapter(RunsAdapterOnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }


    @Override
    public RunsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.history_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(layoutId, parent, false);
        return new RunsAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RunsAdapterViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position))
            return;

        long id = mCursor.getLong(Constants.RunsTable.INDEX_RUN_ID);
        int distance = mCursor.getInt(Constants.RunsTable.INDEX_RUN_DISTANCE);
        float speed = mCursor.getFloat(Constants.RunsTable.INDEX_RUN_SPEED);
        int timeSpentRunning = mCursor.getInt(Constants.RunsTable.INDEX_TIME_SPENT_RUNNING);
        String startTime = mCursor.getString(Constants.RunsTable.INDEX_RUN_START_TIME);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.getDefault());
        Date formattedDate = null;
        try {
            formattedDate = dateFormat.parse(startTime);
            dateFormat = new SimpleDateFormat("dd/mm/yyyy", Locale.getDefault());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String displayedDate = startTime;
        if (formattedDate != null)
            displayedDate = dateFormat.format(formattedDate);

        holder.itemView.setTag(id);
        holder.distanceTextView.setText(String.valueOf(distance));
        holder.speedTextView.setText(String.valueOf(speed));
        holder.timeTextView.setText(String.valueOf(timeSpentRunning));
        holder.dateTextView.setText(displayedDate);
    }


    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    /**
     * Swaps the cursor used by the RunsData for its data.
     * Called when we have a new set of data.
     *
     * @param newCursor the new cursor to use as RunsAdapter's data source
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    class RunsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView distanceTextView;
        TextView timeTextView;
        TextView speedTextView;
        TextView dateTextView;


        @Override
        public void onClick(View v) {
            mClickListener.onClick("TODO");
//            int adapterPosition = getAdapterPosition();
//            String runData = mRunsData[adapterPosition];
//            mClickListener.onClick(runData);
        }


        public RunsAdapterViewHolder(View itemView) {
            super(itemView);
            distanceTextView = (TextView) itemView.findViewById(R.id.tv_distance);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            speedTextView = (TextView) itemView.findViewById(R.id.tv_speed);
            dateTextView = (TextView) itemView.findViewById(R.id.tv_date);
            itemView.setOnClickListener(this);
        }
    }
}
