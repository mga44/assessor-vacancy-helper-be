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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
        FileWriter.writeToOut(AdditionalInformationEnricher.class, JsonMapper.toJson(result));
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


    static List<Population> POPULATION_CACHE = new ArrayList<>();

    record Population(String city, BigDecimal count) {
    }

    private String getPopulation(String city) {
        if (city == null) {
            return "N/A";
        }

        if (POPULATION_CACHE.isEmpty()) {
            init();
        }
        Optional<Population> possiblePopulation = POPULATION_CACHE.stream().filter(x -> x.city().startsWith(city)).findFirst();
        if (possiblePopulation.isEmpty()) {
            log.warn("Could not find population for city {}", city);
            return "N/A";
        }

        log.debug("Found [{}] for city [{}]", possiblePopulation.get(), city);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');

        DecimalFormat df = new DecimalFormat("#,##0.00 'tys.'", symbols);
        return df.format(possiblePopulation.get().count());
    }

    private static void init() {
        CSVFormat format = CSVFormat.Builder.create()
                .setHeader("Kod", "Nazwa", "ludno�� w tysi�cach")
                .setSkipHeaderRecord(true).setDelimiter(';').build();
        try (CSVParser parse = CSVParser.parse(Path.of(POPULATION_FILENAME), StandardCharsets.UTF_8, format)) {
            for (CSVRecord record : parse.getRecords()) {
                POPULATION_CACHE.add(new Population(
                        record.get("Nazwa").trim(),
                        new BigDecimal(record.get("ludno�� w tysi�cach").trim().replace(",", "."))
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}