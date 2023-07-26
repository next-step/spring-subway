package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;

import static org.assertj.core.api.Assertions.*;

class SectionTest {

    @DisplayName("생성 테스트")
    @Test
    void createSectionTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");

        // when & then
        assertThatNoException().isThrownBy(() -> new Section(line, station1, station2, 10));
    }

    @DisplayName("길이 유효성 검증 테스트")
    @Test
    void validateDistanceTest() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");

        // when & then
        assertThatThrownBy(() -> new Section(line, station1, station2, 0))
                .hasMessage("구간 길이는 0보다 커야합니다.")
                .isInstanceOf(IllegalSectionException.class);
    }
}
