package subway.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static subway.domain.SectionTest.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @Test
    @DisplayName("정렬된 구간들을 생성한다.")
    void createSortedSections() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 1);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 1);
        Section thirdSection = new Section(3L, lineA, stationC, stationD, 1);

        //when
        Sections sections = new Sections(List.of(thirdSection, secondSection, firstSection));

        //then
        List<Section> actual = sections.getSections();
        assertThat(actual).isEqualTo(List.of(firstSection, secondSection, thirdSection))
            .isNotEqualTo(List.of(thirdSection, secondSection, firstSection));
    }

    @Test
    @DisplayName("순환된 구간으로 생성할 수 없다.")
    void cannotCreateWithCircularSection() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

        Section section = new Section(1L, lineA, stationA, stationB, 1);
        Section circularSection = new Section(2L, lineA, stationB, stationA, 1);

        //when & then
        assertThatThrownBy(() -> new Sections(List.of(section, circularSection)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("널이거나 비어 있는 구간 리스트로 생성할 수 없다.")
    void cannotCreateWithNullOrEmptySectionList() {
        //given

        //when & then
        assertThatThrownBy(() -> new Sections(null))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Sections(emptyList()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("끊어진 구간들로 생성할 수 없다.")
    void cannotCreateWithSeperatedSections() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, lineA, stationC, stationD, 3);

        //when & then
        assertThatThrownBy(() -> new Sections(List.of(sectionA, sectionB)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("다른 노선에 속한 구간들로 생성할 수 없다.")
    void cannotCreateWithSectionsOfOtherLines() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Line otherLine = new Line(2L, "lineB", "red");
        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, otherLine, stationB, stationC, 3);

        //when & then
        assertThatThrownBy(() -> new Sections(List.of(sectionA, sectionB)))
            .isInstanceOf(IllegalArgumentException.class);
    }


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


        //when & then
        assertThatThrownBy(() -> sections.validateAddition(stationA, stationC, 3))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> sections.validateAddition(stationD, stationE, 3))
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

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        //when & then
        assertThatThrownBy(() -> sections.validateAddition(stationB, stationC, 5))
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

        //when & then
        assertThatCode(() -> sections.validateAddition(stationB, stationC, 3))
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

        //when & then
        assertThatCode(() -> sections.validateAddition(stationC, stationD, 3))
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

        //when & then
        assertThatCode(() -> sections.validateAddition(stationA, stationB, 7))
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

        //when & then
        assertThatCode(() -> sections.validateAddition(stationC, stationD, 7))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구간이 노선의 중간에 추가된다.")
    void isAddedInMiddle() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section section = new Section(1L, lineA, stationA, stationC, 5);
        Sections sections = new Sections(List.of(section));

        Section sectionInMiddle = new Section(2L, lineA, stationB, stationC, 3);
        Section sectionAtEnd = new Section(2L, lineA, stationC, stationB, 3);

        //when & then
        assertThat(sections.isAddedInMiddle(sectionInMiddle)).isTrue();
        assertThat(sections.isAddedInMiddle(sectionAtEnd)).isFalse();
    }

    @Test
    @DisplayName("노선의 맨 앞이나 뒤에 구간을 추가하는 경우 변경 대상 구간이 존재하지 않는다.")
    void addFirstOrLastThenNoChange() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        Section section = new Section(lineA, stationC, stationD, 7);

        //when & then
        assertThatThrownBy(() -> sections.findSectionToChange(section))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선의 중간에 구간을 추가하는 경우 변경 대상 구간이 존재한다.")
    void addInTheMiddleThenChangeExists() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section firstSection = new Section(1L, lineA, stationA, stationB, 5);
        Section secondSection = new Section(2L, lineA, stationB, stationD, 5);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        Section section = new Section(lineA, stationC, stationD, 3);

        //when & then
        assertThatCode(() -> sections.findSectionToChange(section))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("구간 추가로 인해 변경된 구간을 반환한다.")
    void findChangedSectionDueToAddition() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section section = new Section(1L, lineA, stationA, stationC, 5);
        Sections sections = new Sections(List.of(section));

        Section newSection = new Section(2L, lineA, stationB, stationC, 3);

        //when
        Section actual = sections.findSectionToChange(newSection);

        //then
        Section expected = new Section(1L, lineA, stationA, stationB, 2);
        assertThat(actual).isEqualTo(expected);
        assertThat(doSectionsHaveSameFields(actual, expected)).isTrue();
    }

    @Test
    @DisplayName("구간이 1개인 경우 삭제할 수 없다")
    void cannotRemoveOneSizeSections() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

        Section section = new Section(1L, lineA, stationA, stationB, 1);
        Sections sections = new Sections(List.of(section));

        //when & then
        assertThatThrownBy(() -> sections.validateRemoval(stationB))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("노선에 등록되어 있지 않은 역을 제거할 수 없다.")
    void cannotRemoveUnregisteredStation() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Station stationD = new Station(4L, "D");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 2);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 5);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        //when & then
        assertThatThrownBy(() -> sections.validateRemoval(stationD))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노선에서 중간에 위치한 역을 제거하는 경우 변경 대상 구간이 존재한다.")
    void removeInMiddleThenUpdateExists() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        //when & then
        assertThatCode(() -> sections.findSectionToChange(stationB))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("노선에서 종점역을 제거하는 경우 변경 대상 구간이 존재하지 않는다.")
    void removeEndThenUpdateExists() {
        //given
        Line lineA = new Line(1L, "A", "#ff0000");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 1);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 1);
        Sections sections = new Sections(List.of(sectionA, sectionB));

        //when & then
        assertThatThrownBy(() -> sections.findSectionToChange(stationC))
            .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    @DisplayName("역이 노선의 중간에 위치한다.")
    void isInMiddle() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 3);

        Sections sections = new Sections(List.of(sectionA, sectionB));

        //when & then
        assertThat(sections.isInMiddle(stationB)).isTrue();
        assertThat(sections.isInMiddle(stationA)).isFalse();
        assertThat(sections.isInMiddle(stationC)).isFalse();
    }

    @Test
    @DisplayName("구간 삭제로 인해 변경된 구간을 반환한다.")
    void findChangedSectionDueToRemoval() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Section sectionA = new Section(1L, lineA, stationA, stationB, 3);
        Section sectionB = new Section(2L, lineA, stationB, stationC, 2);

        Sections sections = new Sections(List.of(sectionA, sectionB));

        //when
        Section actual = sections.findSectionToChange(stationB);

        //then
        Section expected = new Section(1L, lineA, stationA, stationC, 5);
        assertThat(expected).isEqualTo(actual);
        assertThat(doSectionsHaveSameFields(expected, actual)).isTrue();
    }
}
