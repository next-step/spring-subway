package subway.service;

import org.junit.jupiter.api.Test;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.testdouble.InMemoryLineRepository;
import subway.testdouble.InMemorySectionRepository;
import subway.testdouble.InMemoryStationRepository;

import static org.junit.jupiter.api.Assertions.*;

class SectionServiceTest {
    private final LineRepository lineRepository = new InMemoryLineRepository();
    private final StationRepository stationRepository = new InMemoryStationRepository();
    private final SectionRepository sectionRepository = new InMemorySectionRepository();
    private final LineService lineService = new LineService(lineRepository);
    private final StationService stationService = new StationService(stationRepository);
    private final SectionService sectionService = new SectionService(lineService, stationService, sectionRepository);

    @Test
    void saveSection() {
    }

    @Test
    void deleteSectionByStationId() {
    }
}
