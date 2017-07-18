package fr.altoine.jnogging;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        mCursor.moveToPosition(position);
        int distance = mCursor.getInt(MainActivity.INDEX_RUN_DISTANCE);
        int speed = mCursor.getInt(MainActivity.INDEX_RUN_SPEED);

        holder.distanceTextView.setText(String.valueOf(distance));
        holder.speedTextView.setText(String.valueOf(speed));
        holder.timeTextView.setText("TODO");
//        String runData = mRunsData[position];
//        holder.descriptionTextView.setText(runData);
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
            itemView.setOnClickListener(this);
        }
    }
}
