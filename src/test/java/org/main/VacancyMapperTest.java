package org.main;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mga44.court.vacancy.CourtVacancy;
import org.mga44.court.vacancy.VacancyMapper;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class VacancyMapperTest {
    @Test
    void shouldMapInput() {
        //given
        Map<String, List<String>> input = Map.of(
                "w obszarze w�a�ciwo�ci S�du Apelacyjnego w Bia�ymstoku",
                List.of("S�d Rejonowy w Gi�ycku 1 II Wydzia� Karny")
        );
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
}