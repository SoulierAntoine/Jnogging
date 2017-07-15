package fr.altoine.jnogging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.Calendar;

import fr.altoine.jnogging.utils.FakeRunsData;

public class HistoryActivity extends AppCompatActivity implements RunsAdapter.RunsAdapterOnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RunsAdapter runsAdapter = new RunsAdapter(this);
        runsAdapter.setRunsData(FakeRunsData.getFakeData());

        Calendar calendar = Calendar.getInstance();
        calendar
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView history = (RecyclerView) findViewById(R.id.rv_runs);
        history.setHasFixedSize(true);
        history.setLayoutManager(layoutManager);
        history.setAdapter(runsAdapter);
    }

    @Override
    public void onClick(String data) {
        // TODO: go on detail
        Toast.makeText(this, "TODO: go on detail", Toast.LENGTH_SHORT).show();
        Intent goToDetail = new Intent(HistoryActivity.this, DetailRunActivity.class);
        goToDetail.putExtra()
    }
}
