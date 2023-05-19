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
import subway.domain.dto.LineDto;
import subway.domain.dto.StationDto;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("지하철 노선 그래프 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SubwayGraphServiceTest {
    @Mock
    SectionRepository sectionRepository;

    @Mock
    LineRepository lineRepository;

    @BeforeEach
    void mockingInitData() {
        // given
        List<Section> sections = new ArrayList<>();
        sections.add(makeSection(1L, 1L, 2L, 1));
        sections.add(makeSection(1L, 2L, 3L, 1));
        sections.add(makeSection(1L, 3L, 4L, 1));
        sections.add(makeSection(1L, 4L, 5L, 1));
        sections.add(makeSection(2L, 1L, 6L, 1));

        given(sectionRepository.findAll())
                .willReturn(sections);
    }

    @Test
    @DisplayName("그래프를 초기화 합니다.")
    void initGraphSuccess(){
        // when
        SubwayGraphService subwayGraphService = new SubwayGraphService(sectionRepository, lineRepository);

        // then
        assertThat(subwayGraphService).isNotNull();
    }

    @Test
    @DisplayName("구간 추가에 성공합니다.")
    void addSectionSuccessfully() {
        // given
        Section section = makeSection(2L, 6L, 7L, 3);
        SubwayGraphService subwayGraphService = new SubwayGraphService(sectionRepository, lineRepository);

        // when
        subwayGraphService.addSection(section);

        // then
        then(sectionRepository).should(times(1)).insert(section);
    }

    @Test
    @DisplayName("구간 삭제에 성공합니다.")
    void removeSectionSuccessfully() {
        // given
        Long lineId = 5L;
        SubwayGraphService subwayGraphService = new SubwayGraphService(sectionRepository, lineRepository);

        // when
        subwayGraphService.removeStation(lineId);

        // then
        then(sectionRepository).should(times(1)).deleteById(lineId);
    }

    @Test
    @DisplayName("호선에 해당하는 역을 모두 조회합니다.")
    void getLineWithStationsSuccessfully() {
        // given
        Long lineId = 1L;
        SubwayGraphService subwayGraphService = new SubwayGraphService(sectionRepository, lineRepository);

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

    private Section makeSection(Long lineId, Long upStationId, Long downStationId, Integer distance) {
        return Section.of(new Line(lineId, "name", "color"),
                new Station(upStationId, "name"),
                new Station(downStationId, "name"),
                distance);
    }

}
