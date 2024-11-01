package org.mga44.court.vacancy.enrich.population;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

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

@Slf4j
public class PopulationService {

    private static final String POPULATION_FILENAME = "src/main/resources/LUDN_2425_CTAB_20241028202819_2023.csv";

    static List<Population> POPULATION_DB = new ArrayList<>();


    public static String getPopulation(String city) {
        if (city == null) {
            return "N/A";
        }

        if (POPULATION_DB.isEmpty()) {
            init();
        }
        Optional<Population> possiblePopulation = POPULATION_DB.stream()
                .filter(x -> x.city().startsWith(city))
                .findFirst();
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
                .setHeader("Kod", "Nazwa", "ludnoœæ w tysi¹cach")
                .setSkipHeaderRecord(true)
                .setDelimiter(';')
                .build();
        try (CSVParser parse = CSVParser.parse(Path.of(POPULATION_FILENAME), StandardCharsets.UTF_8, format)) {
            for (CSVRecord record : parse.getRecords()) {
                POPULATION_DB.add(new Population(
                        record.get("Nazwa").trim(),
                        new BigDecimal(record.get("ludnoœæ w tysi¹cach").trim().replace(",", "."))
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    record Population(String city, BigDecimal count) {
    }
}
