package org.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mga44.court.vacancy.LaneSanitizer;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LaneSanitizerTest {
    @Test
    void shouldGroupIntoLogicalStruct() {
        //given
        final String input = """
                w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku \s
                S�d Rejonowy w Gi�ycku  1 II Wydzia� Karny\s
                """;
        //when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(1)
                .containsEntry("w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku",
                        List.of("S�d Rejonowy w Gi�ycku  1 II Wydzia� Karny")
                );
    }

    @Test
    void shouldMapMultipleInGroup() {
        //given
        final String input = """
                w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku \s
                S�d Rejonowy w Gi�ycku  1 II Wydzia� Karny\s
                S�d Rejonowy w Pu�tusku  2 I Wydzia� Cywilny \s
                                """;
//when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(1)
                .containsEntry("w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku",
                        List.of(
                                "S�d Rejonowy w Gi�ycku  1 II Wydzia� Karny",
                                "S�d Rejonowy w Pu�tusku  2 I Wydzia� Cywilny"
                        )
                );

    }

    @Test
    void shouldHandleMultipleVacanciesInOneCourt() {
        //given
        String input = """
                w obszarze w�a�ciwo�ci S�du Apelacyjnego w Gda�sku\s
                S�d Rejonowy w Bydgoszczy\s
                1 XII Wydzia� Cywilny \s
                1 IX Wydzia� Karny
                S�d Rejonowy dla Krakowa-Podg�rza\s
                w Krakowie  2 XII Wydzia� Cywilny\s
                """;

        //when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(1)
                .containsEntry("w obszarze w�a�ciwo�ci S�du Apelacyjnego w Gda�sku",
                        List.of(
                                "S�d Rejonowy w Bydgoszczy 1 XII Wydzia� Cywilny",
                                "S�d Rejonowy w Bydgoszczy 1 IX Wydzia� Karny",
                                "S�d Rejonowy dla Krakowa-Podg�rza w Krakowie  2 XII Wydzia� Cywilny"
                        )
                );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Poz. 57Dziennik Urz�dowy Ministra Sprawiedliwo�ci � 3 �\n"})
    void shouldHandleErrorsGracefully(String input) {
        //when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleMultipleAppelations() {
        //given
        final String input = """
                w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku \s
                S�d Rejonowy w Gi�ycku  1 II Wydzia� Karny\s
                w obszarze w�a�ciwo�ci S�du Apelacyjnego w Gda�sku\s
                S�d Rejonowy w Bydgoszczy 1 XII Wydzia� Cywilny \s
                """;
        //when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku",
                        List.of("S�d Rejonowy w Gi�ycku  1 II Wydzia� Karny"),
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Gda�sku",
                        List.of("S�d Rejonowy w Bydgoszczy 1 XII Wydzia� Cywilny")
                ));
    }
}