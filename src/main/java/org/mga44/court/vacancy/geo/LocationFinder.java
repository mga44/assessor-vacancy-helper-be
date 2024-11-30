package org.mga44.court.vacancy.geo;

import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.mga44.court.vacancy.CourtVacancy;
import org.mga44.court.vacancy.Sequencable;
import org.mga44.court.vacancy.Step;
import org.mga44.utils.FileWriter;
import org.mga44.utils.JsonMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
public class LocationFinder implements Sequencable<List<CourtVacancy>, List<GeolocatedCourtVacancy>> {
    final GeocodingService geoService = new GeocodingService();

    private static final Map<String, GeoInformation> GEO_CACHE = loadCache();

    private static Map<String, GeoInformation> loadCache() {
        try {
            return fromJsonMap(Files.readString(Path.of("cache/LocationFinder.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, GeoInformation> fromJsonMap(String map) {

        final Type mapType = new TypeToken<Map<String, GeoInformation>>() {
        }.getType();
        return JsonMapper.GSON.fromJson(map, mapType);
    }

    @Override
    public boolean enabled(Set<Step> enabled) {
        return enabled.contains(Step.GEO_COORDINATE);
    }

    @Override
    public List<GeolocatedCourtVacancy> execute(List<CourtVacancy> input) {
        final ArrayList<GeolocatedCourtVacancy> resultVacancies = new ArrayList<>();
        for (CourtVacancy vacancy : input) {
            GeoInformation coordinates = GEO_CACHE.get(vacancy.courtName());
            if (coordinates == null) {
                Optional<GeoInformation> fetched = getCoordinates(vacancy);
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
                    coordinates.lon(),
                    coordinates.city()
            ));
        }
        dumpCache(); //TODO: this seems to be more service oriented
        log.info("Found coordinates for {} vacancies", resultVacancies.size());
        return resultVacancies;
    }

    @Override
    public void writeResult(List<GeolocatedCourtVacancy> output) {
        FileWriter.writeToOut(LocationFinder.class, JsonMapper.toJson(output));
    }

    //TODO dump as different file
    private void dumpCache() {

        FileWriter.writeToResult(LocationFinder.class, JsonMapper.toJson(GEO_CACHE));
        GEO_CACHE.clear();
    }

    private Optional<GeoInformation> getCoordinates(CourtVacancy vacancy) {
        Optional<Coordinates> coordinates = geoService.getCoordinates(vacancy.courtName());
        Optional<String> city = LocalCourtService.getCity(vacancy.courtName());
        if (coordinates.isPresent()) {
            GeoInformation value = new GeoInformation(coordinates.get(), city.orElse(null));
            GEO_CACHE.put(vacancy.courtName(), value);
            return Optional.of(value);
        }

        coordinates = city.flatMap(geoService::getCoordinates);
        if (coordinates.isPresent()) {
            GeoInformation value = new GeoInformation(coordinates.get(), city.orElse(null));
            GEO_CACHE.put(vacancy.courtName(), value);
            return Optional.of(value);
        }

        log.warn("Skipping [{}] for now", vacancy.courtName());
        return Optional.empty();
    }

    public record GeoInformation(BigDecimal lat, BigDecimal lon, String city) {
        GeoInformation(Coordinates c, String city) {
            this(c.lat(),c.lon(), city);
        }
    }
}
