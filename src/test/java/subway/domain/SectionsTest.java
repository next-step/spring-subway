package subway.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static subway.domain.SectionTest.doSectionsHaveSameFields;

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
    Station stationE;

    @BeforeEach
    void setUp() {
        lineA = new Line(1L, "A", "red");
        stationA = new Station(1L, "A");
        stationB = new Station(2L, "B");
        stationC = new Station(3L, "C");
        stationD = new Station(4L, "D");
        stationE = new Station(5L, "E");
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
    @DisplayName("끊어진 구간들로 생성할 수 없다.")
    void cannotCreateWithSeperatedSections() {
        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, lineA, stationC, stationD, 3);

        assertThatThrownBy(() -> new Sections(List.of(sectionA, sectionB)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다른 노선에 속한 구간들로 생성할 수 없다.")
    void cannotCreateWithSectionsOfOtherLines() {
        Line otherLine = new Line(2L, "lineB", "red");
        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, otherLine, stationB, stationC, 3);

        assertThatThrownBy(() -> new Sections(List.of(sectionA, sectionB)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가할 구간의 한 역만 기존 노선에 포함되어야 한다.")
    void canAddSectionToLineContainingOnlyOneStation() {
        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section bothContainedSection = new Section(lineA, stationA, stationC, 3);
        Section bothNotContainedSection = new Section(lineA, stationD, stationE, 3);

        assertThatThrownBy(() -> sections.add(bothContainedSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sections.add(bothNotContainedSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선의 특정 구간과 상행역만 같은 경우 노선에 추가한다.")
    void addSectionOfSameUpStationAsLinesSection() {
        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationB, stationC, 3);

        Section result = sections.add(targetSection).get();

        Section sectionToUpdate = new Section(2L, lineA, stationC, stationD, 2);
        assertThat(result.getId()).isEqualTo(sectionToUpdate.getId());
        assertThat(doSectionsHaveSameFields(result, sectionToUpdate)).isTrue();
        assertThat(sections).isEqualTo(new Sections(List.of(firstSection, targetSection, result)));
    }

    @Test
    @DisplayName("노선의 특정 구간과 하행역만 같은 경우 노선에 추가한다.")
    void addSectionOfSameDownStationAsLinesSection() {
        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationC, stationD, 3);

        Section result = sections.add(targetSection).get();

        Section sectionToUpdate = new Section(2L, lineA, stationB, stationC, 2);
        assertThat(result.getId()).isEqualTo(sectionToUpdate.getId());
        assertThat(doSectionsHaveSameFields(result, sectionToUpdate)).isTrue();
        assertThat(sections).isEqualTo(new Sections(List.of(firstSection, result, targetSection)));
    }

    @Test
    @DisplayName("추가할 구간의 하행역이 기존 노선의 상행 종점역과 같은 경우 노선의 맨 앞에 추가한다.")
    void addFirst() {
        Section firstSection = new Section(1L, lineA, stationB, stationC, 5);
        Section secondSection = new Section(2L, lineA, stationC, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationA, stationB, 3);

        Optional<Section> result = sections.add(targetSection);

        assertThat(result).isEmpty();
        assertThat(sections)
            .isEqualTo(new Sections(List.of(targetSection, firstSection, secondSection)));
    }

    @Test
    @DisplayName("추가할 구간의 상행역이 기존 노선의 하행 종점역과 같은 경우 노선의 맨 뒤에 추가한다.")
    void addLast() {
        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationC, stationD, 3);

        Optional<Section> result = sections.add(targetSection);

        assertThat(result).isEmpty();
        assertThat(sections).isEqualTo(
            new Sections(List.of(firstSection, secondSection, targetSection)));
    }
    @Test
    @DisplayName("구간이 1개인 경우 구간을 삭제할 수 없다")
    void cannotRemoveOneSizeSections() {
        Section section = new Section(1L, lineA, stationA, stationB, 1);
        Sections sections = new Sections(List.of(section));

        assertThatThrownBy(() -> sections.remove(stationB))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("하행 종점이 제거될 경우 다음으로 오던 역이 종점이 된다.")
    void removeLast() {
        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        Optional<Section> sectionToUpdate = sections.remove(stationC);

        Sections expectedSections = new Sections(List.of(sectionA));
        assertThat(sections).isEqualTo(expectedSections);
        assertThat(sectionToUpdate).isEmpty();
    }

    @Test
    @DisplayName("상행 종점이 제거될 경우 다음으로 오던 역이 종점이 된다.")
    void removeFirst() {
        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        Optional<Section> sectionToUpdate = sections.remove(stationA);

        Sections expectedSections = new Sections(List.of(sectionB));
        assertThat(sections).isEqualTo(expectedSections);
        assertThat(sectionToUpdate).isEmpty();
    }

    @Test
    @DisplayName("노선에 등록되어 있지 않은 역을 제거할 수 없다.")
    void cannotRemoveUnregisteredStation() {
        Section sectionA = new Section(1L, lineA, stationA, stationB, 2);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        assertThatThrownBy(() -> sections.remove(stationD))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("중간역을 삭제할 경우 재배치를 하고, 거리는 두 구간의 거리 합이 된다.")
    void removeInMiddle() {
        Section sectionA = new Section(1L, lineA, stationA, stationB, 2);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 5);
        Section sectionC = new Section(3L, lineA, stationC, stationD, 6);
        Sections sections = new Sections(List.of(sectionA, sectionB, sectionC));

        Section sectionToUpdate = sections.remove(stationB).get();

        Section expectedSection = new Section(1L, lineA, stationA, stationC, 7);
        assertThat(doSectionsHaveSameFields(sectionToUpdate, expectedSection)).isTrue();
        assertThat(sections).isEqualTo(new Sections(List.of(expectedSection, sectionC)));
    }
}
