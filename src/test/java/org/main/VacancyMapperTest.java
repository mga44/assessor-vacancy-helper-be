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
                w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku \s
                S¹d Rejonowy w Gi¿ycku  1 II Wydzia³ Karny\s
                """;
        //when
        List<CourtVacancy> vacancies = new VacancyMapper().mapToVacancies(input);

        //then
        assertThat(vacancies)
                .containsOnly(new CourtVacancy(
                        "S¹d Rejonowy w Gi¿ycku",
                        "II Wydzia³ Karny",
                        1,
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku"
                ));
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
        List<CourtVacancy> vacancies = new VacancyMapper().mapToVacancies(input);

        //then
        assertThat(vacancies).containsOnly(
                new CourtVacancy(
                        "S¹d Rejonowy w Gi¿ycku",
                        "II Wydzia³ Karny",
                        1,
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku"
                ),
                new CourtVacancy(
                        "S¹d Rejonowy w Pu³tusku",
                        "I Wydzia³ Cywilny",
                        2,
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Bia³ymstoku"
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
        List<CourtVacancy> vacancies = new VacancyMapper().mapToVacancies(input);

        //then
        assertThat(vacancies).containsOnly(
                new CourtVacancy(
                        "S¹d Rejonowy w Bydgoszczy",
                        "XII Wydzia³ Cywilny",
                        1,
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Gdañsku"
                ),
                new CourtVacancy(
                        "S¹d Rejonowy w Bydgoszczy",
                        "IX Wydzia³ Karny",
                        1,
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Gdañsku"
                ),
                new CourtVacancy(
                        "S¹d Rejonowy dla Krakowa-Podgórza w Krakowie",
                        "XII Wydzia³ Cywilny",
                        2,
                        "w obszarze w³aœciwoœci S¹du Apelacyjnego w Gdañsku"
                )
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"Poz. 57Dziennik Urzêdowy Ministra Sprawiedliwoœci – 3 –\n"})
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