package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@SpringBootTest
@Transactional
class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @DisplayName("구간 저장 성공")
    @Test
    void saveSection() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 10L;
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        // when
        SectionResponse response = sectionService.saveSection(lineId, request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response).extracting(
                SectionResponse::getLineId,
                SectionResponse::getUpStationId,
                SectionResponse::getDownStationId,
                SectionResponse::getDistance
        ).contains(lineId, upStationId, downStationId, distance);

    }

    @DisplayName("하행 종점역과 새로운 구간의 상행역이 다르면 예외를 던진다")
    @Test
    void ifLastDownStationEqualToUpstationThrow() {
        // given
        Long lineId = 1L;
        sectionService.saveSection(lineId, new SectionRequest(1L, 2L, 10L));
        sectionService.saveSection(lineId, new SectionRequest(2L, 3L, 10L));

        Long upStationId = 4L;
        Long downStationId = 5L;
        Long distance = 10L;
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        // when , then
        Assertions.assertThatCode(() -> sectionService.saveSection(lineId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행 종점역과 새로운 구간의 상행역은 같아야합니다.");

    }

    @DisplayName("새로운 구간 하행역이 기존 노선에 존재하면 예외를 던진다.")
    @Test
    void ifLastDownStationAlreadyExistInLineThenThrow() {
        // given
        Long lineId = 1L;
        sectionService.saveSection(lineId, new SectionRequest(1L, 2L, 10L));
        sectionService.saveSection(lineId, new SectionRequest(2L, 3L, 10L));

        Long upStationId = 3L;
        Long downStationId = 1L;
        Long distance = 10L;
        SectionRequest request = new SectionRequest(upStationId, downStationId, distance);

        // when , then
        Assertions.assertThatCode(() -> sectionService.saveSection(lineId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새로운 구간 하행역이 기존 노선에 존재하면 안됩니다.");

    }

    @DisplayName("지하철 노선에 등록된 하행 종점역만 제거할 수 있다")
    @Test
    void deleteSectionTest() {
        // given
        Long lineId = 1L;
        Long lastStationId = 3L;
        SectionResponse sectionResponse1 = sectionService.saveSection(lineId,
                new SectionRequest(1L, 2L, 10L));
        SectionResponse sectionResponse2 = sectionService.saveSection(lineId,
                new SectionRequest(2L, lastStationId, 10L));

        // when, then
        assertThatCode(() -> sectionService.deleteSection(lineId, lastStationId))
                .doesNotThrowAnyException();
    }

    @DisplayName("지하철 노선에 등록된 하행 종점역이 아니면 예외를 던진다.")
    @Test
    void deleteSectionNotLastDownStationIdThenThrow() {
        // given
        Long lineId = 1L;
        Long lastStationId = 3L;
        SectionResponse sectionResponse1 = sectionService.saveSection(lineId,
                new SectionRequest(1L, 2L, 10L));
        SectionResponse sectionResponse2 = sectionService.saveSection(lineId,
                new SectionRequest(2L, lastStationId, 10L));

        // when, then
        assertThatCode(() -> sectionService.deleteSection(lineId, lastStationId + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
    }

    @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
    @Test
    void deleteSectionIfOneSectionThenThrow() {
        // given
        Long lineId = 1L;
        Long lastStationId = 2L;
        SectionResponse sectionResponse1 = sectionService.saveSection(lineId,
                new SectionRequest(1L, lastStationId, 10L));

        // when, then
        assertThatCode(() -> sectionService.deleteSection(lineId, lastStationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
    }
}
