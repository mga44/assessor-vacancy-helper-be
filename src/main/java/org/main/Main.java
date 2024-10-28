package org.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.mga44.court.vacancy.*;
import org.mga44.court.vacancy.geo.Coordinates;
import org.mga44.court.vacancy.geo.GeocodingService;
import org.mga44.court.vacancy.geo.LocalCourtService;
import org.mga44.utils.FileWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
public class Main {

    private static final String FILENAME_1 = "src/main/resources/zal._nr_1_zarzadzenie_ministra_sprawiedliwosci.pdf";
    private static final String FILENAME_2 = "src/main/resources/zarzadzenie_ms_z_26.04.24_-_wykaz_wolnych_stanowisk_asesorskich.pdf";

    public static void main(String[] args) {
        final Set<Step> stepsForExecution = EnumSet.allOf(Step.class);

        String pdfTextContents = null;
        if (stepsForExecution.contains(Step.PARSE)) {
            final PDFCourtVacancyParser parser = new PDFCourtVacancyParser(FILENAME_1);
            pdfTextContents = parser.parsePDFFile();
            // prints to result/PDFCourtVacancyParser.out
        }

        Map<String, List<String>> sanitizedLanes = null;
        if (stepsForExecution.contains(Step.SANITIZE)) {
            if (pdfTextContents == null) {
                //        pdfTextContents =
            }
            sanitizedLanes = new LaneSanitizer().clean(pdfTextContents);
            // prints to result/PDFCourtVacancyParser.out //TODO
        }

        List<CourtVacancy> vacancies = null; // currently a blocker - perhapse custom mechanism of reading/dumping from file
        if (stepsForExecution.contains(Step.MAP)) {
            final VacancyMapper vacancyMapper = new VacancyMapper();
            vacancies = vacancyMapper.mapToVacancies(sanitizedLanes);
            log.info("Found {} vacancies", vacancies.size());
        }

        if (stepsForExecution.contains(Step.GEO_COORDINATE)) {
            final GeocodingService geoService = new GeocodingService();
            final ArrayList<CourtVacancyJson> resultVacancies = new ArrayList<>();
            for (CourtVacancy vacancy : vacancies) {
                Optional<Coordinates> coordinates = geoService.getCoordinates(vacancy.courtName());
                if (coordinates.isEmpty()) {
                    Optional<String> city = LocalCourtService.getCity(vacancy.courtName());
                    coordinates = city.flatMap(geoService::getCoordinates);
                    if (coordinates.isEmpty()) {
                        log.warn("Skipping [{}] for now", vacancy.courtName());
                        continue;
                    }
                }
                resultVacancies.add(new CourtVacancyJson(
                        vacancy.courtName(),
                        vacancy.courtDepartment(),
                        vacancy.vacancy(),
                        vacancy.appelation(),
                        coordinates.get().lat(),
                        coordinates.get().lon()
                ));
            }
            log.info("Found coordinates for {} vacancies", resultVacancies.size());
            FileWriter.writeToOut(Main.class, new GsonBuilder().setPrettyPrinting().create().toJson(resultVacancies));
            saveAsJson();
        }
    }

    private static void saveAsJson() {
        try {
            Files.copy(
                    Paths.get("out/Main_output.txt"),
                    Paths.get("out/coordinates.json"),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}