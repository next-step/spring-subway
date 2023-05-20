package subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.entity.Line;
import subway.domain.entity.Section;
import subway.domain.entity.Station;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.testdouble.InMemoryLineRepository;
import subway.testdouble.InMemorySectionRepository;
import subway.testdouble.InMemoryStationRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionServiceTest {
    private final LineRepository lineRepository = new InMemoryLineRepository();
    private final StationRepository stationRepository = new InMemoryStationRepository();
    private final SectionRepository sectionRepository = new InMemorySectionRepository();
    private final LineService lineService = new LineService(lineRepository);
    private final StationService stationService = new StationService(stationRepository);
    private final SectionService sectionService = new SectionService(lineService, stationService, sectionRepository);

    @Test
    void saveSection() {
        // given
        Line line = new Line(1L, "2호선", "green");
        lineRepository.insert(line);
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);

        // when
        Section section = new Section(1L, station1.getId(), station2.getId(), 1L, 10);
        sectionService.saveSection(1L, section);

        // then
        List<Section> lines = sectionRepository.findAllByLineId(1L);
        assertThat(lines.size()).isEqualTo(1);
    }

    @DisplayName("존재하지 않는 노선에 대해 예외를 던진다.")
    @Test
    void saveSectionWithInvalidLine() {
        // given
        Line line = new Line(1L, "2호선", "green");
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);

        // when, then
        Section section = new Section(1L, station1.getId(), station2.getId(), 1L, 10);
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            sectionService.saveSection(1L, section);
        });
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 노선입니다.");
    }

    @DisplayName("존재하지 않는 역에 대해 예외를 던진다.")
    @Test
    void saveSectionWithInvalidStation() {
        // given
        Line line = new Line(1L, "2호선", "green");
        lineRepository.insert(line);
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        stationRepository.insert(station1);

        // when, then
        Section section = new Section(1L, station1.getId(), station2.getId(), 1L, 10);
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            sectionService.saveSection(1L, section);
        });
        assertThat(e.getMessage()).isEqualTo("존재하지 않는 역입니다.");
    }

    @DisplayName("노선의 하행 종점역이 새 구간의 상행역과 같지 않으면 예외를 던진다.")
    @Test
    void saveSectionWithInvalidUpStation() {
        // given
        Line line = new Line(1L, "2호선", "green");
        lineRepository.insert(line);
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Station station3 = new Station(3L, "선릉역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);
        stationRepository.insert(station3);
        Section section1 = new Section(1L, station2.getId(), station1.getId(), 1L, 10);
        sectionRepository.insert(section1);

        // when, then
        Section section2 = new Section(2L, station3.getId(), station1.getId(), 1L, 2);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            sectionService.saveSection(1L, section2);
        });
        assertThat(e.getMessage()).isEqualTo("노선의 하행 종점역이 새 구간의 상행역과 같지 않습니다.");
    }

    @DisplayName("이미 등록된 역이면 예외를 던진다.")
    @Test
    void saveSectionWithAlreadyRegistered() {
        // given
        Line line = new Line(1L, "2호선", "green");
        lineRepository.insert(line);
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Station station3 = new Station(3L, "선릉역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);
        stationRepository.insert(station3);
        Section section1 = new Section(1L, station2.getId(), station1.getId(), 1L, 10);
        sectionRepository.insert(section1);

        // when, then
        Section section2 = new Section(2L, station1.getId(), station2.getId(), 1L, 2);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            sectionService.saveSection(1L, section2);
        });
        assertThat(e.getMessage()).isEqualTo("이미 등록된 역입니다.");
    }

    @Test
    void deleteSectionByStationId() {
        // given
        Line line = new Line(1L, "2호선", "green");
        lineRepository.insert(line);
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Station station3 = new Station(3L, "선릉역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);
        stationRepository.insert(station3);
        Section section1 = new Section(1L, station2.getId(), station1.getId(), 1L, 10);
        sectionRepository.insert(section1);

        // when
        sectionService.deleteSectionByStationId(1L, station2.getId());

        // then
        List<Section> sections = sectionRepository.findAllByLineId(1L);
        assertThat(sections.size()).isEqualTo(0);
    }

    @DisplayName("노선에 등록된 구간이 없으면 예외를 던진다.")
    @Test
    void deleteWithoutSection() {
        // given
        Line line = new Line(1L, "2호선", "green");
        lineRepository.insert(line);
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Station station3 = new Station(3L, "선릉역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);
        stationRepository.insert(station3);

        // when, then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            sectionService.deleteSectionByStationId(1L, station2.getId());
        });
        assertThat(e.getMessage()).isEqualTo("등록된 구간이 없습니다.");
    }

    @DisplayName("노선의 하행 종점역이 아닌 구간을 제거하려하면 예외를 던진다.")
    @Test
    void deleteWithInvalidDownStationId() {
        // given
        Line line = new Line(1L, "2호선", "green");
        lineRepository.insert(line);
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "역삼역");
        Station station3 = new Station(3L, "선릉역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);
        stationRepository.insert(station3);
        Section section1 = new Section(1L, station2.getId(), station1.getId(), 1L, 10);
        sectionRepository.insert(section1);

        // when, then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            sectionService.deleteSectionByStationId(1L, station1.getId());
        });
        assertThat(e.getMessage()).isEqualTo("노선의 하행 종점역만 제거할 수 있습니다.");
    }
}
