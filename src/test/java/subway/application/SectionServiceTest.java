package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.*;

@DisplayName("구간 서비스 테스트")
@SpringBootTest
class SectionServiceTest {

    private final SectionService sectionService;
    private final LineService lineService;
    private final StationService stationService;

    @Autowired
    SectionServiceTest(final SectionService sectionService,
                       final LineService lineService,
                       final StationService stationService) {
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

    @DisplayName("일치하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // given
        final Long lineId = 3L;
        final SectionRequest sectionRequest = new SectionRequest("1", "1", 10.0);

        // when
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sectionService.saveSection(lineId, sectionRequest));
    }
}
