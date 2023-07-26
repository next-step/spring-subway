package subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.application.dto.SectionParam;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSectionsTest() {
        // given
        List<Section> sections = createInitialSectionList();

        // when & then
        assertThatNoException().isThrownBy(() -> new Sections(sections));
    }

    @DisplayName("해당 구간에 연결된 구간이 존재하는지 검증한다.")
    @Test
    void isOverlappedTest() {
        // given
        List<Section> sectionList =  createInitialSectionList();
        Sections sections = new Sections(sectionList);
        Line line = sections.getLine();

        SectionParam overlapped = new SectionParam(line.getId(), 7L, sectionList.get(2).getUpStation().getId(), 2);
        SectionParam notOverlapped = new SectionParam(line.getId(), 8L, sectionList.get(0).getUpStation().getId(), 2);

        // when & then
        assertThat(sections.isOverlapped(overlapped)).isTrue();
        assertThat(sections.isOverlapped(notOverlapped)).isFalse();
    }

    @DisplayName("해당 구간이 추가 가능한 구간인지 검증에 성공한다.")
    @Test
    void validateSectionTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        Line line = sections.getLine();

        SectionParam sectionParam = new SectionParam(line.getId(), 2, 6, 3);

        // when & then
        assertThatNoException()
            .isThrownBy(() -> sections.updateOverlappedSection(sectionParam));
    }

    @DisplayName("두 역이 모두 노선에 존재하는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailBothContainTest() {
        // given
        Sections sections = createInitialSections();
        Line line = sections.getLine();

        long duplicateUpStationId = 1;
        long duplicateDownStationId = 4;
        SectionParam sectionParam = new SectionParam(line.getId(), duplicateUpStationId,
            duplicateDownStationId, 3);

        // when & then
        assertThatThrownBy(() -> sections.updateOverlappedSection(sectionParam))
            .isInstanceOf(IllegalSectionException.class)
            .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("두 역이 모두 노선에 존재하지 않는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailNeitherContainTest() {
        // given
        Sections sections = createInitialSections();
        Line line = sections.getLine();

        long notExistUpStationId = 6;
        long notExistDownStationId = 7;
        SectionParam sectionParam = new SectionParam(line.getId(), notExistUpStationId,
            notExistDownStationId, 3);

        // when & then
        assertThatThrownBy(() -> sections.updateOverlappedSection(sectionParam))
            .isInstanceOf(IllegalSectionException.class)
            .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void validateSectionDistanceFailTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        Line line = sections.getLine();

        int invalidDistance = 10;
        SectionParam sectionParam = new SectionParam(line.getId(), 7L, sectionList.get(2).getUpStation().getId(), invalidDistance);

        // when & then
        assertThatThrownBy(() -> sections.updateOverlappedSection(sectionParam))
            .isInstanceOf(IllegalSectionException.class)
            .hasMessage("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 검증에 성공한다.")
    @Test
    void validateSectionDistanceTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        Line line = sections.getLine();

        Station upStation = sectionList.get(1).getDownStation();

        int validDistance = 3;
        SectionParam sectionParam = new SectionParam(line.getId(), upStation.getId(), 6, validDistance);

        // when & then
        assertThatNoException()
            .isThrownBy(() -> sections.updateOverlappedSection(sectionParam));
    }

    @DisplayName("역 식별자로 해당 역이 종점역인지 반환한다.")
    @Test
    void isLastSectionTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long startStationId = sectionList.get(0).getUpStation().getId();
        long innerStationId = sectionList.get(1).getDownStation().getId();

        // when & then
        assertThat(sections.isLastStation(startStationId)).isTrue();
        assertThat(sections.isLastStation(innerStationId)).isFalse();
    }

    @DisplayName("역 식별자와 일치하는 종점역을 반환한다.")
    @Test
    void getLastSectionTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long startStationId = sectionList.get(0).getUpStation().getId();

        // when
        Section startSection = sections.getLastSection(startStationId);

        // then
        assertThat(startSection.getUpStation().getId()).isEqualTo(startStationId);
    }

    @DisplayName("식별자와 일치하는 종점역이 없으면 예외를 던진다.")
    @Test
    void getLastSectionNotExistExceptionTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);
        long innerStationId = sectionList.get(2).getUpStation().getId();

        // when & then
        assertThatThrownBy(() -> sections.getLastSection(innerStationId))
            .hasMessage("종점 구간이 포함된 역이 아닙니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @DisplayName("역과 상행 방향으로 연결된 구간을 반환한다.")
    @Test
    void findUpDirectionSectionTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);

        Section upDirection = sectionList.get(2);
        long stationId = upDirection.getDownStation().getId();

        // when
        Section result = sections.findUpDirectionSection(stationId);

        // then
        assertThat(result).isEqualTo(upDirection);
    }

    @DisplayName("역과 하행 방향으로 연결된 구간을 반환한다.")
    @Test
    void findDownDirectionSectionTest() {
        // given
        List<Section> sectionList = createInitialSectionList();
        Sections sections = new Sections(sectionList);

        Section downDirection = sectionList.get(2);
        long stationId = downDirection.getUpStation().getId();

        // when
        Section result = sections.findDownDirectionSection(stationId);

        // then
        assertThat(result).isEqualTo(downDirection);
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
        return IntStream.range(0, stations.size() - 1)
            .mapToObj(
                id -> new Section((long) id, line, stations.get(id), stations.get(id + 1), 10))
            .collect(Collectors.toList());
    }

    private Sections createInitialSections() {
        List<Section> sectionList = createInitialSectionList();
        return new Sections(sectionList);
    }

    private Line createInitialLine() {
        return new Line(1, "1호선", "blue");
    }

}
