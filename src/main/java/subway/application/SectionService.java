package subway.application;

import org.springframework.transaction.annotation.Transactional;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

public interface SectionService {
    @Transactional
    SectionResponse saveSection(Long lineId, SectionRequest request);

    @Transactional
    void deleteSection(Long lineId, Long stationId);
}
