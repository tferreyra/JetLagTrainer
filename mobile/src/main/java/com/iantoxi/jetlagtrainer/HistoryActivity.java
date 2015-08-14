package com.iantoxi.jetlagtrainer;

import android.app.ActivityOptions;
import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

/** Class that displays a user's history of completed or canceled sleep schedules. */
public class HistoryActivity extends ListActivity {
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_history);
        setTransitions();

        HistoryAdapter adapter = new HistoryAdapter(this);
        setListAdapter(adapter);
        noneMessage();
    }

    private void noneMessage() {
        ListView lv = (ListView) findViewById(android.R.id.list);
        TextView tv = (TextView) findViewById(R.id.textView3);
        if (lv.getAdapter().getCount() == 0) {
            lv.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }

    private void setTransitions() {
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        long scheduleId = (Long) v.getTag(R.id.schedule_tags);
        Intent intent = new Intent(this, HistorySummaryActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
