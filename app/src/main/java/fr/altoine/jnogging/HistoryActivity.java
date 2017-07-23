package fr.altoine.jnogging;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.altoine.jnogging.model.data.RunContract;
import fr.altoine.jnogging.model.data.RunDbHelper;
import fr.altoine.jnogging.utils.Constants;
import fr.altoine.jnogging.utils.FakeRunsData;

public class HistoryActivity extends AppCompatActivity implements
        RunsAdapter.RunsAdapterOnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private SQLiteDatabase mDb;
    private RecyclerView mRunsHistory;
    private RunsAdapter mRunsAdapter;
    private ProgressBar mLoadingIndicator;
    private int mPosition = RecyclerView.NO_POSITION;

    private static final int ID_RUNS_LOADER = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading);
        mRunsAdapter = new RunsAdapter(this);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRunsHistory = (RecyclerView) findViewById(R.id.rv_runs);
        mRunsHistory.setHasFixedSize(true);
        mRunsHistory.setLayoutManager(layoutManager);
        mRunsHistory.setAdapter(mRunsAdapter);

        mDb = new RunDbHelper(this).getReadableDatabase();

        if (BuildConfig.DEBUG) {
            Cursor cursor = mDb.query(RunContract.RunsEntry.TABLE_NAME, Constants.RUNS_PROJECTION, null, null, null, null, null);
            if (cursor.getCount() == 0)
                FakeRunsData.insertFakeData(HistoryActivity.this);

            cursor.close();
        }

        // TODO: TMP. Perhaps propose to delete run in detailed view.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                removeRun(id);
                mRunsAdapter.swapCursor(getAllRuns());
                Toast.makeText(HistoryActivity.this, "ID Run " + String.valueOf(id) + " was removed.", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(mRunsHistory);

        getSupportLoaderManager().initLoader(ID_RUNS_LOADER, null, this);
        showLoading();
    }

    // TODO: move database related queries to a utility class.
    private boolean removeRun(long id) {
        return mDb.delete(
                RunContract.RunsEntry.TABLE_NAME,
                RunContract.RunsEntry._ID + "=" + String.valueOf(id),
                null
        ) > 0;
    }

    private Cursor getAllRuns() {
        return mDb.query(
                RunContract.RunsEntry.TABLE_NAME,
                Constants.RUNS_PROJECTION,
                null,
                null,
                null,
                null,
                RunContract.RunsEntry.COLUMN_START_TIME + " DESC"
        );
    }

    private void showLoading() {
        mRunsHistory.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showHistory() {
        mRunsHistory.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(String data) {
        // TODO: Put extra info in intent when going in run details.
        Intent goToDetailIntent = new Intent(HistoryActivity.this, DetailRunActivity.class);
        //goToDetail.putExtra()
        startActivity(goToDetailIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_RUNS_LOADER:
                Uri runsUri = RunContract.RunsEntry.CONTENT_URI;
                String sortOrder = RunContract.RunsEntry.COLUMN_START_TIME + " DESC";
                return new CursorLoader(
                        this,
                        runsUri,
                        Constants.RUNS_PROJECTION,
                        null,
                        null,
                        sortOrder
                );
            default:
                throw new RuntimeException("Loader not implemented");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRunsAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRunsHistory.smoothScrollToPosition(mPosition);
        showHistory();
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRunsAdapter.swapCursor(null);
    }
}
