package org.mga44.court.vacancy.geo;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LocalCourtServiceTest {

    @Test
    void shouldGiveCity() {
        //given
        String input = "S�d Rejonowy w �om�y";
        //when
        String city = LocalCourtService.getCity(input).get();
        //then
        assertThat(city).isEqualTo("Polowa 1, �om�a");
    }

    @Test
    void shouldReturnEmptyIfNotFound() {
        //given
        String input = "S�d Rejonowy w New York";
        //when
        Optional<String> city = LocalCourtService.getCity(input);
        //then
        assertThat(city).isEmpty();
    }
}