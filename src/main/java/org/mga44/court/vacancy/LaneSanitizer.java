package org.mga44.court.vacancy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mga44.utils.FileWriter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class LaneSanitizer {
    private static final String APPELATION_HEADER = "w obszarze w³aœciwoœci";
    private static final String COURT_HEADER = "S¹d Rejonowy";

    private static final Pattern COURT_NUMBER_DEP = Pattern.compile("^.* \\d+ .*$");

    public Map<String, List<String>> clean(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyMap();
        }
        final List<String> verses = Arrays.stream(text.split("\n"))
                .map(String::trim)
                .toList();

        final Map<String, List<String>> groupedLines = new HashMap<>();
        String currentAppelation = "";
        String currentCourt = "";
        for (String verse : verses) {
            if (!verse.startsWith(APPELATION_HEADER) && !verse.startsWith(COURT_HEADER) && !verse.trim().contains("Wydzia³")) {
                log.warn("Omitting verse [{}] with unidentified structure: {}", verses.indexOf(verse), verse);
                continue;
            }

            if (verse.startsWith(APPELATION_HEADER)) {
                currentAppelation = verse.trim();
                groupedLines.put(currentAppelation, new ArrayList<>());
                continue;
            }

            final boolean hasSimpleStructure = COURT_NUMBER_DEP.matcher(verse).matches();
            // not full info provided
            if (verse.startsWith(COURT_HEADER) && !hasSimpleStructure) {
                currentCourt = verse.trim();
                continue;
            }
            //TODO: think about other approach - read inputs until you get Sad
            if (verse.startsWith(COURT_HEADER) && hasSimpleStructure) {
                groupedLines.get(currentAppelation).add(verse);
            } else {
                // multiple vacancies or linebreak
                final String possibleCourt = currentCourt + " " + verse.trim();
                if (COURT_NUMBER_DEP.matcher(possibleCourt).matches()) {
                    groupedLines.get(currentAppelation).add(possibleCourt);
                } else {
                    log.warn("Could not parse verse [{}]: [{}]", verses.indexOf(verse), verse);
                }
            }
        }

        FileWriter.writeToOut(LaneSanitizer.class, prepareForPrettyPrint(groupedLines));
        Integer courts = groupedLines.values().stream().map(List::size).reduce(0, Integer::sum);
        int lines = groupedLines.size() + courts;
        log.info("Sanitized [{}] lines, with [{}] courts", lines, courts);
        return groupedLines;
    }

    private String prepareForPrettyPrint(Map<String, List<String>> groupedLines) {
        return groupedLines.entrySet().stream()
                .map(e -> e.getKey() + System
                        .lineSeparator() + e.getValue().stream()
                        .map(x -> " - " + x)
                        .collect(Collectors.joining(System.lineSeparator())))
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
