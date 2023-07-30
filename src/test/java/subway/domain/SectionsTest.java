package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalStationsException;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSections_notThrowException() {
        // given
        List<Section> sections = createInitialSectionList();

        // when & then
        assertThatNoException().isThrownBy(() -> new Sections(sections));
    }

    @DisplayName("역과 연결된 구간들을 반환한다.")
    @Test
    void getConnectedSection_returnSections() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long stationId = sectionList.get(0)
            .getUpStation()
            .getId();

        // when
        List<Section> result = sections.getConnectedSection(stationId);

        // then
        List<Section> expected = sectionList.stream()
            .filter(section -> section.hasStation(stationId))
            .collect(Collectors.toList());
        assertThat(result).hasSameElementsAs(expected);
    }

    @DisplayName("역과 상행 방향으로 연결된 구간을 반환한다.")
    @Test
    void getUpDirectionSection_returnSection() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);

        Section upDirection = sectionList.get(2);
        long stationId = upDirection.getDownStationId();

        // when
        Section result = sections.getUpDirectionSection(stationId);

        // then
        assertThat(result).isEqualTo(upDirection);
    }

    @DisplayName("역과 하행 방향으로 연결된 구간을 반환한다.")
    @Test
    void getDownDirectionSection_returnSection() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);

        Section downDirection = sectionList.get(2);
        long stationId = downDirection.getUpStationId();

        // when
        Section result = sections.getDownDirectionSection(stationId);

        // then
        assertThat(result).isEqualTo(downDirection);
    }

    @DisplayName("모든 구간을 반환한다.")
    @Test
    void getAll_returnAllSections() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);

        // when
        List<Section> allSections = sections.getAll();

        // then
        assertThat(sectionList).containsExactlyInAnyOrderElementsOf(allSections);
    }

    @DisplayName("식별자로 역 정보를 조회한다.")
    @Test
    void getStationById_returnSections() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        Station station = sectionList.get(2).getDownStation();

        // when
        Station result = sections.getStationById(station.getId());

        // then
        assertThat(result).isEqualTo(station);
    }

    @DisplayName("식별자로 역 정보를 조회시, 역이 존재하지 않으면 예외를 던진다.")
    @Test
    void getStationById_notExist_throwException() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long notExistId = 99;

        // when & then
        assertThatThrownBy(() -> sections.getStationById(notExistId))
            .hasMessage("존재하지 않는 역 정보입니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @DisplayName("모든 역 정보를 반환한다.")
    @Test
    void getAllStations_returnStations() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);

        // when
        List<Station> result = sections.getAllStations();

        // then
        List<Station> expected = sectionList.stream()
            .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
        assertThat(result).hasSameElementsAs(expected);
    }

    @DisplayName("하행 방향으로 연결된 구간이 존재하는지 여부를 반환한다.")
    @Test
    void isDownDirectionSectionExist_returnBoolean() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long stationId = sectionList.get(1).getUpStationId();

        // when & then
        assertThat(sections.isDownDirectionSectionExist(stationId)).isTrue();
    }

    @DisplayName("상행 방향으로 연결된 구간이 존재하는지 여부를 반환한다.")
    @Test
    void isUpDirectionSectionExist_returnBoolean() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long stationId = sectionList.get(1).getUpStationId();

        // when & then
        assertThat(sections.isUpDirectionSectionExist(stationId)).isTrue();
    }

    @DisplayName("종점인지 여부를 반환한다.")
    @Test
    void isLastStation_returnBoolean() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long stationId = sectionList.get(0).getUpStationId();

        // when & then
        assertThat(sections.isLastStation(stationId)).isTrue();
    }

    @DisplayName("상행 종점 여부를 반환한다.")
    @Test
    void isStartStation_returnBoolean() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long stationId = sectionList.get(0).getUpStationId();

        // when & then
        assertThat(sections.isLastStation(stationId)).isTrue();
    }

    @DisplayName("하행 종점인지 여부를 반환한다.")
    @Test
    void isEndStation_returnBoolean() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long stationId = sectionList.get(sectionList.size()-1)
            .getDownStationId();

        // when & then
        assertThat(sections.isEndStation(stationId)).isTrue();
    }

    @DisplayName("역이 존재하는지 여부를 반환한다.")
    @Test
    void isStationExist_returnBoolean() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long existStationId = sectionList.get(sectionList.size()-1)
            .getDownStationId();
        long notExistStationId = 99;

        // when & then
        assertThat(sections.isStationExist(existStationId)).isTrue();
        assertThat(sections.isStationExist(notExistStationId)).isFalse();
    }

    private List<Station> createInitialStationList() {
        List<Station> station = new ArrayList<>();
        station.add(new Station(1L, "교대역"));
        station.add(new Station(2L, "강남역"));
        station.add(new Station(3L, "역삼역"));
        station.add(new Station(4L, "선릉역"));
        station.add(new Station(5L, "삼성역"));
        return station;
    }

    private List<Section> createInitialSectionList() {
        Line line = createInitialLine();
        List<Station> stations = createInitialStationList();

        List<Section> sections = new ArrayList<>();
        sections.add(new Section(2L, line, stations.get(0), stations.get(4), 10));
        sections.add(new Section(4L, line, stations.get(4), stations.get(2), 10));
        sections.add(new Section(3L, line, stations.get(2), stations.get(1), 10));
        sections.add(new Section(1L, line, stations.get(1), stations.get(3), 10));
        return sections;
    }

    private Line createInitialLine() {
        return new Line(1, "1호선", "blue");
    }

}