package com.telegram_bot.Services;


import com.telegram_bot.Configurations.BotConfiguration;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoordinatesService {
    @Autowired
    BotConfiguration configs;

    @Autowired
    RequestResponseService requestResponseService;

    public String getTimeZone(String city, String code) {
        try {
            String requestLongLat;
            if (code.length()==0){
                requestLongLat = configs.getApiWeather().replaceAll("city,code", city);
            } else {
                requestLongLat = configs.getApiWeather().replaceAll("city", city).replaceAll("code", code);
            }
            String responseLongLat = requestResponseService.sendRequestGetResponse(requestLongLat);
            if (responseLongLat.equals("I have not found this city")){
                return responseLongLat;
            }
            JSONObject jsonWeather = new JSONObject(responseLongLat);
            String longitude = Double.toString(jsonWeather.getJSONObject("coord").getDouble("lon"));
            String latitude = Double.toString(jsonWeather.getJSONObject("coord").getDouble("lat"));
            String requestZone = configs.getApiZoneId().replaceAll("latitude", latitude).replaceAll("longitude", longitude);
            String responseZone = requestResponseService.sendRequestGetResponse(requestZone);
            JSONObject jsonZone = new JSONObject(responseZone);
            String zone = jsonZone.getString("zoneName");
            String result=zone;
            if (code.length()==0){
                String countryCode = jsonZone.getString("countryCode");
                result+=", "+countryCode;
            }
            return result;
        } catch (JSONException ex) {
            return "JSON has not parsed";
        } catch (Exception ex) {
            return "Unknown exception";
        }
    }

    public JSONObject getCityFromCoordinates(double lat, double lon) {
        try {
            String request = configs.getApiGeo().replaceAll("lat", Double.toString(lat)).replaceAll("lon", Double.toString(lon));
            String response = requestResponseService.sendRequestGetResponse(request);
            JSONObject json = new JSONObject(response);
            String city = json.getJSONArray("results").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getString("adminArea3");
            String code =json.getJSONArray("results").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getString("adminArea1");
            String state =json.getJSONArray("results").getJSONObject(0).getJSONArray("locations").getJSONObject(0).getString("adminArea5");
            JSONObject result = new JSONObject();
            result.put("city", city).put("code", code).put("state", state);
            return result;
        } catch (JSONException ex) {
            return new JSONObject().put("exception", "JSONException");
        } catch (Exception ex) {
            return new JSONObject().put("exception", "Unknown");
        }

    }
}
