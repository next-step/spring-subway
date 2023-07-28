package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.ui.dto.PathResponse;

@DisplayName("구간 조회 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @InjectMocks
    private PathService pathService;

    @Mock
    private SectionDao sectionDao;

    @Test
    @DisplayName("최단 경로와 거리를 조회한다.")
    void searchShortestPathsTest() {
        // given
        List<Section> sections = createInitialSections();
        Station source = new Station (1L, "교대역");
        Station middle = new Station(4L, "남부터미널역");
        Station target = new Station (3L, "양재역");
        List<Station> shortestPathStations = Arrays.asList(source, middle, target);

        given(sectionDao.findAll()).willReturn(sections);

        // when
        PathResponse response = pathService.findShortestPaths(source.getId(), target.getId());

        // then
        List<Station> actualStations = response.getStations().stream()
            .map(stationResponse -> new Station(stationResponse.getId(), stationResponse.getName()))
            .collect(Collectors.toList());
        assertThat(actualStations).isEqualTo(shortestPathStations);
        assertThat(response.getDistance()).isEqualTo(5);
    }

    private List<Station> createInitialStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(1L, "교대역"));
        stations.add(new Station(2L, "강남역"));
        stations.add(new Station(3L, "양재역"));
        stations.add(new Station(4L, "남부터미널역"));
        return stations;
    }

    private List<Line> createInitialLines() {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line(1L, "이호선", "초록색"));
        lines.add(new Line(2L, "신분당선", "빨간색"));
        lines.add(new Line(3L, "삼호선", "주황색"));
        return lines;
    }

    private List<Section> createInitialSections() {
        List<Line> lines = createInitialLines();
        List<Station> stations = createInitialStations();
        List<Section> sections = new ArrayList<>();

        /**
         * 교대역    --- *2호선* ---   강남역
         * |                        |
         * *3호선*                   *신분당선*
         * |                        |
         * 남부터미널역  --- *3호선* ---   양재
         */
        sections.add(new Section(1L, lines.get(0), stations.get(0), stations.get(1), 10));
        sections.add(new Section(1L, lines.get(1), stations.get(1), stations.get(2), 10));
        sections.add(new Section(1L, lines.get(2), stations.get(0), stations.get(3), 2));
        sections.add(new Section(1L, lines.get(2), stations.get(3), stations.get(2), 3));
        return sections;
    }
}