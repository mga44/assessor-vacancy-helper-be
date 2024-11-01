package org.mga44.court.vacancy.enrich;

import lombok.extern.slf4j.Slf4j;
import org.mga44.court.vacancy.geo.GeolocatedCourtVacancy;
import org.mga44.utils.FileWriter;
import org.mga44.utils.JsonMapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mga44.court.vacancy.enrich.distance.DistanceToWarsawCalculator.calculateDistance;
import static org.mga44.court.vacancy.enrich.population.PopulationService.getPopulation;

@Slf4j
public class AdditionalInformationEnricher {

    public List<EnrichedCourtVacancies> enrich(List<GeolocatedCourtVacancy> vacancies) {
        AtomicInteger i = new AtomicInteger(1);
        List<EnrichedCourtVacancies> result = vacancies.stream()
                .map(v -> new EnrichedCourtVacancies(
                        i.getAndIncrement(),
                        v.courtName(),
                        v.courtDepartment(),
                        v.vacancy(),
                        v.appelation(),
                        v.latitude(),
                        v.longitude(),
                        getPopulation(v.city()),
                        String.valueOf(calculateDistance(v.latitude(), v.longitude()))
                )).toList();

        FileWriter.writeToResult(AdditionalInformationEnricher.class, JsonMapper.toJson(result));
        FileWriter.writeToOut(AdditionalInformationEnricher.class, JsonMapper.toJson(result));
        return result;
    }
}