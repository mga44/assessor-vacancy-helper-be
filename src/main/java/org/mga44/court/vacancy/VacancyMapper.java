package org.mga44.court.vacancy;

import lombok.extern.slf4j.Slf4j;
import org.mga44.utils.FileWriter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class VacancyMapper implements Sequencable<Map<String, List<String>>, List<CourtVacancy>> {
    private static final Pattern COURT_NUMBER_DEP = Pattern.compile(" (?=\\d+ )");

    @Override
    public boolean enabled(Set<Step> enabled) {
        return enabled.contains(Step.MAP);
    }

    @Override
    public List<CourtVacancy> execute(Map<String, List<String>> input) {
        final List<CourtVacancy> vacancies = new ArrayList<>();
        for (var e : input.entrySet()) {
            String appelation = e.getKey();
            List<String> courts = e.getValue();
            vacancies.addAll(courts.stream().map(verse -> getCourtVacancy(verse, appelation)).toList());
        }
        log.info("Found {} vacancies", vacancies.size());
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

    @Override
    public void writeResult(List<CourtVacancy> output) {
        FileWriter.writeToOut(VacancyMapper.class,
                output.stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    public List<CourtVacancy> mapToVacancies(Map<String, List<String>> groupedByAppelation) {
        final List<CourtVacancy> vacancies = new ArrayList<>();
        for (var e : groupedByAppelation.entrySet()) {
            String appelation = e.getKey();
            List<String> courts = e.getValue();
            vacancies.addAll(courts.stream().map(verse -> getCourtVacancy(verse, appelation)).toList());
        }

        return vacancies;
    }
}
