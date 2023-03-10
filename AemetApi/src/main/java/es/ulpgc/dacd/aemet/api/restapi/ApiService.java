package es.ulpgc.dacd.aemet.api.restapi;

import com.google.gson.Gson;
import es.ulpgc.dacd.aemet.api.model.Weather;
import spark.Request;
import spark.Response;

import java.time.LocalDate;

import java.util.List;
import java.util.stream.Collectors;

import static es.ulpgc.dacd.aemet.api.Controller.maxweathers;
import static es.ulpgc.dacd.aemet.api.Controller.minweathers;
import static spark.Spark.get;
import static spark.Spark.port;


/**
 * The type Api service.
 */
public class ApiService implements WebService {
    public void start() {
        port(9000);
        get("/v1/places/with-max-temperature", ApiService::getMaxTemperatures);
        get("/v1/places/with-min-temperature", ApiService::getMinTemperatures);
    }

    private static String getMaxTemperatures(Request request, Response response) {
        response.header("content-type", "application/json");
        LocalDate from = LocalDate.parse(request.queryParams("from"));
        LocalDate to = LocalDate.parse(request.queryParams("to"));

        List<Weather> objetosFiltrados = maxweathers.stream()
                .filter(o -> o.getDate().isAfter(from) && o.getDate().isBefore(to))
                .collect(Collectors.toList());

        return toJson(objetosFiltrados);
    }

    private static String getMinTemperatures(Request request, Response response) {
        response.header("content-type", "application/json");
        LocalDate from = LocalDate.parse(request.queryParams("from"));
        LocalDate to = LocalDate.parse(request.queryParams("to"));

        List<Weather> objetosFiltrados = minweathers.stream()
                .filter(o -> o.getDate().isAfter(from) && o.getDate().isBefore(to))
                .collect(Collectors.toList());

        return toJson(objetosFiltrados);
    }

    private static String toJson(Object o) {
        return new Gson().toJson(o);
    }
}
