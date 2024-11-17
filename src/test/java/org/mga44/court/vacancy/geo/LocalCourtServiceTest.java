package org.mga44.court.vacancy.geo;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class LocalCourtServiceTest {

    @Test
    void shouldGiveCity() {
        //given
        String input = "S퉐 Rejonowy w 즣m퓓";
        //when
        String city = LocalCourtService.getCity(input).get();
        //then
        assertThat(city).isEqualTo("즣m풹");
    }

    @Test
    void shouldReturnEmptyIfNotFound() {
        //given
        String input = "S퉐 Rejonowy w New York";
        //when
        Optional<String> city = LocalCourtService.getCity(input);
        //then
        assertThat(city).isEmpty();
    }

    @Test
    void shouldSanitizeInput() {
        fail("TBA");
    }
}