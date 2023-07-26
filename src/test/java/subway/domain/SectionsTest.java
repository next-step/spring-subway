package subway.domain;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        List<Section> actual = sections.getValues();
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
    @DisplayName("역 포함 여부를 반환한다.")
    void hasStation() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");

        Sections sections = new Sections(List.of(new Section(1L, lineA, stationA, stationB, 3)));

        //when & then
        assertThat(sections.hasStation(stationA)).isTrue();
        assertThat(sections.hasStation(stationC)).isFalse();
    }

    @Test
    @DisplayName("노선의 첫 역인지 여부를 반환한다.")
    void isFirst() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

        Sections sections = new Sections(List.of(new Section(1L, lineA, stationA, stationB, 3)));

        //when & then
        assertThat(sections.isFirst(stationA)).isTrue();
        assertThat(sections.isFirst(stationB)).isFalse();
    }

    @Test
    @DisplayName("노선의 마지막 역인지 여부를 반환한다.")
    void isLast() {
        //given
        Line lineA = new Line(1L, "A", "red");
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");

        Sections sections = new Sections(List.of(new Section(1L, lineA, stationA, stationB, 3)));

        //when & then
        assertThat(sections.isLast(stationB)).isTrue();
        assertThat(sections.isLast(stationA)).isFalse();
    }
}
