package org.mga44.court.vacancy;

import java.math.BigDecimal;

public record CourtVacancyJson(
        String courtName,
        String courtDepartment,
        int vacancy,
        String appelation,
        BigDecimal latitude,
        BigDecimal longitude) {
}
