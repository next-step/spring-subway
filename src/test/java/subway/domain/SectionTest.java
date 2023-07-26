package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Section 클래스 테스트")
class SectionTest {
    @Test
    @DisplayName("구간 길이는 양수여야 한다.")
    void sectionDistanceShouldBePositive() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

        //when & then
        assertThatCode(() -> new Section(1L, lineA, stationA, stationB, 1))
            .doesNotThrowAnyException();
        assertThatThrownBy(() -> new Section(1L, lineA, stationA, stationB, -1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역은 같을 수 없다")
    void upStationAndDownStationShouldNotEqual() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");

        //when & then
        assertThatThrownBy(() -> new Section(1L, lineA, stationA, stationA, 1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("해당 역은 구간의 상행역과 같다")
    void hasUpStation() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

        Section section = new Section(1L, lineA, stationA, stationB, 1);

        //when & then
        assertThat(section.hasUpStation(stationA)).isTrue();
        assertThat(section.hasUpStation(stationB)).isFalse();
    }

    @Test
    @DisplayName("해당 역은 구간의 하행역과 같다")
    void hasDownStation() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Section section = new Section(1L, lineA, stationA, stationB, 1);

        //when & then
        assertThat(section.hasDownStation(stationB)).isTrue();
        assertThat(section.hasDownStation(stationA)).isFalse();
    }

    @Test
    @DisplayName("구간과 상행역 또는 하행역이 겹친다.")
    void matchEitherStation() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section section = new Section(1L, lineA, stationA, stationB, 3);
        Section upStationMatchSection = new Section(2L, lineA, stationA, stationC, 2);
        Section noMatchSection = new Section(3L, lineA, stationC, stationD, 2);

        //when & then
        assertThat(section.matchEitherStation(upStationMatchSection)).isTrue();
        assertThat(section.matchEitherStation(noMatchSection)).isFalse();
    }

    @Test
    @DisplayName("구간이 주어진 길이보다 길지 않다.")
    void isNotLongerThan() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

        Section section = new Section(1L, lineA, stationA, stationB, 5);

        //when & then
        assertThat(section.isNotLongerThan(5)).isTrue();
        assertThat(section.isNotLongerThan(3)).isFalse();
    }

    @Test
    @DisplayName("대상 구간의 길이가 같거나 긴 경우 자를 수 없다.")
    void cannotCutByLargeOrSameLengthSection() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section section = new Section(1L, lineA, stationA, stationC, 3);
        Section sameLengthSection = new Section(2L, lineA, stationA, stationB, 3);
        Section largeSection = new Section(3L, lineA, stationA, stationB, 4);

        //when & then
        assertThatThrownBy(() -> section.cutBy(sameLengthSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section.cutBy(largeSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간의 상행역과 추가할 구간의 상행역이 같은 경우 자른다.")
    void cutUpperPart() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section section = new Section(1L, lineA, stationA, stationC, 6);
        Section targetSection = new Section(2L, lineA, stationA, stationB, 2);

        //when
        Section actual = section.cutBy(targetSection);

        //then
        Section expected = new Section(1L, lineA, stationB, stationC, 4);
        assertThat(actual).isEqualTo(expected);
        assertThat(doSectionsHaveSameFields(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("구간의 하행역과 추가할 구간의 하행역이 같은 경우 자른다.")
    void cutLowerPart() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section section = new Section(1L, lineA, stationA, stationC, 6);
        Section targetSection = new Section(2L, lineA, stationB, stationC, 2);

        //when
        Section actual = section.cutBy(targetSection);

        //then
        Section expected = new Section(1L, lineA, stationA, stationB, 4);
        assertThat(actual).isEqualTo(expected);
        assertThat(doSectionsHaveSameFields(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("구간의 상행역, 하행역이 추가할 구간의 상행역, 하행역과 모두 같거나 모두 다른 경우 자를 수 없다.")
    void cannotCutByFullMatchOrNoMatchSection() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section section = new Section(lineA, stationA, stationB, 3);
        Section fullMatchSection = new Section(lineA, stationA, stationB, 2);
        Section noMatchSection = new Section(lineA, stationC, stationD, 2);

        //when & then
        assertThatThrownBy(() -> section.cutBy(fullMatchSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section.cutBy(noMatchSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간을 연장한다.")
    void extendBy() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section section = new Section(1L, lineA, stationA, stationB, 2);
        Section targetSection = new Section(2L, lineA, stationB, stationC, 5);

        //when
        Section actual = section.extendBy(targetSection);

        //then
        Section expected = new Section(1L, lineA, stationA, stationC, 7);
        assertThat(actual).isEqualTo(expected);
        assertThat(doSectionsHaveSameFields(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("구간의 하행역과 대상 구간의 상행역이 다른 경우 연장할 수 없다.")
    void cannotExtendByNotConnectedSection() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section section = new Section(1L, lineA, stationA, stationB, 2);
        Section targetSection = new Section(2L, lineA, stationC, stationD, 5);

        //when & then
        assertThatThrownBy(() -> section.extendBy(targetSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    static boolean doSectionsHaveSameFields(Section section, Section other) {
        return Objects.equals(section.getLine(), other.getLine())
            && Objects.equals(section.getUpStation(), other.getUpStation())
            && Objects.equals(section.getDownStation(), other.getDownStation())
            && section.getDistance() == other.getDistance();
    }
}
