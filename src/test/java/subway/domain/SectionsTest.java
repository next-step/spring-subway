package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionsTest {
    @Test
    @DisplayName("하행 종점역을 삭제할 수 있다.")
    void removeDownTerminalStationTest() {
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
        Sections newSections = sections.removeStation(deleteStation);

        // then
        assertThat(newSections.getSections()).doesNotContain(deleteSection);
    }

    @Test
    @DisplayName("상행 종점역을 삭제할 수 있다.")
    void removeUpTerminalStationTest() {
        // given
        Station deleteStation = new Station("서울대입구역");
        Section deleteSection = new Section(
                deleteStation,
                new Station("신대방역"),
                4
        );
        Sections sections = new Sections(List.of(
                deleteSection,
                new Section(
                        new Station("신대방역"),
                        new Station("잠실역"),
                        10
                )
        ));

        // when
        Sections newSections = sections.removeStation(deleteStation);

        // then
        assertThat(newSections.getSections()).doesNotContain(deleteSection);
    }

    @Test
    @DisplayName("중간 역을 삭제하면 앞뒤 역을 이어준다.")
    void removeMiddleStationTest() {
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

        // when
        Sections newSections = sections.removeStation(deleteStation);

        // then
        assertThat(newSections.getSections()).hasSize(1);
        assertThat(newSections.getSections()).contains(
                new Section(new Station("서울대입구역"), new Station("상도역"), 14)
        );
    }

    @Test
    @DisplayName("노선에 역이 포함되지 않을 때는 삭제할 수 없다.")
    void stationNotInSectionsTest() {
        // given
        Station deleteStation = new Station("신대방역");
        Sections sections = new Sections(List.of(
                new Section(
                        new Station("서울대입구역"),
                        new Station("잠실역"),
                        10
                ),
                new Section(
                        new Station("잠실역"),
                        new Station("상도역"),
                        10
                )
        ));

        // when, then
        assertThatCode(() -> sections.removeStation(deleteStation))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.REMOVE_SECTION_NOT_CONTAIN.getMessage());
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
        assertThatCode(() -> sections.removeStation(deleteStation))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.SECTION_VALIDATE_SIZE.getMessage());
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
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.NEW_SECTION_BOTH_MATCH.getMessage());
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
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.NEW_SECTION_NO_MATCH.getMessage());
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
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.DISTANCE_VALIDATE_SUBTRACT.getMessage() + "10");
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