package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThatCode;

class SectionTest {
    @Test
    @DisplayName("구간의 상행역과 하행역이 같은 경우 예외 반환")
    void sameStationsThrowError() {
        // given
        Station station = new Station("서울대입구역");

        // when, then
        assertThatCode(() -> new Section(station, station, 10))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.SECTION_SAME_STATIONS.getMessage());
    }

    @Test
    @DisplayName("구간 생성 테스트")
    void createSectionTest() {
        // given
        Station upStation = new Station("서울대입구역");
        Station downStation = new Station("신대방역");

        // when, then
        assertThatCode(() -> new Section(upStation, downStation, 10))
                .doesNotThrowAnyException();
    }
}