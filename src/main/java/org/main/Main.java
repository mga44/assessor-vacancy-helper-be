package org.main;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mga44.court.vacancy.*;
import org.mga44.court.vacancy.geo.GeolocatedCourtVacancy;
import org.mga44.court.vacancy.geo.LocationFinder;
import org.mga44.utils.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class Main {

    private static final String FILENAME_1 = "src/main/resources/zal._nr_1_zarzadzenie_ministra_sprawiedliwosci.pdf";
    private static final String FILENAME_2 = "src/main/resources/zarzadzenie_ms_z_26.04.24_-_wykaz_wolnych_stanowisk_asesorskich.pdf";

    @SneakyThrows
    public static void main(String[] args) {
        final Set<Step> stepsForExecution = //EnumSet.allOf(Step.class);
                EnumSet.of(
             //   Step.PARSE,
                Step.SANITIZE,
                Step.MAP,
                Step.GEO_COORDINATE
        );

        String pdfTextContents = null;
        if (stepsForExecution.contains(Step.PARSE)) {
            final PDFCourtVacancyParser parser = new PDFCourtVacancyParser(FILENAME_1);
            pdfTextContents = parser.parsePDFFile();
        }

        Map<String, List<String>> sanitizedLanes = null;
        if (stepsForExecution.contains(Step.SANITIZE)) {
            if (pdfTextContents == null) {
                pdfTextContents = read(Path.of("result/PDFCourtVacancyParser.out"));
            }
            sanitizedLanes = new LaneSanitizer().clean(pdfTextContents);
        }

        List<CourtVacancy> vacancies = null;
        if (stepsForExecution.contains(Step.MAP)) {
            if (sanitizedLanes == null) {
                sanitizedLanes = JsonMapper.fromJsonMap(read(Path.of("result/LaneSanitizer.out")));
            }
            final VacancyMapper vacancyMapper = new VacancyMapper();
            vacancies = vacancyMapper.mapToVacancies(sanitizedLanes);
            log.info("Found {} vacancies", vacancies.size());
        }

        if (stepsForExecution.contains(Step.GEO_COORDINATE)) {
            if (vacancies == null) {
                vacancies = JsonMapper.fromJsonList(read(Path.of("result/VacancyMapper.out")));
            }

            List<GeolocatedCourtVacancy> resultVacancies = new LocationFinder().findCoordinates(vacancies);
            log.info("Found coordinates for {} vacancies", resultVacancies.size());
            saveAsJson();
        }
    }

    private static void saveAsJson() {
        try {
            Files.copy(
                    Paths.get("out/Main_output.txt"),
                    Paths.get("result/coordinates.json"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            log.error("Could not open file: {}", path);
            throw new RuntimeException(e);
        }
    }
}