package org.mga44.court.vacancy.geo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalCourtService {
    private static final String FILE_NAME = "src/main/resources/Dane_teleadresowe_s¹dów.csv";

    private static final Map<String, String> LOCAL_DB = new HashMap<>();


    public static Optional<String> getCity(String court) {
        if (LOCAL_DB.isEmpty())
            init();

        String city = LOCAL_DB.get(court);
        Optional<String> opt = Optional.ofNullable(city);
        if(opt.isEmpty()){
            log.warn("Could not find address for court: [{}]", court);
        }
        return opt;
    }

    private static void init() {
        try (CSVParser parse = CSVParser.parse(Path.of(FILE_NAME), StandardCharsets.UTF_8, CSVFormat.EXCEL.withHeader());) {
            for (CSVRecord record : parse.getRecords()) {
                LOCAL_DB.put(
                        record.get("Nazwa s¹du").trim(),
                    //    record.get("Ulica").trim() + ", " +
                                record.get("Miejscowoœæ").trim()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
