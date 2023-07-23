package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionTest {
    @Test
    @DisplayName("구간의 상행역과 하행역이 같은 경우 예외 반환")
    void sameStationsThrowError() {
        // given
        Station station = new Station("서울대입구역");

        // when, then
        assertThatCode(() -> new Section(station, station, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 같을 수 없습니다");
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

    @Test
    @DisplayName("입력된 섹션이 현재 섹션에 하나라도 겹치면, subtract된다 ")
    void subtractTest() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Section newSection = new Section
                (
                        new Station("서울대입구역"),
                        new Station("잠실역"),
                        4
                );
        // when
        Section subtractSection = section.subtract(newSection);
        // then
        assertThat(subtractSection).isEqualTo(new Section(new Station("잠실역"), new Station("신대방역"), 6));
    }


    @Test
    @DisplayName("입력된 섹션이 현재 섹션에 하나라도 겹치지 않으면, subtract 실패한다.")
    void subtractTestError() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Section newSection = new Section
                (
                        new Station("상도역"),
                        new Station("잠실역"),
                        4
                );
        // when & then
        assertThatCode(() -> section.subtract(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("입력으로 들어온 section이 현재 section에 포함되지 않습니다");
    }
}
