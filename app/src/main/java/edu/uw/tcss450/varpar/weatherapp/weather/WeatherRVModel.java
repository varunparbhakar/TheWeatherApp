package edu.uw.tcss450.varpar.weatherapp.weather;

/**
 * Recycler view model for weather.
 */
public class WeatherRVModel {

    /** Time at location. */
    private String time;

    /** Temperature at location. */
    private String temp;

    /** Icon for weather conditions. */
    private String icon;

    /** Windspeed at location. */
    private String windSpeed;

    /** Weather conditions. */
    private String condition;

    /** Name of city. */
    private String cityName;

    /**
     * Constructor with full perms.
     * @param tim time at loc
     * @param tem temp at loc
     * @param ico icon of weather conditions
     * @param con weather conditions
     */
    public WeatherRVModel(final String tim, final String tem,
                          final String ico, final String con) {
        this.time = tim;
        this.temp = tem;
        this.icon = ico;
        this.condition = con;
    }

    /**
     * Constructor with only city name.
     * @param cit name of city
     */
    public WeatherRVModel(final String cit) {
        this.cityName = cit;
    }

    /**
     * Get time.
     * @return String time.
     */
    public String getTime() {
        return time;
    }

    /**
     * Get temp.
     * @return String temp.
     */
    public String getTemp() {
        return temp;
    }

    /**
     * Get icon.
     * @return String icon.
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Get Windspeed.
     * @return String windspeed.
     */
    public String getWindSpeed() {
        return windSpeed;
    }

    /**
     * Get weather conditions.
     * @return String weather conditions.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Get City name.
     * @return String city name.
     */
    public String getCityName() {
        return cityName;
    }
}
