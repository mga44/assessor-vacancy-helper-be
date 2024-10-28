package org.mga44.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.mga44.court.vacancy.CourtVacancy;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class JsonMapper {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    public static Map<String, List<String>> fromJsonMap(String map) {
        final Type mapType = new TypeToken<Map<?, List<?>>>() {
        }.getType();
        return GSON.fromJson(map, mapType);
    }

    public static List<CourtVacancy> fromJsonList(String list) {
        final Type listType = new TypeToken<List<CourtVacancy>>() {
        }.getType();
        return GSON.fromJson(list, listType);
    }
}
