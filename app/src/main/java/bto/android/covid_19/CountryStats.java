package bto.android.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fdev.backgroundchart.GradientChart;

public class CountryStats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_stats);

        GradientChart g = findViewById(R.id.gradientChart);

        Float[] f = {100f, 40f, 40f, 32f, 13f, 5f, 18f, 36f, 20f, 30f, 28f, 27f, 29f};

        g.setChartValues(f);

    }
}