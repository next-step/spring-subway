package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class SectionMergerTest {

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
    @DisplayName("추가할 구간의 크기가 같거나 큰 경우 합칠 수 없다.")
    void cannotMergeLargeOrSameLengthSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section sameLengthSection = new Section(lineA, stationA, stationB, 3);
        Section largeSection = new Section(lineA, stationA, stationB, 4);

        assertThatThrownBy(() -> section.mergeUpWith(sameLengthSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section.mergeUpWith(largeSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간의 상행역과 추가할 구간의 상행역이 같은 경우 합친다")
    void mergeSameUpStationSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section targetSection = new Section(lineA, stationA, stationB, 2);

        List<Section> mergedSections = SectionMerger.merge(section,targetSection);

        Section createdSection = new Section(lineA, stationB, stationC, 1);
        assertThat(mergedSections).isEqualTo(List.of(targetSection, createdSection));
    }

    @Test
    @DisplayName("구간의 하행역과 추가할 구간의 하행역이 같은 경우 합친다")
    void mergeSameDownStationSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section targetSection = new Section(lineA, stationB, stationC, 2);

        List<Section> mergedSections = SectionMerger.merge(section,targetSection);

        Section createdSection = new Section(lineA, stationA, stationB, 1);
        assertThat(mergedSections).isEqualTo(List.of(createdSection, targetSection));
    }

    @Test
    @DisplayName("구간의 상행역, 하행역이 추가할 구간의 상행역, 하행역과 모두 같거나 모두 다른 경우 합칠 수 없다.")
    void cannotMergeAllSameStationOrNothingSameStationSection() {
        Section section = new Section(lineA, stationA, stationB, 3);
        Section bothSameSection = new Section(lineA, stationA, stationB, 2);
        Section nothingSameSection = new Section(lineA, stationC, stationD, 2);

        assertThatThrownBy(() -> SectionMerger.merge(section,bothSameSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SectionMerger.merge(section,nothingSameSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
