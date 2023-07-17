package subway.application;

import static org.assertj.core.api.Assertions.assertThat;

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

}
