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
                w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku \s
                S¹d Rejonowy w Gi¿ycku  1 II Wydzia³ Karny\s
                """;
        //when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(1)
                .containsEntry("w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku",
                        List.of("S¹d Rejonowy w Gi¿ycku  1 II Wydzia³ Karny")
                );
    }

    @Test
    void shouldMapMultipleInGroup() {
        //given
        final String input = """
                w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku \s
                S¹d Rejonowy w Gi¿ycku  1 II Wydzia³ Karny\s
                S¹d Rejonowy w Pu³tusku  2 I Wydzia³ Cywilny \s
                                """;
//when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(1)
                .containsEntry("w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku",
                        List.of(
                                "S¹d Rejonowy w Gi¿ycku  1 II Wydzia³ Karny",
                                "S¹d Rejonowy w Pu³tusku  2 I Wydzia³ Cywilny"
                        )
                );

    }

    @Test
    void shouldHandleMultipleVacanciesInOneCourt() {
        //given
        String input = """
                w obszarze w³aœciwoœci S¹du Apelacyjnego w Gdañsku\s
                S¹d Rejonowy w Bydgoszczy\s
                1 XII Wydzia³ Cywilny \s
                1 IX Wydzia³ Karny
                S¹d Rejonowy dla Krakowa-Podgórza\s
                w Krakowie  2 XII Wydzia³ Cywilny\s
                """;

        //when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(1)
                .containsEntry("w obszarze w³aœciwoœci S¹du Apelacyjnego w Gdañsku",
                        List.of(
                                "S¹d Rejonowy w Bydgoszczy 1 XII Wydzia³ Cywilny",
                                "S¹d Rejonowy w Bydgoszczy 1 IX Wydzia³ Karny",
                                "S¹d Rejonowy dla Krakowa-Podgórza w Krakowie  2 XII Wydzia³ Cywilny"
                        )
                );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Poz. 57Dziennik Urzêdowy Ministra Sprawiedliwoœci – 3 –\n"})
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
                w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku \s
                S¹d Rejonowy w Gi¿ycku  1 II Wydzia³ Karny\s
                w obszarze w³aœciwoœci S¹du Apelacyjnego w Gdañsku\s
                S¹d Rejonowy w Bydgoszczy 1 XII Wydzia³ Cywilny \s
                """;
        //when
        Map<String, List<String>> result = new LaneSanitizer().clean(input);

        //then
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku",
                        List.of("S¹d Rejonowy w Gi¿ycku  1 II Wydzia³ Karny"),
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Gdañsku",
                        List.of("S¹d Rejonowy w Bydgoszczy 1 XII Wydzia³ Cywilny")
                ));
    }
}