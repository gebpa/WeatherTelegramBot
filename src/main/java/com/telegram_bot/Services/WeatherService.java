package com.telegram_bot.Services;


import com.telegram_bot.Configurations.BotConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Autowired
    BotConfiguration configs;

    @Autowired
    RequestResponseService requestResponseService;

    public JSONObject notifyIfChange(String city, String code, int periodOfNotice, String timeZoneId) {
        String request = configs.getApiForecast().replaceAll("city", city).replaceAll("code", code);
        String response = requestResponseService.sendRequestGetResponse(request);
        JSONObject forecast = getForecast(response, periodOfNotice, timeZoneId);
        JSONObject current = getCurrentWeatherForNotification(city, code, timeZoneId);
        if (!forecast.has("main") || !current.has("main")) {
            return new JSONObject().put("no changes", 1);
        }
        String mainCurr = current.getString("main");
        String mainFor = forecast.getString("main");
        double tempCurr = current.getDouble("temp");
        double tempFor = forecast.getDouble("temp");
        int humCurr = current.getInt("humidity");
        int humFor = forecast.getInt("humidity");
        double windCurr = current.getDouble("wind speed");
        double windFor = forecast.getDouble("wind speed");
        double pressCurr = current.getDouble("pressure");
        double pressFor = forecast.getDouble("pressure");
        JSONObject result = new JSONObject();
        JSONArray resultArray = new JSONArray();
        if (!mainCurr.equals(mainFor)) {
            resultArray.put("It will change from " + mainCurr + " to " + mainFor + ".");
        }
        if (Math.abs(tempCurr - tempFor) > 5) {
            resultArray.put("Temperature will change from " + tempCurr + "\u2103 to " + tempFor + "\u2103.");
        }
        if (Math.abs(humCurr - humFor) > 15) {
            resultArray.put("Humidity will change from " + humCurr + "% to " + humFor + "%.");
        }
        if (Math.abs(windCurr - windFor) > 4) {
            resultArray.put("Wind speed will change from " + windCurr + "m/s to " + windFor + "m/s.");
        }
        if (Math.abs(pressCurr - pressFor) > 8) {
            resultArray.put("Pressure will change from " + pressCurr + "hPa to " + pressFor + "hPa.");
        }
        if (resultArray.length() == 0) {
            result.put("no changes", 1);
            return result;
        }
        result.put("array", resultArray);
        return result;
    }

    public JSONObject getCurrentWeatherForNotification(String city , String code, String timeZoneId){
        String request = configs.getApiForecast().replaceAll("city", city).replaceAll("code", code);
        String response =requestResponseService.sendRequestGetResponse(request);
        try{

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            DateTimeZone zone = DateTimeZone.forID(timeZoneId);
            DateTime currentTime = new DateTime().withZone(zone);

            //проверка на летнее время, так как в РФ не переводятся часы летом, нужно вычитать один час
            DateTime summerBegin = new DateTime().year().setCopy(currentTime.year().get()).monthOfYear().setCopy(3).dayOfMonth().setCopy(25).secondOfDay().setCopy(0);
            DateTime summerEnd = summerBegin.monthOfYear().setCopy(10).dayOfMonth().setCopy(28);
            if (timeZoneId.equals("Europe/Moscow") && currentTime.compareTo(summerBegin) > 0 && currentTime.compareTo(summerEnd) < 0) {
                currentTime = currentTime.minusHours(1);
            }
            JSONObject result = new JSONObject();
            for (int i = 0; i < jsonArray.length(); i++) {
                String dateAndTime = jsonArray.getJSONObject(i).getString("dt_txt");
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime dtStart = formatter.parseDateTime(dateAndTime);
                DateTime dtEnd = dtStart.plusMinutes(180);
                if (currentTime.compareTo(dtStart) >= 0 && currentTime.compareTo(dtEnd) < 0) {
                    JSONObject from = jsonArray.getJSONObject(i);
                    putParametersInJson(result, from);
                }
            }
            return result;
        } catch (JSONException ex) {
            return new JSONObject().put("exception", "JSONException");
        } catch (Exception ex) {
            return new JSONObject().put("exception", "Unknown");
        }

    }

    public JSONObject getForecast(String response, int periodOfNotice, String timeZoneId) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray jsonArray = jsonResponse.getJSONArray("list");
            DateTimeZone zone = DateTimeZone.forID(timeZoneId);
            DateTime currentTime = new DateTime().withZone(zone);

            //проверка на летнее время, так как в РФ не переводятся часы летом, нужно вычитать один час
            DateTime summerBegin = new DateTime().year().setCopy(currentTime.year().get()).monthOfYear().setCopy(3).dayOfMonth().setCopy(25).secondOfDay().setCopy(0);
            DateTime summerEnd = summerBegin.monthOfYear().setCopy(10).dayOfMonth().setCopy(28);
            if (timeZoneId.equals("Europe/Moscow") && currentTime.compareTo(summerBegin) > 0 && currentTime.compareTo(summerEnd) < 0) {
                currentTime = currentTime.minusHours(1);
            }

            DateTime timeOfNotification = currentTime.plusHours(periodOfNotice);
            JSONObject result = new JSONObject();
            for (int i = 0; i < jsonArray.length(); i++) {
                String dateAndTime = jsonArray.getJSONObject(i).getString("dt_txt");
                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                DateTime dtStart = formatter.parseDateTime(dateAndTime);
                DateTime dtEnd = dtStart.plusMinutes(60);
                if (timeOfNotification.compareTo(dtStart) >= 0 && timeOfNotification.compareTo(dtEnd) <= 0) {
                    JSONObject from = jsonArray.getJSONObject(i);
                    putParametersInJson(result, from);
                }
            }
            return result;
        } catch (JSONException ex) {
            return new JSONObject().put("exception", "JSONException");
        } catch (Exception ex) {
            return new JSONObject().put("exception", "Unknown");
        }
    }

    public void putParametersInJson(JSONObject to, JSONObject from){
        String main = from.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperature = from.getJSONObject("main").getDouble("temp");
        int humidity = from.getJSONObject("main").getInt("humidity");
        double pressure = from.getJSONObject("main").getInt("pressure");
        double windSpeed = from.getJSONObject("wind").getDouble("speed");
        to.put("pressure", pressure);
        to.put("main", main);
        to.put("temp", temperature);
        to.put("humidity", humidity);
        to.put("wind speed", windSpeed);
    }

    public JSONObject getCurrentWeather(String city, String code) {
        try {
            String request = configs.getApiWeather().replaceAll("city", city).replaceAll("code", code);
            String response = requestResponseService.sendRequestGetResponse(request);
            JSONObject jsonWeather = new JSONObject(response);
            JSONObject result = new JSONObject();
            String main = jsonWeather.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = jsonWeather.getJSONObject("main").getDouble("temp");
            int humidity = jsonWeather.getJSONObject("main").getInt("humidity");
            double pressure = jsonWeather.getJSONObject("main").getInt("pressure");
            double windSpeed = jsonWeather.getJSONObject("wind").getDouble("speed");
            result.put("main", main);
            result.put("temp", temperature);
            result.put("humidity", humidity);
            result.put("wind speed", windSpeed);
            result.put("pressure", pressure);
            return result;
        } catch (JSONException ex) {
            return new JSONObject().put("exception", "JSONException");
        } catch (Exception ex) {
            return new JSONObject().put("exception", "Unknown");
        }
    }

}
