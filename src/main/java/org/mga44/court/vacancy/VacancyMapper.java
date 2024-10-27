package org.mga44.court.vacancy;

import org.mga44.utils.FileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VacancyMapper {
    private static final Pattern COURT_NUMBER_DEP = Pattern.compile(" (?=\\d+ )");

    private static final Logger log = LoggerFactory.getLogger(VacancyMapper.class);

    public List<CourtVacancy> mapToVacancies(Map<String, List<String>> groupedByAppelation) {
        final List<CourtVacancy> vacancies = new ArrayList<>();
        for (var e : groupedByAppelation.entrySet()) {
            String appelation = e.getKey();
            List<String> courts = e.getValue();
            vacancies.addAll(courts.stream().map(verse -> getCourtVacancy(verse, appelation)).toList());
        }

        FileWriter.writeToOut(VacancyMapper.class,
                vacancies.stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining(System.lineSeparator()))
        );
        return vacancies;
    }

    private CourtVacancy getCourtVacancy(String verse, String appellation) {
        final String[] parts = COURT_NUMBER_DEP.split(verse, 2);
        final String court = parts[0].trim();
        final String[] numberAndDep = parts[1].split(" ", 2);
        return new CourtVacancy(
                court,
                numberAndDep[1].trim(),
                Integer.parseInt(numberAndDep[0]),
                appellation.trim()
        );
    }
}
