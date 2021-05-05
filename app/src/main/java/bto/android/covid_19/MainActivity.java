package bto.android.covid_19;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * App to present Covid-19 country stats
 * https://covid19api.com/#details
 *
 * @author iaindownie
 */
public class MainActivity extends AppCompatActivity {

    private static final String JSON_OBJECT_REQUEST_URL = "https://androidtutorialpoint.com/api/volleyJsonObject";

    private static final String JOHN_HOPKINS_API_BASE_URL = "https://api.covid19api.com";

    ProgressDialog progressDialog;
    private static final String TAG = "MainActivity";

    private List<CountryItem> countryItemList;

    private RecyclerView recyclerView;
    private CountryItemAdapter mAdapter;
    private TextView date;

    //private SwipeRefreshLayout swipeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_list);

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        date = findViewById(R.id.date);
        countryItemList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        volleyJsonObjectRequest(JOHN_HOPKINS_API_BASE_URL + "/summary");

//        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                swipeView.setRefreshing(true);
//                (new Handler()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeView.setRefreshing(false);
//
//                        //Log.d("INFO", "Swiped");
//                        volleyJsonObjectRequest(JOHN_HOPKINS_API_BASE_URL + "/summary");
//
//                    }
//                }, 1000);
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem more = menu.findItem(R.id.more);
        more.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(MainActivity.this, "About coming soon...", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String queryText) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("INFO", newText);
                mAdapter.filterData(newText);
                return true;
            }
        });

        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                return true;
            }
        };
        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);
        return super.onCreateOptionsMenu(menu);
    }

    public void volleyJsonObjectRequest(String url) {

        String REQUEST_TAG = "volleyJsonObjectRequest.covid19.summary";
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonObjectRequest jsonObjectReq = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("INFO", response.toString());

                        try {
                            countryItemList.clear();
                            JSONArray countries = response.getJSONArray("Countries");
                            String update = response.getString("Date");

                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM h:mma", Locale.getDefault());
                            final DateFormat fmt;
                            if (update.endsWith("Z")) {
                                fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            } else {
                                fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            }
                            String preDate = "Total cases, total deaths, new cases: ";

                            date.setText(String.format("%s%s", preDate, sdf.format(fmt.parse(update))));

                            for (int i = 0; i < countries.length(); i++) {
                                JSONObject o = countries.getJSONObject(i);
                                CountryItem aCountry = new CountryItem(
                                        o.getString("Country"),
                                        o.optString("CountrySlug", ""),
                                        o.getInt("NewConfirmed"),
                                        o.getInt("TotalConfirmed"),
                                        o.getInt("NewDeaths"),
                                        o.getInt("TotalDeaths"),
                                        o.getInt("NewRecovered"),
                                        o.getInt("TotalRecovered")
                                );
                                countryItemList.add(aCountry);
//                                if (aCountry.Country.length() > 0 && !aCountry.Country.startsWith("Iran (")) {
//                                    countryItemList.add(aCountry);
//                                }
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }

                        mAdapter = new CountryItemAdapter(MainActivity.this, countryItemList);

                        recyclerView.setAdapter(mAdapter);

                        progressDialog.hide();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });

        // Adding JsonObject request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectReq, REQUEST_TAG);
    }


    public void volleyCacheRequest(String url) {
        Cache cache = AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if (entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    public void volleyInvalidateCache(String url) {
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().invalidate(url, true);
    }

    public void volleyDeleteCache(String url) {
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().remove(url);
    }

    public void volleyClearCache() {
        AppSingleton.getInstance(getApplicationContext()).getRequestQueue().getCache().clear();
    }

}
