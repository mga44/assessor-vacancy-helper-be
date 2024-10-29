package org.mga44.court.vacancy.enrich;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.mga44.court.vacancy.geo.Coordinates;
import org.mga44.court.vacancy.geo.GeolocatedCourtVacancy;
import org.mga44.utils.FileWriter;
import org.mga44.utils.JsonMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AdditionalInformationEnricher {

    private static final String POPULATION_FILENAME = "src/main/resources/LUDN_2425_CTAB_20241028202819_2023.csv";

    public List<EnrichedCourtVacancies> enrich(List<GeolocatedCourtVacancy> vacancies) {
        AtomicInteger i = new AtomicInteger(1);
        List<EnrichedCourtVacancies> result = vacancies.stream()
                .map(v -> new EnrichedCourtVacancies(
                        i.getAndIncrement(),
                        v.courtName(),
                        v.courtDepartment(),
                        v.vacancy(),
                        v.appelation(),
                        v.latitude(),
                        v.longitude(),
                        getPopulation(v.city()),
                        String.valueOf(calculateDistance(v.latitude(), v.longitude()))
                )).toList();


        FileWriter.writeToResult(AdditionalInformationEnricher.class, JsonMapper.toJson(result));
        return result;
    }

    private static final int EARTH_RADIUS = 6371;
    private static final DecimalFormat df = new java.text.DecimalFormat("# km");

    String calculateDistance(BigDecimal lat2, BigDecimal lon2) {
        Coordinates warsawCoordinates = new Coordinates(
                new BigDecimal("52.2337172"),
                new BigDecimal("21.071432235636493")
        );
        double lat1Rad = Math.toRadians(warsawCoordinates.lat().doubleValue());
        double lon1Rad = Math.toRadians(warsawCoordinates.lon().doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);
        double distance = Math.sqrt(x * x + y * y) * EARTH_RADIUS;

        return df.format(distance);
    }


    static Map<String, BigDecimal> POPULATION_CACHE = new HashMap<>();

    private String getPopulation(String city) {
        if (POPULATION_CACHE.isEmpty()) {
            init();
        }

        Optional<BigDecimal> possiblePopulation = Optional.ofNullable(POPULATION_CACHE.get(city));
        if(possiblePopulation.isEmpty()) {
            log.warn("Could not find population for city {}", city);
            return "N/A";
        }


        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');

        DecimalFormat df = new DecimalFormat("#,##0.00 'tys.'", symbols);
        return df.format(possiblePopulation.get());
    }

    private static void init() {
        try (CSVParser parse = CSVParser.parse(Path.of(POPULATION_FILENAME), StandardCharsets.UTF_8, CSVFormat.EXCEL.withHeader().withDelimiter(';'));) {
            for (CSVRecord record : parse.getRecords()) {
                POPULATION_CACHE.put(
                        record.get("Nazwa").trim(),
                        new BigDecimal(record.get("ludnoœæ w tysi¹cach").trim().replace(",", "."))
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}