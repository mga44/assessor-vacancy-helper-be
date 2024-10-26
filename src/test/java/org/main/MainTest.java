package org.main;

import org.junit.jupiter.api.Test;

class MainTest {
    private static final String FILENAME_1 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\zal._nr_1_zarzadzenie_ministra_sprawiedliwosci.pdf";
    private static final String FILENAME_2 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\zarzadzenie_ms_z_26.04.24_-_wykaz_wolnych_stanowisk_asesorskich.pdf";
    private static final String FILENAME_3 = "C:\\Users\\marku\\Documents\\GitHub\\lox\\AssessPlaceHelper\\src\\main\\resources\\2024-wyniki.pdf";

    @Test
    void shouldEncodePlCharsMain() {
        Main.main(new String[]{});
    }
}