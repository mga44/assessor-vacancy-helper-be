package org.mga44.court.vacancy.geo;

import java.math.BigDecimal;

public record GeolocatedCourtVacancy(
        String courtName,
        String courtDepartment,
        int vacancy,
        String appelation,
        BigDecimal latitude,
        BigDecimal longitude) {
}
