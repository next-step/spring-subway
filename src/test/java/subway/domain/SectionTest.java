package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static subway.exception.ErrorCode.SAME_UP_AND_DOWN_STATION;
import static subway.exception.ErrorCode.SECTION_DOES_NOT_CONTAIN_SECTION;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;

class SectionTest {

    @Test
    @DisplayName("구간의 상행역과 하행역이 같은 경우 예외 반환")
    void sameStationsThrowError() {
        // given
        Station station = new Station("서울대입구역");

        // when, then
        assertThatCode(() -> new Section(station, station, 10))
            .isInstanceOf(SubwayException.class)
            .hasMessage(SAME_UP_AND_DOWN_STATION.getMessage());
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
    @DisplayName("입력된 섹션이 현재 섹션에 하나라도 겹칠 때, subtract 함수를 통해 구간의 차를 반환할 수 있다. ")
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
        assertThat(subtractSection).isEqualTo(
            new Section(new Station("잠실역"), new Station("신대방역"), 6));
    }


    @Test
    @DisplayName("입력된 섹션이 현재 섹션에 하나라도 겹치지 않으면, subtract가 실패한다.")
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
            .isInstanceOf(SubwayException.class)
            .hasMessage(SECTION_DOES_NOT_CONTAIN_SECTION.getMessage());
    }

    @Test
    @DisplayName("입력된 섹션과 현재 섹션이 겹칠 때, add 함수를 통해 구간을 합칠 수 있다. ")
    void 입력된_구간_현재_섹션_겹치면_add_함수_성공() {
        // given
        Section section = new Section(
            new Station("서울대입구역"),
            new Station("신대방역"),
            10
        );
        Section newSection = new Section(
            new Station("신대방역"),
            new Station("잠실역"),
            4
        );
        // when
        Section addSection = section.add(newSection);
        // then
        assertThat(addSection).isEqualTo(
            new Section(new Station("서울대입구역"), new Station("잠실역"), 14));
    }

    @Test
    @DisplayName("입력된 섹션과 현재 섹션이 겹치지 않으면, add 함수는 오류를 반환한다")
    void 입력된_구간_현재_섹션_겹치지_않으면_add_함수_실패() {
        // given
        Section section = new Section(
            new Station("서울대입구역"),
            new Station("신대방역"),
            10
        );
        Section newSection = new Section(
            new Station("상도역"),
            new Station("잠실역"),
            4
        );
        // when & then
        assertThatCode(() -> section.add(newSection))
            .isInstanceOf(SubwayException.class)
            .hasMessage(SECTION_DOES_NOT_CONTAIN_SECTION.getMessage());
    }
}
