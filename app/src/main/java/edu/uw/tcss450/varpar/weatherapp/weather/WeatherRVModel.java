package edu.uw.tcss450.varpar.weatherapp.weather;

public class WeatherRVModel {

    private String time;
    private String temp;
    private String icon;
    private String windSpeed;
    private String condition;

    public WeatherRVModel(String time, String temp, String icon) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
    }
    public WeatherRVModel(String time, String temp, String icon, String condition) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
        this.condition = condition;
    }

    public String getTime() {
        return time;
    }

    public String getTemp() {
        return temp;
    }

    public String getIcon() {
        return icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }
    public String getCondition() {
        return condition;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
}
