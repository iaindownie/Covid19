package bto.android.covid_19;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author @iaindownie on 29/03/2020.
 */
public class CountryItemAdapter extends RecyclerView.Adapter<CountryItemAdapter.MyViewHolder> {

    private List<CountryItem> countryList;
    private List<CountryItem> filteredCountryList;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView position, countryName, totalCases, totalDeaths, newDeaths;

        public MyViewHolder(View view) {
            super(view);
            position = view.findViewById(R.id.position);
            countryName = view.findViewById(R.id.country);
            totalCases = view.findViewById(R.id.cases);
            totalDeaths = view.findViewById(R.id.deaths);
            newDeaths = view.findViewById(R.id.new_deaths);
        }
    }

    public CountryItemAdapter(Activity activity, List<CountryItem> countryList) {
        this.countryList = countryList;
        this.activity = activity;
        (this.filteredCountryList = new ArrayList<>()).addAll(this.countryList);
        Collections.sort(countryList, new CasesComparator());
    }


    @Override
    public CountryItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_item, parent, false);

        return new CountryItemAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CountryItemAdapter.MyViewHolder holder, int position) {
        final CountryItem country = countryList.get(holder.getAdapterPosition());
        holder.position.setText(String.valueOf(position + 1));
        holder.countryName.setText(country.getCountry());
        holder.totalCases.setText(String.valueOf(country.TotalConfirmed));
        holder.totalDeaths.setText(String.valueOf(country.TotalDeaths));
        holder.newDeaths.setText(String.format("+%d", country.NewConfirmed));
        holder.countryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(activity, "Country stats coming soon...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, CountryStats.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public class CasesComparator implements Comparator<CountryItem> {
        public int compare(CountryItem p1, CountryItem p2) {
            if (p1.TotalConfirmed < p2.TotalConfirmed) return 1;
            if (p1.TotalConfirmed > p2.TotalConfirmed) return -1;
            return 0;
        }
    }

    public void filterData(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        countryList.clear();
        if (charText.length() == 0) {
            countryList.addAll(filteredCountryList);
        } else {
            for (CountryItem c : filteredCountryList) {
                if (charText.length() != 0 && c.getCountry().toLowerCase(Locale.getDefault()).contains(charText)) {
                    countryList.add(c);
                }

            }
        }

        Collections.sort(countryList, new CasesComparator());
        notifyDataSetChanged();
    }


}
