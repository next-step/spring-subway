package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.vo.SectionAdditionResult;

class SectionsAddTest {

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
    @DisplayName("다른 라인의 구간이 추가될 수 없다.")
    void cannotAddSectionOfOtherLine() {
        Section firstSection = new Section(lineA, stationA, stationB, 5);
        Section secondSection = new Section(lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Line otherLine = new Line(2L, "B", "green");
        Section otherLineSection = new Section(otherLine, stationC, stationD, 3);

        assertThatThrownBy(() -> sections.add(otherLineSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가할 구간의 한 역만 기존 노선에 포함되어야 한다.")
    void canAddSectionToLineContainingOnlyOneStation() {
        Section firstSection = new Section(lineA, stationA, stationB, 5);
        Section secondSection = new Section(lineA, stationB, stationC, 5);
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
    void tes1() {
        Section firstSection = new Section(lineA, stationA, stationB, 5);
        Section secondSection = new Section(lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationB, stationC, 3);

        SectionAdditionResult sectionAdditionResult = sections.add(targetSection);

        Section createdSection = new Section(lineA, stationC, stationD, 2);
        assertThat(sectionAdditionResult.getSectionsToAdd()).isEqualTo(
            List.of(targetSection, createdSection));
        assertThat(sectionAdditionResult.getSectionToRemove()).isEqualTo(Optional.of(secondSection));
        assertThat(sections).isEqualTo(
            new Sections(List.of(firstSection, targetSection, createdSection)));
    }

    @Test
    @DisplayName("노선의 특정 구간과 하행역만 같은 경우 노선에 추가한다.")
    void tes2() {
        Section firstSection = new Section(lineA, stationA, stationB, 5);
        Section secondSection = new Section(lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationC, stationD, 3);

        SectionAdditionResult sectionAdditionResult = sections.add(targetSection);

        Section createdSection = new Section(lineA, stationB, stationC, 2);
        assertThat(sectionAdditionResult.getSectionsToAdd()).isEqualTo(
            List.of(createdSection, targetSection));
        assertThat(sectionAdditionResult.getSectionToRemove()).isEqualTo(Optional.of(secondSection));
        assertThat(sections).isEqualTo(
            new Sections(List.of(firstSection, createdSection, targetSection)));
    }

    @Test
    @DisplayName("추가할 구간의 하행역이 기존 노선의 상행 종점역과 같은 경우 노선의 맨 앞에 추가한다.")
    void tes3() {
        Section firstSection = new Section(lineA, stationB, stationC, 5);
        Section secondSection = new Section(lineA, stationC, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationA, stationB, 3);

        SectionAdditionResult sectionAdditionResult = sections.add(targetSection);

        assertThat(sectionAdditionResult.getSectionsToAdd()).isEqualTo(List.of(targetSection));
        assertThat(sectionAdditionResult.getSectionToRemove()).isEqualTo(Optional.empty());
        assertThat(sections).isEqualTo(
            new Sections(List.of(targetSection, firstSection, secondSection)));
    }

    @Test
    @DisplayName("추가할 구간의 상행역이 기존 노선의 하행 종점역과 같은 경우 노선의 맨 뒤에 추가한다.")
    void tes4() {
        Section firstSection = new Section(lineA, stationA, stationB, 5);
        Section secondSection = new Section(lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));
        Section targetSection = new Section(lineA, stationC, stationD, 3);

        SectionAdditionResult sectionAdditionResult = sections.add(targetSection);

        assertThat(sectionAdditionResult.getSectionsToAdd()).isEqualTo(List.of(targetSection));
        assertThat(sectionAdditionResult.getSectionToRemove()).isEqualTo(Optional.empty());
        assertThat(sections).isEqualTo(
            new Sections(List.of(firstSection, secondSection, targetSection)));
    }

}
