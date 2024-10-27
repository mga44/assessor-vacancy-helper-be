package org.main;

import com.google.gson.Gson;
import org.mga44.court.vacancy.*;
import org.mga44.court.vacancy.geo.Coordinates;
import org.mga44.court.vacancy.geo.GeocodingService;
import org.mga44.utils.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String FILENAME_1 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\zal._nr_1_zarzadzenie_ministra_sprawiedliwosci.pdf";
    private static final String FILENAME_2 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\zarzadzenie_ms_z_26.04.24_-_wykaz_wolnych_stanowisk_asesorskich.pdf";
    private static final String FILENAME_3 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\2024-wyniki.pdf";

    public static void main(String[] args) {
        final PDFCourtVacancyParser parser = new PDFCourtVacancyParser(FILENAME_1);
        final String pdfTextContents = parser.parsePDFFile();
        final Map<String, List<String>> sanitizedLanes = new LaneSanitizer().clean(pdfTextContents);
        final VacancyMapper vacancyMapper = new VacancyMapper();
        final List<CourtVacancy> vacancies = vacancyMapper.mapToVacancies(sanitizedLanes);
        logger.info("Found {} vacancies", vacancies.size());

        final GeocodingService geoService = new GeocodingService();
        final ArrayList<CourtVacancyJson> resultVacancies = new ArrayList<>();
        for (CourtVacancy vacancy : vacancies) {
            final Optional<Coordinates> coordinates = geoService.getCoordinates(vacancy.courtName());
            if(coordinates.isEmpty()) {
                logger.warn("Skipping [{}] for now", vacancy.courtName());
            } else {
                resultVacancies.add(new CourtVacancyJson(
                        vacancy.courtName(),
                        vacancy.courtDepartment(),
                        vacancy.vacancy(),
                        vacancy.appelation(),
                        coordinates.get().lat(),
                        coordinates.get().lon()
                ));
            }
        }
        logger.info("Found coordinates for {} vacancies", resultVacancies.size());
        FileWriter.writeContentsToFile(Main.class, new Gson().toJson(resultVacancies));
        saveAsJson();
    }

    private static void saveAsJson() {
        try {
            Files.copy(Paths.get("out/Main_output.txt"), Paths.get("out/coordinates.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}