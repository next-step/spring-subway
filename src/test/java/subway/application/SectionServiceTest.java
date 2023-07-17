package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.withPrecision;

@DisplayName("구간 서비스 테스트")
@SpringBootTest
class SectionServiceTest {

    private final SectionService sectionService;

    SectionServiceTest(@Autowired final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @DisplayName("구간 생성 성공")
    @Test
    void createSection() {
        // given
        final Long lineId = 1L;
        final SectionRequest sectionRequest = new SectionRequest("4", "2", 10.0);

        // when
        final SectionResponse sectionResponse = sectionService.saveSection(lineId, sectionRequest);

        // then
        assertThat(sectionResponse.getId()).isNotNull();
        assertThat(sectionResponse.getLineId()).isNotNull();
        assertThat(sectionResponse.getDownStationId()).isEqualTo(4);
        assertThat(sectionResponse.getUpStationId()).isEqualTo(2);
        assertThat(sectionResponse.getDistance()).isEqualTo(10, withPrecision(1d));
    }
}
