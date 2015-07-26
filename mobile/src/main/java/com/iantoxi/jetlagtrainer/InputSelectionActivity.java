package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class InputSelectionActivity extends Activity {

    private Document doc;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input_selection);
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_selection, menu);
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


    public void onStart() {
        super.onStart();
        EditText txtDate = (EditText) findViewById(R.id.txtdeparturedate);
        txtDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DateDialog dialog = new DateDialog(v);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }
        });

        EditText txtNum = (EditText) findViewById(R.id.txtnum);
        txtNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                retrieveFlightData(s.toString());
            }
        });
    }

    public void generateSchedule(View view) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }

    private void retrieveFlightData(String flightNumber) {
        String searchURLFront = "http://www.google.com/search?q=";
        String searchURLBack = "&gws_rd=ssl";
        searchQuery = searchURLFront + flightNumber + searchURLBack;

        String departureDate = null;
        String arrivalDate = null;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(searchQuery).userAgent("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36")
                            .timeout(12000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (doc != null) {
            Elements tables = doc.select("table");
            for (Element table : tables) {
                for (Element row : table.select("tr")) {
                    String text = row.text();
                    if (text.matches(".*\\bDeparts\\b.*") || text.matches(".*\\bArrives\\b.*")) {
                        String[] phrases = text.split(",");
                        for (int i = 0; i < phrases.length; i++) {
                            if (phrases[i].matches(".*\\bDeparts\\b.*"))
                                departureDate = extractDate(phrases[i + 2]);
                            if (phrases[i].matches(".*\\bArrives\\b.*"))
                                arrivalDate = extractDate(phrases[i + 2]);
                        }
                    }
                }
            }
        }

        EditText departureTxt = (EditText) findViewById(R.id.txtdeparturedate);
        EditText arrivalTxt = (EditText) findViewById(R.id.txtarrivaldate);
        if (departureDate != null && arrivalDate != null) {
            departureTxt.setText(departureDate);
            arrivalTxt.setText(arrivalDate);
        } else {
            departureTxt.setText("");
            arrivalTxt.setText("");
        }
    }

    public String extractDate (String str) {
        String[] words = str.trim().split("\\s+");
        return words[0] + " " + words[1];
    }

}
