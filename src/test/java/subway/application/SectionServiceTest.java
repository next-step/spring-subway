package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

@DisplayName("구간 서비스 테스트")
@SpringBootTest
class SectionServiceTest {

    private final SectionService sectionService;
    private final LineService lineService;
    private final StationService stationService;

    SectionServiceTest(@Autowired final SectionService sectionService,
                       @Autowired final LineService lineService,
                       @Autowired final StationService stationService) {
        this.sectionService = sectionService;
        this.lineService = lineService;
        this.stationService = stationService;
    }

    @BeforeEach
    void setUp() {
        lineService.saveLine(new LineRequest("1", "blue"));
        stationService.saveStation(new StationRequest("Incheon"));
    }

    @DisplayName("구간 생성 성공")
    @Test
    void createSection() {
        // given
        final Long lineId = 1L;
        final SectionRequest sectionRequest = new SectionRequest("1", "1", 10.0);

        // when
        final SectionResponse sectionResponse = sectionService.saveSection(lineId, sectionRequest);

        // then
        assertThat(sectionResponse.getId()).isNotNull();
        assertThat(sectionResponse.getLineId()).isNotNull();
        assertThat(sectionResponse.getDownStationId()).isEqualTo(1);
        assertThat(sectionResponse.getUpStationId()).isEqualTo(1);
        assertThat(sectionResponse.getDistance()).isEqualTo(10, withPrecision(1d));
    }
}
