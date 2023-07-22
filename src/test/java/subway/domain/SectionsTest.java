package subway.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
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

        Section firstSection = new Section(1L, lineA, stationA, stationB, 1);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 1);
        Section thirdSection = new Section(3L, lineA, stationC, stationD, 1);

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
        Section section = new Section(1L, lineA, stationA, stationB, 1);
        Section circularSection = new Section(2L, lineA, stationB, stationA, 1);

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
        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, lineA, stationC, stationD, 3);

        assertThatThrownBy(() -> new Sections(List.of(sectionA, sectionB)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간이 1개인 경우 구간을 삭제할 수 없다")
    void cannotRemoveOneSizeSections() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Section section = new Section(1L, lineA, stationA, stationB, 1);
        Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.removeLast(stationB))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주어진 역이 노선의 하행 종점역이 아니면 구간을 삭제할 수 없다")
    void cannotRemoveSectionIfNotFinalDownStation() {
        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        assertThatThrownBy(() -> sections.removeLast(stationA))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("마지막 구간을 삭제한다")
    void removeLast() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        Section removedSection = sections.removeLast(stationC);

        Sections expectedSections = new Sections(List.of(sectionA));
        assertThat(sections).isEqualTo(expectedSections);
        assertThat(removedSection).isEqualTo(sectionB);
    }

    @Test
    @DisplayName("다른 노선에 속한 구간들로 생성할 수 없습니다.")
    void cannotCreateWithSectionsOfOtherLines() {
        Line otherLine = new Line(2L, "lineB", "red");
        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, otherLine, stationB, stationC, 3);

        assertThatThrownBy(() -> new Sections(List.of(sectionA, sectionB)))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
