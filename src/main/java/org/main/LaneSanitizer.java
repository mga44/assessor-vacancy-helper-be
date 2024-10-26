package org.main;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LaneSanitizer {
    private static final String APPELATION_HEADER = "w obszarze w³aœciwoœci";
    private static final String COURT_HEADER = "S¹d Rejonowy";

    public Map<String, List<String>> clean(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return Collections.emptyMap();
        }
        List<String> verses = Arrays.stream(text.split("\n"))
                .map(String::trim)
                .toList();

        Map<String, List<String>> groupedLines = new HashMap<>();
        String currentAppelation = "";
        String currentCourt = "";
        for (String verse : verses) {
            if (!verse.startsWith(APPELATION_HEADER) && !verse.startsWith(COURT_HEADER) && !verse.trim().contains("Wydzia³")) {
                log.warn("Omitting verse with unidentified structure: {}", verse);
                continue;
            }

            if (verse.startsWith(APPELATION_HEADER)) {
                currentAppelation = verse.trim();
                groupedLines.put(currentAppelation, new ArrayList<>());
                continue;
            }

            // not full info provided
            if (verse.startsWith(COURT_HEADER) && !verse.matches("^.* \\d+ .*$")) {
                currentCourt = verse.trim();
                continue;
            }

            if (verse.startsWith(COURT_HEADER) && verse.matches("^.* \\d+ .*$")) {
                groupedLines.get(currentAppelation).add(verse);
            } else {
                // multiple vacancies or linebreak
                String possibleCourt = currentCourt + " " + verse.trim();
                if (possibleCourt.matches("^.* \\d+ .*$")) {
                    groupedLines.get(currentAppelation).add(possibleCourt);
                } else {
                    log.warn("Could not parse verse: {}", verse);
                }
            }
        }
        FileWriter.writeContentsToFile(LaneSanitizer.class,
                groupedLines.entrySet().stream()
                        .map(e -> e.getKey() + System
                                .lineSeparator() + e.getValue().stream().collect(Collectors.joining(System.lineSeparator())))
                        .collect(Collectors.joining()));

        return groupedLines;
    }
}
