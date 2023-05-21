package subway.domain.application;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.SubwayGraph;
import subway.domain.dto.AddSectionDto;
import subway.domain.dto.LineDto;
import subway.domain.dto.StationDto;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static subway.domain.EntityFactoryForTest.*;

@DisplayName("지하철 노선 그래프 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SubwayGraphServiceTest {

    SubwayGraph subwayGraph = new SubwayGraph();
    @Mock
    SectionRepository sectionRepository;

    @Mock
    LineRepository lineRepository;

    @Mock
    StationRepository stationRepository;

    private SubwayGraphService subwayGraphService;

    @BeforeEach
    void initGraph() {
        // given
        List<Section> sections = new ArrayList<>();
        sections.add(makeSection(1L, 1L, 2L, 1));
        sections.add(makeSection(1L, 2L, 3L, 1));
        sections.add(makeSection(1L, 3L, 4L, 1));
        sections.add(makeSection(1L, 4L, 5L, 1));
        sections.add(makeSection(2L, 1L, 6L, 1));

        given(sectionRepository.findAll())
                .willReturn(sections);

        subwayGraphService = new SubwayGraphService(subwayGraph, sectionRepository, lineRepository, stationRepository);
        subwayGraphService.initGraph();
    }

    @Test
    @DisplayName("구간 추가에 성공합니다.")
    void addSectionSuccessfully() {
        // given
        Line line = new Line(2L, "2호선", "color");
        Station upStation = new Station(6L, "6번역");
        Station downStation = new Station(7L, "7번역");
        Integer distance = 10;
        when(lineRepository.findById(2L)).thenReturn(line);
        when(stationRepository.findById(6L)).thenReturn(upStation);
        when(stationRepository.findById(7L)).thenReturn(downStation);

        AddSectionDto addSectionDto = AddSectionDto.builder()
                .lineId(line.getId())
                .upStationId(upStation.getId())
                .downStationId(downStation.getId())
                .distance(distance)
                .build();

        // when
        subwayGraphService.addSection(addSectionDto);

        // then
        Section section = Section.of(line, upStation, downStation, distance);
        then(sectionRepository).should(times(1)).insert(section);
    }

    @Test
    @DisplayName("구간 삭제에 성공합니다.")
    void removeSectionSuccessfully() {
        // given
        Long lineId = 1L;
        Long stationId = 5L;
        when(lineRepository.findById(lineId)).thenReturn(new Line(lineId, "name", "color"));
        when(stationRepository.findById(stationId)).thenReturn(new Station(stationId, "name"));

        // when
        subwayGraphService.removeStation(lineId, stationId);

        // then
        then(sectionRepository).should(times(1)).deleteById(any());
    }

    @Test
    @DisplayName("호선에 해당하는 역을 모두 조회합니다.")
    void getLineWithStationsSuccessfully() {
        // given
        Long lineId = 1L;

        Line line = new Line(1L, "1호선", "white");
        when(lineRepository.findById(lineId)).thenReturn(line);

        // when
        LineDto lineDto = subwayGraphService.getLineWithStations(lineId);

        // then
        assertThat(lineDto.getId()).isEqualTo(line.getId());
        assertThat(lineDto.getName()).isEqualTo(line.getName());
        assertThat(lineDto.getColor()).isEqualTo(line.getColor());

        List<Long> stationIds = List.of(1L, 2L, 3L, 4L, 5L);
        assertThat(lineDto.getStations())
                .extracting(StationDto::getId)
                .usingRecursiveComparison()
                .isEqualTo(stationIds);
    }
}
