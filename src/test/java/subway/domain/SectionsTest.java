package subway.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

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
    @DisplayName("정렬된 구간들을 생성한다.")
    void createSortedSections() {

        Section firstSection = new Section(lineA, stationA, stationB, 1);
        Section secondSection = new Section(lineA, stationB, stationC, 1);
        Section thirdSection = new Section(lineA, stationC, stationD, 1);

        Sections sections = new Sections(List.of(thirdSection, secondSection, firstSection));

        List<Section> expectedValues = List.of(firstSection, secondSection, thirdSection);
        List<Section> unexpectedValues = List.of(thirdSection, secondSection, firstSection);
        assertThat(sections.getValues()).isEqualTo(expectedValues);
        assertThat(sections.getValues()).isNotEqualTo(unexpectedValues);
    }

    @Test
    @DisplayName("순환된 구간으로 생성할 수 없다.")
    void cannotCreateWithCircularSection() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Section section = new Section(lineA, stationA, stationB, 1);
        Section circularSection = new Section(lineA, stationB, stationA, 1);

        assertThatThrownBy(() -> new Sections(List.of(section, circularSection)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("널이거나 비어 있는 구간 리스트로 생성할 수 없다.")
    void cannotCreateWithNullOrEmptySectionList() {

        assertThatThrownBy(() -> new Sections(null))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Sections(emptyList()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("끊어진 구간들로 생성할 수 없습니다.")
    void cannotCreateWithSeperatedSections() {
        Section sectionA = new Section(lineA, stationA, stationB, 3);
        Section sectionB = new Section(lineA, stationC, stationD, 3);

        assertThatThrownBy(() -> new Sections(List.of(sectionA, sectionB)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("삭제할 역이 구간들 내에 없는경우 삭제할 수 없다")
    void cannotRemoveStationNotInSections() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Section section = new Section(lineA, stationA, stationB, 1);
        Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.remove(stationC))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간이 1개인 경우 구간을 삭제할 수 없다")
    void cannotRemoveOneSizeSections() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Section section = new Section(lineA, stationA, stationB, 1);
        Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.remove(stationB))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("첫 구간을 삭제한다")
    void removeFirst() {
        Section sectionA = new Section(lineA, stationA, stationB, 1);
        Section sectionB = new Section(lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

         SectionRemovalResult sectionRemovalResult = sections.remove(stationA);

        Sections expectedSections = new Sections(List.of(sectionB));
        assertThat(sections).isEqualTo(expectedSections);
        assertThat(sectionRemovalResult.getSectionToAdd()).isEmpty();
        assertThat(sectionRemovalResult.getSectionToRemove()).containsExactly(sectionA);
    }

    @Test
    @DisplayName("중간 구간을 삭제한다")
    void removeMiddle() {
        Section sectionA = new Section(lineA, stationA, stationB, 1);
        Section sectionB = new Section(lineA, stationB, stationC, 2);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        SectionRemovalResult sectionRemovalResult = sections.remove(stationB);

        int sumOfDistances = sectionA.getDistance() + sectionB.getDistance();
        Section expectedSectionToAdd = new Section(lineA, stationA, stationC, sumOfDistances);
        Sections expectedSections = new Sections(List.of(expectedSectionToAdd));
        assertThat(sections).isEqualTo(expectedSections);
        assertThat(sectionRemovalResult.getSectionToRemove()).containsExactly(sectionA, sectionB);
        assertThat(sectionRemovalResult.getSectionToAdd()).isEqualTo(Optional.of(expectedSectionToAdd));
    }

    @Test
    @DisplayName("마지막 구간을 삭제한다")
    void removeLast() {
        Section sectionA = new Section(lineA, stationA, stationB, 1);
        Section sectionB = new Section(lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        SectionRemovalResult sectionRemovalResult = sections.remove(stationC);

        Sections expectedSections = new Sections(List.of(sectionA));
        assertThat(sections).isEqualTo(expectedSections);
        assertThat(sectionRemovalResult.getSectionToRemove()).containsExactly(sectionB);
        assertThat(sectionRemovalResult.getSectionToAdd()).isEmpty();
    }

    @Test
    @DisplayName("구간들의 역 목록을 순서대로 가져온다")
    void getStationOfSections() {
        Section sectionA = new Section(lineA, stationA, stationB, 1);
        Section sectionB = new Section(lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        List<Station> stations = sections.getStations();

        assertThat(stations).containsExactly(stationA, stationB, stationC);
    }
}
