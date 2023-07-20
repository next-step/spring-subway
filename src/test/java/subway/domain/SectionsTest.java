package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionsTest {
    @Test
    @DisplayName("해당 역이 노선의 하행 종점역인 경우 true")
    void downStationTerminalTest() {
        // given
        Station station = new Station("신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        1L,
                        new Station("서울대입구역"),
                        new Station("신대방역"),
                        10
                )
        ));

        // when & then
        assertThat(sections.isTerminalDownStation(station)).isTrue();
    }

//    @Test
//    @DisplayName("해당 역이 노선의 상행 종점역인 경우 true")
//    void upStationTerminalTest() {
//        // given
//        Station station = new Station(1L, "서울대입구역");
//        Sections sections = new Sections(List.of(
//                new Section(
//                        1L,
//                        new Station(1L, "서울대입구역"),
//                        new Station(2L, "신대방역"),
//                        10
//                )
//        ));
//
//        // when & then
//        assertThat(sections.isTerminal(station)).isTrue();
//    }

    @Test
    @DisplayName("해당 역이 노선의 상행 종점역인 경우 true")
    void notTerminalTest() {
        // given
        Station station = new Station("신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        new Station("서울대입구역"),
                        station,
                        10
                ),
                new Section(
                        station,
                        new Station("잠실역"),
                        10
                )
        ));

        // when & then
        assertThat(sections.isTerminalDownStation(station)).isFalse();
    }

    @Test
    @DisplayName("입력 구간의 하행역이 노선에 포함되어 있는 경우")
    void containStationTest() {
        //given
        Station station = new Station("신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        new Station("서울대입구역"),
                        new Station("신대방역"),
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
        Station station = new Station("상도역");
        Sections sections = new Sections(List.of(
                new Section(
                        new Station("서울대입구역"),
                        new Station("신대방역"),
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
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Sections sections1 = new Sections(List.of(section1));

        Section section2 = new Section(
                new Station("잠실역"),
                new Station("상도역"),
                5
        );
        Sections sections2 = new Sections(List.of(section2));

        // when
        Sections unionSections = sections1.union(sections2);

        // then
        assertThat(unionSections.getSections()).containsAll(List.of(section1, section2));
    }

    @Test
    @DisplayName("한개 구간 삭제 테스트")
    void removeSectionTest() {
        // given
        Station deleteStation = new Station("상도역");
        Section deleteSection = new Section(
                new Station("신대방역"),
                deleteStation,
                4
        );
        Sections sections = new Sections(List.of(
                new Section(
                        new Station("서울대입구역"),
                        new Station("신대방역"),
                        10
                ),
                deleteSection
        ));

        // when
        Sections newSections = sections.remove(deleteStation);

        // then
        assertThat(newSections.getSections()).doesNotContain(deleteSection);
    }

    @Test
    @DisplayName("하행 종점역이 아닌 역을 삭제 할 수 없다.")
    void validateDownStationTerminalRemoveSectionTest() {
        // given
        Station deleteStation = new Station("신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        new Station("서울대입구역"),
                        deleteStation,
                        10
                ),
                new Section(
                        deleteStation,
                        new Station("상도역"),
                        4
                )
        ));

        // when, then
        assertThatCode(() -> sections.remove(deleteStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행 종점역이 아니면 지울 수 없습니다.");
    }

    @Test
    @DisplayName("노선에 구간이 하나일 때는 삭제할 수 없다.")
    void validateOnlyOneSectionRemoveSectionTest() {
        // given
        Station deleteStation = new Station("신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        new Station("서울대입구역"),
                        deleteStation,
                        10
                )
        ));

        // when, then
        assertThatCode(() -> sections.remove(deleteStation))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 구간이 하나일 때는 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 두 역 모두 기존 노선에 포함되어 있으면 오류.")
    void allStationsContainThrowError() {
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Sections sections = new Sections(List.of(section));

        assertThatCode(() -> sections.addSection(section))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 역 모두 기존 노선에 포함될 수 없습니다.");
    }

    @Test
    @DisplayName("새로운 구간의 두 역 모두 기존 노선에 포함되지 않으면 오류.")
    void noStationsContainThrowError() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Section section2 = new Section(
                new Station("상도역"),
                new Station("잠실역"),
                10
        );
        Sections sections = new Sections(List.of(section));

        // when, then
        assertThatCode(() -> sections.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("두 역 중 하나는 기존 노선에 포함되어야 합니다");
    }

    @Test
    @DisplayName("새로운 역의 상행역이 기존 노선의 하행종점역인 경우 추가.")
    void newUpStationMatchesDownTerminal() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Sections sections = new Sections(List.of(section));
        Section newSection = new Section
                (
                        new Station("신대방역"),
                        new Station("잠실역"),
                        4
                );

        // when
        Sections newSections = sections.addSection(newSection);

        // then
        assertThat(newSections.getSections()).containsAll(List.of(section, newSection));
    }

    @Test
    @DisplayName("새로운 역의 하행역이 기존 노선의 상행종점역인 경우 추가.")
    void newDownStationMatchesUpTerminal() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Sections sections = new Sections(List.of(section));
        Section newSection = new Section(
                new Station("잠실역"),
                new Station("서울대입구역"),
                4
        );

        // when
        Sections newSections = sections.addSection(newSection);

        // then
        assertThat(newSections.getSections()).containsAll(List.of(section, newSection));
    }

    @Test
    @DisplayName("새로운 역의 하행역이 기존 노선에 포함되고 상행종점이 아닌 경우")
    void newDownStationNotUpTerminal() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Sections sections = new Sections(List.of(section));
        Section newSection = new Section(
                new Station("잠실역"),
                new Station("신대방역"),
                4
        );

        // when
        Sections newSections = sections.addSection(newSection);

        // then
        assertThat(newSections.getSections()).containsAll(List.of(
                new Section(new Station("서울대입구역"), new Station("잠실역"), 6),
                new Section(new Station("잠실역"), new Station("신대방역"), 4)
        ));
    }

    @Test
    @DisplayName("새로운 역의 상행역이 기존 노선에 포함되고 하행종점이 아닌 경우")
    void newUpStationNotDownTerminal() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Sections sections = new Sections(List.of(section));
        Section newSection = new Section(
                new Station("서울대입구역"),
                new Station("잠실역"),
                4
        );

        // when
        Sections newSections = sections.addSection(newSection);

        // then
        assertThat(newSections.getSections()).containsAll(List.of(
                new Section(new Station("서울대입구역"), new Station("잠실역"), 4),
                new Section(new Station("잠실역"), new Station("신대방역"), 6)
        ));
    }

    @Test
    @DisplayName("새로운 구간의 거리가 기존 구간 거리보다 큰 경우 오류")
    void greaterOrEqualNewSectionDistanceThrowsError() {
        // given
        Section section = new Section(
                new Station("서울대입구역"),
                new Station("신대방역"),
                10
        );
        Sections sections = new Sections(List.of(section));
        Section newSection = new Section(
                new Station("잠실역"),
                new Station("신대방역"),
                10
        );

        // when, then
        assertThatCode(() -> sections.addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다.");
    }

    @Test
    @DisplayName("노선 역을 정렬하여 반환하는 기능")
    void getSortedStationsTest() {
        // given
        Section section1 = new Section(new Station("서울역"), new Station("잠실역"), 1);
        Section section2 = new Section(new Station("신대방역"), new Station("상도역"), 1);
        Section section3 = new Section(new Station("상도역"), new Station("서울역"), 1);

        Sections sections = new Sections(List.of(section1, section2, section3));

        // when
        List<Station> sortedStations = sections.getSortedStations();

        // then
        assertThat(sortedStations).containsExactly(
                new Station("신대방역"),
                new Station("상도역"),
                new Station("서울역"),
                new Station("잠실역")
        );
    }
}