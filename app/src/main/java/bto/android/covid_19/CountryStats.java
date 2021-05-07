package bto.android.covid_19;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class CountryStats extends AppCompatActivity {

    private static final String JOHN_HOPKINS_API_BASE_URL = "https://api.covid19api.com/total/country/";
    private static final String TAG = "CountryStats";
    private Bundle bundle;
    private String countrySlug, countryTextTitle = "";
    private TextView countryTitle;
    private Activity activity;

    //private GraphView graph;
    //private LineGraphSeries<DataPoint> series;

    private LineChartView chart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_stats);
        activity = this;

        Intent intent = this.getIntent();
        if (intent != null) {
            bundle = intent.getBundleExtra("COUNTRY_DATA");
            countrySlug = bundle.getString("COUNTRY_SLUG", "united-kingdom");
            countryTextTitle = bundle.getString("COUNTRY_TITLE", "United Kingdom");
        }

        countryTitle = findViewById(R.id.country_title);
        countryTitle.setText("Daily new cases: " + countryTextTitle);
        //graph = findViewById(R.id.graph);
        chart = findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        volleyJsonArrayRequest(JOHN_HOPKINS_API_BASE_URL + countrySlug + "/status/confirmed");
    }

    public void volleyJsonArrayRequest(String url) {

        String REQUEST_TAG = "volleyJsonObjectRequest.covid19.countrytotals";

        //SimpleDateFormat sdf = new SimpleDateFormat("MM/YY", Locale.getDefault());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("INFO", String.valueOf(response.length()));

                        //DataPoint[] dataPoints = new DataPoint[response.length()];

                        List<PointValue> values = new ArrayList<PointValue>();

                        //DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                        int maxY = 0;

                        // Process the JSON
                        try {
                            int daily, runningTotal, tally = 0;

                            // Loop through the array elements
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject o = response.getJSONObject(i);
                                //String date = o.getString("Date");
                                runningTotal = o.getInt("Cases");
                                daily = runningTotal - tally;
                                if (daily < 0) daily = 0;
                                tally = runningTotal;
                                //if (date.endsWith("Z")) {
                                //    fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                //}
                                if (daily > maxY) maxY = daily;
                                //Log.d("INFO", sdf.format(fmt.parse(date)) + " " + runningTotal + " " + daily);
                                //dataPoints[i] = new DataPoint(i, daily);
                                values.add(new PointValue(i, daily));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Line line = new Line(values).setColor(R.color.graph_blue);
                        line.setStrokeWidth(1);
                        line.setPointRadius(2);
                        line.setFilled(true);
                        List<Line> lines = new ArrayList<Line>();
                        lines.add(line);

                        LineChartData data = new LineChartData();
                        data.setLines(lines);

                        Axis axisX = new Axis();
                        Axis axisY = new Axis().setHasLines(true);
                        axisX.setTextColor(Color.BLACK);
                        axisY.setMaxLabelChars(6);
                        axisY.setTextColor(Color.BLACK);
                        data.setAxisXBottom(axisX);
                        data.setAxisYLeft(axisY);

                        //LineChartView chart = new LineChartView(getApplicationContext());
                        chart.setLineChartData(data);


//                        series = new LineGraphSeries<DataPoint>(dataPoints);
//                        series.setDrawBackground(true);
//                        series.setColor(R.color.graph_blue);
//                        series.setBackgroundColor(R.color.graph_blue);
//                        graph.addSeries(series);
//
//                        graph.getViewport().setXAxisBoundsManual(true);
//                        graph.getViewport().setMinX(0);
//                        graph.getViewport().setMaxX(response.length() * 1.005);
//
//                        graph.getViewport().setYAxisBoundsManual(true);
//                        graph.getViewport().setMinY(0);
//                        graph.getViewport().setMaxY(maxY * 1.1);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                }
        );


        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest, REQUEST_TAG);
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(activity, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
}