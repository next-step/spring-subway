package subway.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

@SpringBootTest
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
        Long distance = 1L;
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
}
