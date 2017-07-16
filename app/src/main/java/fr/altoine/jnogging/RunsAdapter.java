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

    private String mRunsData[];
    private Cursor mCursor;
    final private RunsAdapterOnClickListener mClickListener;

    public void setRunsData(String[] runsData) {
        mRunsData = runsData;
        notifyDataSetChanged();
    }


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

        String runData = mRunsData[position];
        holder.descriptionTextView.setText(runData);
    }


    @Override
    public int getItemCount() {
        if (mRunsData == null) return  0;
        return mRunsData.length;
    }


    class RunsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView descriptionTextView;


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String runData = mRunsData[adapterPosition];
            mClickListener.onClick(runData);
        }


        public RunsAdapterViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.tv_description);
            itemView.setOnClickListener(this);
        }
    }
}
