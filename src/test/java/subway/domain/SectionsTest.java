package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.vo.SectionDeleteVo;
import subway.exception.SectionDeleteException;

@DisplayName("구간 일급 컬렉션 테스트")
class SectionsTest {

    @DisplayName("빈 리스트로 Sections 생성시 빈 리스트 반환")
    @Test
    void emptySectionsThenEmptyStation() {
        // given
        List<Section> sectionList = List.of();

        // when
        Sections sections = new Sections(sectionList);

        // then
        assertThat(sections.toStations()).isEmpty();
    }

    @DisplayName("여러 개의 Section 정보가 있을 때 정렬된 순서로 역을 반환")
    @Test
    void givenManySectionsWhenToStationsThenOrderedStations() {
        // given

        Line line = new Line(1L, "1호선", "green");
        Station station1 = new Station(1L, "낙성대");
        Station station2 = new Station(2L, "사당");
        Station station3 = new Station(3L, "이수");
        Station station4 = new Station(4L, "잠실");

        List<Section> sectionList = List.of(
                new Section(line, station1, station2, new Distance(10L)),
                new Section(line, station2, station3, new Distance(10L)),
                new Section(line, station4, station1, new Distance(10L))
        );
        Sections sections = new Sections(sectionList);
        // when

        List<Station> stations = sections.toStations();

        // then
        assertThat(stations).containsExactly(station4, station1, station2, station3);
    }

    @DisplayName("구간 삭제 로직 검증 및 삭제 및 추가될 구간들 반환 - 하행종점역 제거시 하행 종점 구간 반환")
    @Test
    void givenLastStationWhenFindDeleteSectionThenReturnLastSection() {
        // given
        Line line = new Line(1L, "1호선", "green");
        Station station1 = new Station(1L, "낙성대");
        Station station2 = new Station(2L, "사당");
        Station station3 = new Station(3L, "이수");
        Station station4 = new Station(4L, "잠실");

        List<Section> sectionList = List.of(
                new Section(line, station1, station2, new Distance(10L)),
                new Section(line, station2, station3, new Distance(10L)),
                new Section(line, station3, station4, new Distance(10L))
        );
        Sections sections = new Sections(sectionList);

        // when
        SectionDeleteVo deleteVo = sections.findDeletedAndCombinedSections(4L);

        // then
        assertThat(deleteVo).extracting(
                SectionDeleteVo::getDeleteSections,
                SectionDeleteVo::getCombinedSection
        ).contains(
                List.of(new Section(line, station3, station4, new Distance(10L))),
                Optional.empty()
        );
    }

    @DisplayName("구간 삭제 로직 검증 및 삭제 및 추가될 구간들 반환 - 상행종점역 제거시 상행 종점 구간 반환")
    @Test
    void givenFirstStationWhenFindDeleteSectionThenReturnFirstSection() {
        // given
        Line line = new Line(1L, "1호선", "green");
        Station station1 = new Station(1L, "낙성대");
        Station station2 = new Station(2L, "사당");
        Station station3 = new Station(3L, "이수");
        Station station4 = new Station(4L, "잠실");

        List<Section> sectionList = List.of(
                new Section(line, station1, station2, new Distance(10L)),
                new Section(line, station2, station3, new Distance(10L)),
                new Section(line, station3, station4, new Distance(10L))
        );
        Sections sections = new Sections(sectionList);

        // when
        SectionDeleteVo deleteVo = sections.findDeletedAndCombinedSections(1L);

        // then
        assertThat(deleteVo).extracting(
                SectionDeleteVo::getDeleteSections,
                SectionDeleteVo::getCombinedSection
        ).contains(
                List.of(new Section(line, station1, station2, new Distance(10L))),
                Optional.empty()
        );
    }

    @DisplayName("구간 삭제 로직 검증 및 삭제 및 추가될 구간들 반환 - 중간역 제거시 앞뒤 구간 리스트 및 합쳐진 구간 반환")
    @Test
    void givenMiddleStationWhenFindDeleteSectionThenReturnMiddleSection() {
        // given
        Line line = new Line(1L, "1호선", "green");
        Station station1 = new Station(1L, "낙성대");
        Station station2 = new Station(2L, "사당");
        Station station3 = new Station(3L, "이수");
        Station station4 = new Station(4L, "잠실");

        List<Section> sectionList = List.of(
                new Section(line, station1, station2, new Distance(10L)),
                new Section(line, station2, station3, new Distance(10L)),
                new Section(line, station3, station4, new Distance(10L))
        );
        Sections sections = new Sections(sectionList);

        // when
        SectionDeleteVo deleteVo = sections.findDeletedAndCombinedSections(2L);

        // then
        assertThat(deleteVo).extracting(
                SectionDeleteVo::getDeleteSections,
                SectionDeleteVo::getCombinedSection
        ).contains(
                List.of(
                        new Section(line, station1, station2, new Distance(10L)),
                        new Section(line, station2, station3, new Distance(10L))
                ),
                Optional.of(new Section(line, station1, station3, new Distance(20L)))
        );
    }


    @DisplayName("구간 삭제 로직 검증 및 삭제 및 추가될 구간들 반환 - 없는역 제거시 예외발생")
    @Test
    void givenStationNotInLineWhenFindDeleteSectionThenThrow() {
        // given
        Line line = new Line(1L, "1호선", "green");
        Station station1 = new Station(1L, "낙성대");
        Station station2 = new Station(2L, "사당");
        Station station3 = new Station(3L, "이수");
        Station station4 = new Station(4L, "잠실");

        List<Section> sectionList = List.of(
                new Section(line, station1, station2, new Distance(10L)),
                new Section(line, station2, station3, new Distance(10L)),
                new Section(line, station3, station4, new Distance(10L))
        );
        Sections sections = new Sections(sectionList);

        // when
        assertThatCode(() -> sections.findDeletedAndCombinedSections(5L))
                .isInstanceOf(SectionDeleteException.class)
                .hasMessage("노선에 해당하는 역을 가진 구간이 없습니다.");
    }
}
