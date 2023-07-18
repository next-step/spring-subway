package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class SectionTest {
    @Test
    @DisplayName("구간의 상행역과 하행역이 같은 경우 예외 반환")
    void sameUpwardDownwardThrowError() {
        // given
        Station station = new Station("서울대입구역");
        Line line = new Line("2호선", "green");
        // when, then
        assertThatCode(() -> new Section(station, station, line, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 같을 수 없습니다");
    }

    @Test
    @DisplayName("구간 생성 테스트")
    void createSectionTest() {
        // given
        Station upStation = new Station("서울대입구역");
        Station downStation = new Station("신대방역");

        Line line = new Line("2호선", "green");
        // when, then
        assertThatCode(() -> new Section(upStation, downStation, line, 10))
                .doesNotThrowAnyException();
    }
}