package org.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mga44.court.vacancy.CourtVacancy;
import org.mga44.court.vacancy.VacancyMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class VacancyMapperTest {
    @Test
    void shouldMapInput() {
        //given
        final String input = """
                w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku \s
                S�d Rejonowy w Gi�ycku  1 II Wydzia� Karny\s
                """;
        //when
        List<CourtVacancy> vacancies = new VacancyMapper().mapToVacancies(input);

        //then
        assertThat(vacancies)
                .containsOnly(new CourtVacancy(
                        "S�d Rejonowy w Gi�ycku",
                        "II Wydzia� Karny",
                        1,
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku"
                ));
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
        List<CourtVacancy> vacancies = new VacancyMapper().mapToVacancies(input);

        //then
        assertThat(vacancies).containsOnly(
                new CourtVacancy(
                        "S�d Rejonowy w Gi�ycku",
                        "II Wydzia� Karny",
                        1,
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku"
                ),
                new CourtVacancy(
                        "S�d Rejonowy w Pu�tusku",
                        "I Wydzia� Cywilny",
                        2,
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku"
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
        List<CourtVacancy> vacancies = new VacancyMapper().mapToVacancies(input);

        //then
        assertThat(vacancies).containsOnly(
                new CourtVacancy(
                        "S�d Rejonowy w Bydgoszczy",
                        "XII Wydzia� Cywilny",
                        1,
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Gda�sku"
                ),
                new CourtVacancy(
                        "S�d Rejonowy w Bydgoszczy",
                        "IX Wydzia� Karny",
                        1,
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Gda�sku"
                ),
                new CourtVacancy(
                        "S�d Rejonowy dla Krakowa-Podg�rza w Krakowie",
                        "XII Wydzia� Cywilny",
                        2,
                        "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Gda�sku"
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Poz. 57Dziennik Urz�dowy Ministra Sprawiedliwo�ci � 3 �\n"})
    void shouldHandleErrorsGracefully(String input) {
        //when
        List<CourtVacancy> vacancies = new VacancyMapper().mapToVacancies(input);

        //then
        assertThat(vacancies).isEmpty();
    }

    @Test
    void shouldHandleMultipleAppelations(){
        fail("TBA");
    }
}