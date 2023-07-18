package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SectionsTest {
    @Test
    @DisplayName("해당 역이 노선의 하행 종점역인지 검증하는 기능")
    void terminalTest() {
        // given
        Station station = new Station(2L, "신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        1L,
                        new Station(1L, "서울대입구역"),
                        new Station(2L, "신대방역"),
                        new Line(1L, "2호선", "green"),
                        10
                )
        ));

        // when & then
        assertThat(sections.isTerminal(station)).isTrue();
    }

    @Test
    @DisplayName("해당 역이 노선의 하행역이 아닌 경우")
    void notTerminalTest() {
        // given
        Station station = new Station(1L, "서울대입구역");
        Sections sections = new Sections(List.of(
                new Section(
                        1L,
                        new Station(1L, "서울대입구역"),
                        new Station(2L, "신대방역"),
                        new Line(1L, "2호선", "green"),
                        10
                )
        ));

        // when & then
        assertThat(sections.isTerminal(station)).isFalse();
    }

    @Test
    @DisplayName("입력 구간의 하행역이 노선에 포함되어 있는 경우")
    void containStationTest() {
        //given
        Station station = new Station(2L, "신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        1L,
                        new Station(1L, "서울대입구역"),
                        new Station(2L, "신대방역"),
                        new Line(1L, "2호선", "green"),
                        10
                )
        ));

        //when
        assertThat(sections.contains(station)).isTrue();
    }

    @Test
    @DisplayName("입력 구간의 하행역이 노선에 포함되어 있지 않은 경우")
    void notContainStationTest() {
        //given
        Station station = new Station(3L, "상도역");
        Sections sections = new Sections(List.of(
                new Section(
                        1L,
                        new Station(1L, "서울대입구역"),
                        new Station(2L, "신대방역"),
                        new Line(1L, "2호선", "green"),
                        10
                )
        ));

        //when
        assertThat(sections.contains(station)).isFalse();
    }

    @Test
    @DisplayName("두 Sections를 합치는 테스트")
    void unionSectionsTest() {
        // given
        Section section1 = new Section(
                1L,
                new Station(1L, "서울대입구역"),
                new Station(2L, "신대방역"),
                new Line(1L, "2호선", "green"),
                10
        );
        Sections sections1 = new Sections(List.of(section1));

        Section section2 = new Section(
                2L,
                new Station(3L, "잠실역"),
                new Station(4L, "상도역"),
                new Line(1L, "2호선", "green"),
                5
        );
        Sections sections2 = new Sections(List.of(section2));

        // when
        Sections unionSections = sections1.union(sections2);

        // then
        assertThat(unionSections.getSections()).containsAll(List.of(section1, section2));
    }
}