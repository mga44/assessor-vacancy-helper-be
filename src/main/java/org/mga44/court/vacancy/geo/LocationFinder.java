package org.mga44.court.vacancy.geo;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.mga44.court.vacancy.CourtVacancy;
import org.mga44.utils.FileWriter;
import org.mga44.utils.JsonMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LocationFinder {
    final GeocodingService geoService = new GeocodingService();

    private static final Map<String, Coordinates> GEO_CACHE = loadCache();

    private static Map<String, Coordinates> loadCache() {
        try {
            return fromJsonMap(Files.readString(Path.of("result/LocationFinder.out")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Coordinates> fromJsonMap(String map) {

        final Type mapType = new TypeToken<Map<String, Coordinates>>() {
        }.getType();
        return JsonMapper.GSON.fromJson(map, mapType);
    }


    public List<GeolocatedCourtVacancy> findCoordinates(List<CourtVacancy> vacancies) {
        final ArrayList<GeolocatedCourtVacancy> resultVacancies = new ArrayList<>();
        for (CourtVacancy vacancy : vacancies) {
            Coordinates coordinates = GEO_CACHE.get(vacancy.courtName());
            if (coordinates == null) {
                Optional<Coordinates> fetched = getCoordinates(vacancy);
                if (fetched.isEmpty()) {
                    continue;
                }
                coordinates = fetched.get();
            }
            resultVacancies.add(new GeolocatedCourtVacancy(
                    vacancy.courtName(),
                    vacancy.courtDepartment(),
                    vacancy.vacancy(),
                    vacancy.appelation(),
                    coordinates.lat(),
                    coordinates.lon()
            ));
        }
        dumpCache();
        FileWriter.writeToOut(LocationFinder.class, JsonMapper.toJson(resultVacancies));
        return resultVacancies;
    }

    //TODO dump as different file
    private void dumpCache() {

        FileWriter.writeToResult(LocationFinder.class, JsonMapper.toJson(GEO_CACHE));
        GEO_CACHE.clear();
    }

    private Optional<Coordinates> getCoordinates(CourtVacancy vacancy) {
        Optional<Coordinates> coordinates = geoService.getCoordinates(vacancy.courtName());
        if (coordinates.isPresent()) {
            GEO_CACHE.put(vacancy.courtName(), coordinates.get());
            return coordinates;
        }

        Optional<String> city = LocalCourtService.getCity(vacancy.courtName());
        coordinates = city.flatMap(geoService::getCoordinates);
        if (coordinates.isPresent()) {
            GEO_CACHE.put(vacancy.courtName(), coordinates.get());
            return coordinates;
        }

        log.warn("Skipping [{}] for now", vacancy.courtName());
        return Optional.empty();
    }
}
