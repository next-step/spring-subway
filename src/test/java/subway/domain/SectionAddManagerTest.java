package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionAddManagerTest {

    @Test
    @DisplayName("추가할 구간의 한 역만 기존 노선에 포함되어야 한다.")
    void canAddSectionToLineContainingOnlyOneStation() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");
        Station stationE = new Station(5L, "E");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);

        //when & then
        assertThatThrownBy(() -> sectionAddManager.validate(stationA, stationC, 3))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sectionAddManager.validate(stationD, stationE, 3))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추가할 구간의 길이가 대상 구간보다 같거나 길면 추가할 수 없다.")
    void cannotAddSectionWithTooLongDistance() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationC, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);

        //when & then
        assertThatThrownBy(() -> sectionAddManager.validate(stationB, stationC, 5))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선의 특정 구간과 상행역만 같은 경우 노선에 추가한다.")
    void addSectionOfSameUpStation() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);

        //when & then
        assertThatCode(() -> sectionAddManager.validate(stationB, stationC, 3))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("노선의 특정 구간과 하행역만 같은 경우 노선에 추가한다.")
    void addSectionOfSameDownStation() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);

        //when & then
        assertThatCode(() -> sectionAddManager.validate(stationC, stationD, 3))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("추가할 구간의 하행역이 기존 노선의 상행 종점역과 같은 경우 노선의 맨 앞에 추가한다.")
    void addFirst() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationB, stationC, 5);
        Section secondSection = new Section(2L, lineA, stationC, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);

        //when & then
        assertThatCode(() -> sectionAddManager.validate(stationA, stationB, 7))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("추가할 구간의 상행역이 기존 노선의 하행 종점역과 같은 경우 노선의 맨 뒤에 추가한다.")
    void addLast() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);

        //when & then
        assertThatCode(() -> sectionAddManager.validate(stationC, stationD, 7))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("노선의 맨 앞이나 뒤에 구간을 추가하는 경우 변경 대상 구간이 존재하지 않는다.")
    void addFirstOrLastThenNoUpdate() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);
        Section section = new Section(lineA, stationC, stationD, 7);

        //when & then
        assertThat(sectionAddManager.lookForChange(section)).isEmpty();
    }

    @Test
    @DisplayName("노선의 중간에 구간을 추가하는 경우 변경 대상 구간이 존재한다.")
    void addInTheMiddleThenUpdateExists() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        SectionAddManager sectionAddManager = new SectionAddManager(sections);
        Section section = new Section(lineA, stationC, stationD, 3);

        //when & then
        assertThat(sectionAddManager.lookForChange(section)).isNotEmpty();
    }
}
