package subway.domain;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

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

        assertThatCode(() -> new Section(lineA, upStation, downStation, 1)).doesNotThrowAnyException();
        assertThatThrownBy(() -> new Section(lineA, upStation, downStation, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역은 같을 수 없다")
    void upStationAndDownStationShouldNotEqual() {
        assertThatThrownBy(() -> new Section(lineA, stationA, stationA, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자신의 하행역과 주어진 구간의 상행역이 같은지 확인한다.")
    void checkMyDownStationAndOtherUpStationEqual() {
        Section section = new Section(lineA, stationA, stationB, 1);
        Section otherSection = new Section(lineA, stationB, stationC, 1);

        assertThat(section.canPrecede(otherSection)).isTrue();
        assertThat(otherSection.canPrecede(section)).isFalse();
    }

    @Test
    @DisplayName("주어진 구간의 하행역을 포함하는지 확인한다.")
    void checkContainingDownStationOfSection() {
        Section section = new Section(lineA, stationA, stationB, 1);
        Section targetSection = new Section(lineA, stationC, stationB, 1);

        assertThat(section.containsDownStationOf(targetSection)).isTrue();
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
        assertThat(mergedSections).isEqualTo(List.of(targetSection, createdSection));
    }

    @Test
    @DisplayName("구간의 하행역과 추가할 구간의 하행역이 같은 경우 합친다")
    void mergeSameDownStationSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section targetSection = new Section(lineA, stationB, stationC, 2);

        List<Section> mergedSections = section.mergeSections(targetSection);

        Section createdSection = new Section(lineA, stationA, stationB, 1);
        assertThat(mergedSections).isEqualTo(List.of(createdSection, targetSection));
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

}
