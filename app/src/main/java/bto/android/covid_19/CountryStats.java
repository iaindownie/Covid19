package bto.android.covid_19;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class CountryStats extends AppCompatActivity {

    private static final String JOHN_HOPKINS_API_BASE_URL = "https://api.covid19api.com/total/country/";
    private static final String TAG = "CountryStats";
    private Bundle bundle;
    private String countrySlug = "";

    private ValueLineChart mCubicValueLineChart;
    private ValueLineSeries series;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_stats);

        Intent intent = this.getIntent();
        if (intent != null) {
            bundle = intent.getBundleExtra("COUNTRY_DATA");
            countrySlug = bundle.getString("COUNTRY_SLUG", "united-kingdom");
        }


        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        volleyJsonArrayRequest(JOHN_HOPKINS_API_BASE_URL + countrySlug + "/status/confirmed");
    }

    public void volleyJsonArrayRequest(String url) {

        String REQUEST_TAG = "volleyJsonObjectRequest.covid19.countrytotals";

        SimpleDateFormat sdf = new SimpleDateFormat("MM/YY", Locale.getDefault());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.d("INFO", String.valueOf(response.length()));

                        // Do something with response
                        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                        // Process the JSON
                        try {
                            int daily, runningTotal, tally = 0;

                            // Loop through the array elements
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject o = response.getJSONObject(i);
                                String date = o.getString("Date");
                                runningTotal = o.getInt("Cases");
                                daily = runningTotal - tally;
                                tally = runningTotal;
                                if (date.endsWith("Z")) {
                                    fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                }
                                //Log.d("INFO", sdf.format(fmt.parse(date)) + " " + runningTotal + " " + daily);
                                series.addPoint(new ValueLinePoint(sdf.format(fmt.parse(date)), Float.valueOf(daily)));
                            }

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }

                        mCubicValueLineChart.addSeries(series);
                        mCubicValueLineChart.startAnimation();
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
}