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

    Line lineA;
    Station stationA;
    Station stationB;
    Station stationC;
    Station stationD;

    @BeforeEach
    void setUp() {
        lineA = new Line(1L, "A", "red");
        stationA = new Station(1L, "A");
        stationB = new Station(2L, "B");
        stationC = new Station(3L, "C");
        stationD = new Station(4L, "D");
    }

    @Test
    @DisplayName("구간 길이는 양수여야 한다.")
    void sectionDistanceShouldBePositive() {
        Station upStation = new Station(1L, "A");
        Station downStation = new Station(2L, "B");

        assertThatCode(() -> new Section(lineA, upStation, downStation, 1))
            .doesNotThrowAnyException();
        assertThatThrownBy(() -> new Section(lineA, upStation, downStation, -1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역은 같을 수 없다")
    void upStationAndDownStationShouldNotEqual() {
        assertThatThrownBy(() -> new Section(lineA, stationA, stationA, 1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자신의 하행역과 주어진 구간의 상행역이 같은지 확인한다.")
    void checkMyDownStationAndOtherUpStationEqual() {
        Section section = new Section(lineA, stationA, stationB, 1);
        Section otherSection = new Section(lineA, stationB, stationC, 1);

        assertThat(section.isUpperSectionOf(otherSection)).isTrue();
        assertThat(otherSection.isUpperSectionOf(section)).isFalse();
    }

    @Test
    @DisplayName("해당 역은 구간의 하행역과 같다")
    void hasDownStationSameAs() {
        Section section = new Section(lineA, stationA, stationB, 1);

        assertThat(section.hasDownStationSameAs(stationB)).isTrue();
        assertThat(section.hasDownStationSameAs(stationA)).isFalse();
    }

    @Test
    @DisplayName("구간은 주어진 라인의 소속이다")
    void sectionBelongToLine() {
        Section section = new Section(lineA, stationA, stationB, 1);

        assertThat(section.belongTo(lineA)).isTrue();
    }

    @Test
    @DisplayName("구간이 주어진 역을 포함한다.")
    void containsStation() {
        Section section = new Section(lineA, stationA, stationB, 1);

        assertThat(section.containsStation(stationA)).isTrue();
        assertThat(section.containsStation(stationC)).isFalse();
    }

    @Test
    @DisplayName("추가할 구간의 크기가 같거나 큰 경우 합칠 수 없다.")
    void cannotMergeLargeOrSameLengthSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section sameLengthSection = new Section(lineA, stationA, stationB, 3);
        Section largeSection = new Section(lineA, stationA, stationB, 4);

        assertThatThrownBy(() -> section.mergeSections(sameLengthSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section.mergeSections(largeSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간의 상행역과 추가할 구간의 상행역이 같은 경우 합친다")
    void mergeSameUpStationSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section targetSection = new Section(lineA, stationA, stationB, 2);

        List<Section> mergedSections = section.mergeSections(targetSection);

        Section createdSection = new Section(lineA, stationB, stationC, 1);
        assertThat(doSectionsHaveSameFields(mergedSections.get(0), targetSection)).isTrue();
        assertThat(doSectionsHaveSameFields(mergedSections.get(1), createdSection)).isTrue();
    }

    @Test
    @DisplayName("구간의 하행역과 추가할 구간의 하행역이 같은 경우 합친다")
    void mergeSameDownStationSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section targetSection = new Section(lineA, stationB, stationC, 2);

        List<Section> mergedSections = section.mergeSections(targetSection);

        Section createdSection = new Section(lineA, stationA, stationB, 1);
        assertThat(doSectionsHaveSameFields(mergedSections.get(0), createdSection)).isTrue();
        assertThat(doSectionsHaveSameFields(mergedSections.get(1), targetSection)).isTrue();
    }

    @Test
    @DisplayName("구간의 상행역, 하행역이 추가할 구간의 상행역, 하행역과 모두 같거나 모두 다른 경우 합칠 수 없다.")
    void cannotMergeAllSameStationOrNothingSameStationSection() {
        Section section = new Section(lineA, stationA, stationB, 3);
        Section bothSameSection = new Section(lineA, stationA, stationB, 2);
        Section nothingSameSection = new Section(lineA, stationC, stationD, 2);

        assertThatThrownBy(() -> section.mergeSections(bothSameSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section.mergeSections(nothingSameSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간과 상행역 또는 하행역이 겹치는지 여부를 반환한다.")
    void hasSameUpStationOrDownStation() {
        Section section = new Section(lineA, stationA, stationB, 3);
        Section upStationSameSection = new Section(lineA, stationA, stationC, 2);
        Section nothingSameSection = new Section(lineA, stationC, stationD, 2);

        assertThat(section.hasSameUpStationOrDownStation(upStationSameSection)).isTrue();
        assertThat(section.hasSameUpStationOrDownStation(nothingSameSection)).isFalse();
    }

    @Test
    @DisplayName("구간의 하행역과 재배치 대상 구간의 상행역이 같은 경우 재배치한다.")
    void rearrangeSections() {
        Section section = new Section(1L, lineA, stationA, stationB, 2);
        Section targetSection = new Section(2L, lineA, stationB, stationC, 5);

        Section rearrangedSection = section.rearrangeSections(targetSection);

        Section expectedSection = new Section(1L, lineA, stationA, stationC, 7);

        assertThat(rearrangedSection).isEqualTo(expectedSection);
        assertThat(doSectionsHaveSameFields(rearrangedSection, expectedSection)).isTrue();
    }

    @Test
    @DisplayName("구간의 하행역과 재배치 대상 구간의 상행역이 다른 경우 재배치할 수 없다.")
    void cannotRearrangeSections() {
        Section section = new Section(1L, lineA, stationA, stationB, 2);
        Section targetSection = new Section(2L, lineA, stationC, stationD, 5);

        assertThatThrownBy(() -> section.rearrangeSections(targetSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    static boolean doSectionsHaveSameFields(Section section, Section other) {
        return Objects.equals(section.getLine(), other.getLine())
            && Objects.equals(section.getUpStation(), other.getUpStation())
            && Objects.equals(section.getDownStation(), other.getDownStation())
            && section.getDistance() == other.getDistance();
    }
}
