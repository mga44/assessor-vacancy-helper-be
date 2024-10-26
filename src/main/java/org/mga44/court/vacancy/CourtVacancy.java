package org.mga44.court.vacancy;

public record CourtVacancy(
        String courtName,
        String courtDepartment,
        int vacancy,
        String appelation) {
}
