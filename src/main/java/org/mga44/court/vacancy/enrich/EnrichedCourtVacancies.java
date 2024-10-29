package org.mga44.court.vacancy.enrich;

import java.math.BigDecimal;

public record EnrichedCourtVacancies(
        int id,
        String courtName,
        String courtDepartment,
        int vacancy,
        String appelation,
        BigDecimal latitude,
        BigDecimal longitude,
        String cityPopulation,
        String distanceFromWarsaw
) {
}
