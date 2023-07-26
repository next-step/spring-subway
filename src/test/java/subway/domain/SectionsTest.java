package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SectionsTest {

    private Line line;
    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "1호선", "green");
        stationA = new Station(1L, "낙성대");
        stationB = new Station(2L, "사당");
        stationC = new Station(3L, "이수");
        stationD = new Station(4L, "잠실");
        stationE = new Station(5L, "신촌");
    }


    @Test
    @DisplayName("Sections 가 적어도 하나의 Section 을 가지지 않으면 예외를 던진다.")
    void SectionsSizeValidation() {
        assertThatCode(() -> new Sections(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 구간은 반드시 한 개 이상이어야합니다.");
    }

    @DisplayName("여러개의 구간 정보를 포함한 Sections 에서 toStations 를 호출하면 포함된 역들을 반환 한다.")
    @Test
    void givenManySectionsWhenToStationsThenStations() {
        // given
        final List<Section> sectionList = List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L)),
                new Section(101L, line, stationD, stationA, new Distance(10L))
        );
        final Sections sections = new Sections(sectionList);
        // when

        final List<Station> stations = sections.toStations();

        // then
        assertThat(stations).containsAnyOf(stationD, stationA, stationB, stationC);
    }

    @DisplayName("노선에 A -> B -> C 구간이 있을 때 A -> D 구간을 새로 생성하면 A -> D -> B -> C 가 된다.")
    @Test
    void givenA_B_C_when_addSection_thenA_D_B_C() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationA, stationD, new Distance(5L)));

        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationD, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationD, stationB, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("노선에 A -> B -> C 구간이 있을 때 C -> D 구간을 새로 생성하면 A -> B -> C -> D 가 된다.")
    @Test
    void givenA_B_C_when_addSection_thenA_B_C_D() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationC, stationD, new Distance(5L)));


        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationB, stationC);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationC, stationD);
        assertThat(result).hasSize(3);
    }

    @DisplayName("노선에 A -> B -> C 구간이 있을 때 D -> C 구간을 새로 생성하면 A -> B -> D -> C 가 된다.")
    @Test
    void givenA_B_C_when_addSection_thenA_B_D_C() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationD, stationC, new Distance(5L)));


        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationB, stationD);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationD, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("노선에 A -> B -> C 구간이 있을 때 D -> A 구간을 새로 생성하면 D -> A -> B -> C 가 된다.")
    @Test
    void givenA_B_C_when_addSection_thenD_A_B_C() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when
        sections.addSection(new Section(line, stationD, stationA, new Distance(5L)));


        // then
        final List<Section> result = sections.getSections();

        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationD, stationA, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationA, stationB, stationC);
        assertThat(result).hasSize(3);
    }

    @DisplayName("A -> B -> C 구간에서 A -> B 사이에 길이보다 새로운 A -> D 구간 길이가 크거나 같으면 예외를 던진다.")
    @Test
    void addSectionTooMuchDistanceThenThrow() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when , then
        assertThatCode(() -> sections.addSection(new Section(line, stationA, stationD, new Distance(100L))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 구간 길이보다 새로운 구간 길이가 같거나 더 클수는 없습니다.");

    }

    @DisplayName("A -> B -> C 구간에서 새롭게 삽입하고자하는 A , C 역이 모두 있다면 예외를 던집니다.")
    @Test
    void addSectionStationsAlreadyExistInLineThenThrow() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when , then
        assertThatCode(() -> sections.addSection(new Section(line, stationA, stationC, new Distance(5L))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("A -> B -> C 구간에서 새롭게 삽입하고자하는 E , D 역이 모두 없다면 예외를 던집니다.")
    @Test
    void addSectionStationsNotExistInLineThenThrow() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L))
        ));

        // when , then
        assertThatCode(() -> sections.addSection(new Section(line, stationE, stationD, new Distance(5L))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @Test
    @DisplayName("D -> A -> B -> C 구간에서 첫 번째 역인 D 역을 삭제하면 A -> B -> C 가 되어야한다.")
    void deleteFirstSection() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L)),
                new Section(101L, line, stationD, stationA, new Distance(10L))
        ));
        Station station = stationD;

        // when
        sections.deleteSection(station);

        // then
        final List<Section> result = sections.getSections();
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationA, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationC);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("D -> A -> B -> C 구간에서 마지막역인 C 역을 삭제하면 D -> A -> B 가 되어야한다.")
    void deleteLastSection() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L)),
                new Section(101L, line, stationD, stationA, new Distance(10L))
        ));
        Station station = stationC;

        // when
        sections.deleteSection(station);

        // then
        final List<Section> result = sections.getSections();
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationD, stationA);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationA, stationB);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("D -> A -> B -> C 구간에서 중간역인 A 역을 삭제하면 D -> B -> C 가 되어야한다.")
    void deleteMiddleSection() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L)),
                new Section(101L, line, stationD, stationA, new Distance(10L))
        ));
        Station station = stationA;

        // when
        sections.deleteSection(station);

        // then
        final List<Section> result = sections.getSections();
        assertThat(result.stream()
                .map(Section::getUpStation)
                .collect(toList())).contains(stationD, stationB);
        assertThat(result.stream()
                .map(Section::getDownStation)
                .collect(toList())).contains(stationB, stationC);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("D -> A -> B -> C 구간에서 D -> A ,A -> B의 구간 길이가 각각 10 , 15일 때 A 역을 삭제하면 D -> B 구간 길이가 25가 된다.")
    void deleteSectionThenAddDistance() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(15L)),
                new Section(100L, line, stationB, stationC, new Distance(10L)),
                new Section(101L, line, stationD, stationA, new Distance(10L))
        ));
        Station station = stationA;

        // when
        sections.deleteSection(station);

        // then
        final Optional<Section> result = sections.getSections().stream()
                .filter(section -> section.getUpStation().equals(stationD))
                .findAny();

        assertThat(result).isPresent();
        assertThat(result.get())
                .extracting(Section::getUpStation, Section::getDownStation, Section::getDistance)
                .contains(stationD, stationB, 25L);
    }

    @Test
    @DisplayName("D -> A -> B -> C 구간에서 구간에 없는 역인 E를 삭제하려고하면 예외를 던진다.")
    void deleteSectionFailBecauseOfNotContainStation() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L)),
                new Section(100L, line, stationB, stationC, new Distance(10L)),
                new Section(101L, line, stationD, stationA, new Distance(10L))
        ));
        Station station = stationE;

        // when , then
        assertThatCode(() -> sections.deleteSection(station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간에서 역을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("A -> B 구간과 같이 노선에 구간이 한 개 인경우 삭제하려고하면 예외를 던진다.")
    void deleteSectionFailBecauseOfLength() {
        // given
        final Sections sections = new Sections(List.of(
                new Section(99L, line, stationA, stationB, new Distance(10L))
        ));
        Station station = stationB;

        // when , then
        assertThatCode(() -> sections.deleteSection(station))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
    }


}
