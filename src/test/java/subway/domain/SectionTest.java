package subway.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;

class SectionTest {

    @DisplayName("생성 테스트")
    @Test
    void createSectionTest() {
        // given
        long lindId = 1L;
        long upStationId = 2L;
        long downStationId = 4L;
        int distance = 10;

        // when & then
        assertThatNoException().isThrownBy(() -> new Section(lindId,  upStationId, downStationId, distance));
    }

    @DisplayName("길이 유효성 검증 테스트")
    @Test
    void validateDistanceTest() {
        // given
        long lindId = 1L;
        long upStationId = 2L;
        long downStationId = 4L;
        int invalidDistance = 0;

        // when & then
        assertThatThrownBy(() -> new Section(lindId,  upStationId, downStationId, invalidDistance))
                .hasMessage("구간 길이는 0보다 커야한다.")
                .isInstanceOf(IllegalSectionException.class);
    }
}
