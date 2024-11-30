package org.mga44.court.vacancy.enrich;

import lombok.extern.slf4j.Slf4j;
import org.mga44.court.vacancy.Sequencable;
import org.mga44.court.vacancy.Step;
import org.mga44.court.vacancy.geo.GeolocatedCourtVacancy;
import org.mga44.utils.FileWriter;
import org.mga44.utils.JsonMapper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mga44.court.vacancy.enrich.distance.DistanceToWarsawCalculator.calculateDistance;
import static org.mga44.court.vacancy.enrich.population.PopulationService.getPopulation;

@Slf4j
public class AdditionalInformationEnricher implements Sequencable<List<GeolocatedCourtVacancy>, List<EnrichedCourtVacancies>> {

    @Override
    public boolean enabled(Set<Step> enabled) {
        return enabled.contains(Step.ENRICH);
    }

    @Override
    public List<EnrichedCourtVacancies> execute(List<GeolocatedCourtVacancy> input) {
        AtomicInteger i = new AtomicInteger(1);
        List<EnrichedCourtVacancies> result = input.stream()
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
        log.info("Enriched {} vacancies", result.size());
        return result;
    }

    @Override
    public void writeResult(List<EnrichedCourtVacancies> output) {
        FileWriter.writeToResult(AdditionalInformationEnricher.class, JsonMapper.toJson(output));
        FileWriter.writeToOut(AdditionalInformationEnricher.class, JsonMapper.toJson(output));
    }
}