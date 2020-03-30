package bto.android.covid_19;

/**
 * @author @iaindownie on 29/03/2020.
 */
public class CountryItem {

    public String Country;
    public String CountrySlug;
    public Integer NewConfirmed;
    public Integer TotalConfirmed;
    public Integer NewDeaths;
    public Integer TotalDeaths;
    public Integer NewRecovered;
    public Integer TotalRecovered;

    public CountryItem(String country, String countrySlug, Integer newConfirmed, Integer totalConfirmed, Integer newDeaths, Integer totalDeaths, Integer newRecovered, Integer totalRecovered) {
        Country = country;
        CountrySlug = countrySlug;
        NewConfirmed = newConfirmed;
        TotalConfirmed = totalConfirmed;
        NewDeaths = newDeaths;
        TotalDeaths = totalDeaths;
        NewRecovered = newRecovered;
        TotalRecovered = totalRecovered;
    }

    public String getCountry() {
        return Country.trim();
    }
}
