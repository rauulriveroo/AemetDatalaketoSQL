package es.ulpgc.dacd.aemet.api.sensor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import es.ulpgc.dacd.aemet.api.model.Weather;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AemetApiReader implements Sensor {
    private static final String API_URL = "https://opendata.aemet.es/opendata/api/observacion/convencional/todas";
    private static final String apiKey = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYXV1bHJpdmVyb29AZ21haWwuY29tIiwianRpIjoiNDk4Y2RkZDgtY2UxZC00NGMxLWFkYTItZjc0Y2VhNDA3NTYwIiwiaXNzIjoiQUVNRVQiLCJpYXQiOjE2NzI2NjU0OTUsInVzZXJJZCI6IjQ5OGNkZGQ4LWNlMWQtNDRjMS1hZGEyLWY3NGNlYTQwNzU2MCIsInJvbGUiOiIifQ.FLxIx1Txh_pfVLx1B5hlPgznoEHO2vLxIxiyRzEFm30";

    public List<Weather> getData() {
        String response = getResponse(API_URL);

        JSONObject responsejson = new JSONObject(response);
        String urldatos = responsejson.getString("datos");

        String responsedata = getResponse(urldatos);

        return parseData(responsedata);
    }

    private List<Weather> parseData(String data) {
        JsonArray jsonArray = new JsonParser().parse(data).getAsJsonArray();
        List<Weather> datos = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            float lat = jsonObject.get("lat").getAsFloat();
            float lon = jsonObject.get("lon").getAsFloat();

            if (27.5 < lat && lat < 28.4 && -16 < lon && lon < -15) {
                String place = jsonObject.get("ubi").getAsString();
                String ts = jsonObject.get("fint").getAsString();
                String date = ts.substring(0, 10);
                String time = ts.substring(11);
                double temperature = jsonObject.get("ta").getAsDouble();
                String station = jsonObject.get("idema").getAsString();

                datos.add(new Weather(date, time, station, place, temperature));
            }
        }

        return datos;
    }

    private String getResponse(String url) {
        try {
            return Jsoup.connect(url)
                    .validateTLSCertificates(false)
                    .timeout(6000)
                    .ignoreContentType(true)
                    .header("accept", "application/json")
                    .header("api_key", apiKey)
                    .method(Connection.Method.GET)
                    .maxBodySize(0).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
