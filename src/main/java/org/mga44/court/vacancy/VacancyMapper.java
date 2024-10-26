package org.mga44.court.vacancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class VacancyMapper {

    private static final Logger log = LoggerFactory.getLogger(VacancyMapper.class);

    private static final String APPELATION_HEADER = "w obszarze w³aœciwoœci";
    private static final String COURT_HEADER = "S¹d Rejonowy";

    public List<CourtVacancy> mapToVacancies(String text) {
        List<String> verses = Arrays.stream(text.split("\n"))
                .map(String::trim)
                .toList();

        final List<CourtVacancy> vacancies = new ArrayList<>();
        String appelation = "";
        String court = "";
        Iterator<String> iterator = verses.iterator();
        while (iterator.hasNext()) {
            String verse = iterator.next();
            if (verse.startsWith(APPELATION_HEADER)) {
                appelation = verse;
                continue;
            }

            if (verse.matches("^.* \\d+ .*$")) {
                // clasic structure "[court] [vacancy number] [department]"
                CourtVacancy result = getCourtVacancy(verse, appelation);
                vacancies.add(result);
                continue;
            }

            String previousVerse = verse;
            if (previousVerse.startsWith(COURT_HEADER)) {
                verse = iterator.next();
                ArrayList<Object> tmpVacancies = new ArrayList<>();
                while (!verse.startsWith(COURT_HEADER)) {
                    CourtVacancy result = getCourtVacancy(previousVerse + " " + verse, appelation);
                    tmpVacancies.add(result);
                }
                //todo

                if (tmpVacancies.isEmpty()) {
                    log.warn("Improper structure containing verses {}", verse);
                }

            } else {
                log.warn("Improper verse of value {}", previousVerse);
            }
//            CourtVacancy result = getCourtVacancy(verse, appelation);
//            vacancies.add(result);

        }

        return vacancies;
    }

    private CourtVacancy getCourtVacancy(String verse, String appelation) {
        String court;
        String[] parts = verse.split(" (?=\\d+ )", 2);
        court = parts[0].trim();
        String[] numberAndDep = parts[1].split(" ", 2);
        return new CourtVacancy(
                court,
                numberAndDep[1].trim(),
                Integer.parseInt(numberAndDep[0]),
                appelation.trim()
        );
    }
}
