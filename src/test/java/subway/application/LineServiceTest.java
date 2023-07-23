package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.StationPair;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.StationResponse;
import subway.exception.IllegalLineException;

@DisplayName("라인 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class LineServiceTest {

    @InjectMocks
    private LineService lineService;

    @Mock
    private LineDao lineDao;
    @Mock
    private SectionDao sectionDao;

    @Test
    @DisplayName("노선을 저장한다.")
    void saveLineTest() {
        // given
        Line line = createInitialLine();
        Section section = new Section(1L, line.getId(), 1L, 2L, 10);
        LineRequest lineRequest = convertToLineRequest(line, section);

        given(lineDao.findByName(line.getName())).willReturn(Optional.empty());
        given(lineDao.insert(any(Line.class))).willReturn(line);
        given(sectionDao.insert(any(Section.class))).willReturn(section);

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        assertThat(lineResponse.getId()).isEqualTo(line.getId());
        assertThat(lineResponse.getName()).isEqualTo(line.getName());
    }

    @Test
    @DisplayName("동일한 이름으로 노선을 저장하면 예외를 던진다.")
    void saveLineDuplicateNameExceptionTest() {
        Line line = createInitialLine();
        Section section = new Section(1L, line.getId(), 1L, 2L, 10);
        LineRequest lineRequest = convertToLineRequest(line, section);

        given(lineDao.findByName(line.getName())).willReturn(Optional.of(line));

        // when & then
        assertThatThrownBy(() -> lineService.saveLine(lineRequest))
            .hasMessage("노선 이름은 중복될 수 없습니다.")
            .isInstanceOf(IllegalLineException.class);
    }

    @DisplayName("식별자로 노선을 가져온다.")
    @Test
    void findLineByIdTest() {
        // given
        Line line = createInitialLine();
        given(lineDao.findById(line.getId())).willReturn(Optional.of(line));

        // when
        Line result = lineService.findLineById(line.getId());

        // then
        assertThat(result).isEqualTo(line);
    }

    @DisplayName("식별자에 해당하는 노선이 없으면 예외를 던진다.")
    @Test
    void findLineByIdExceptionTest() {
        // given
        long lineId = 1L;
        given(lineDao.findById(lineId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> lineService.findLineById(lineId))
            .hasMessage("노선이 존재하지 않습니다.")
            .isInstanceOf(IllegalLineException.class);
    }

    @DisplayName("상행 종점부터 하행 종점까지 정렬된 역들을 반환한다.")
    @Test
    void findStationsInLineTest() {
        // given
        Line line = createInitialLine();

        given(lineDao.findById(line.getId())).willReturn(Optional.of(line));
        given(lineDao.findAllStationPair(line.getId())).willReturn(createInitialStationPairs());

        // when
        LineResponse lineResponse = lineService.findLineResponseById(line.getId());

        // then
        List<Long> expectedIds = getSortedStationIds();
        List<Long> actualIds = lineResponse.getStations().stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());

        // when
        assertThat(actualIds).isEqualTo(expectedIds);
    }

    private Line createInitialLine() {
        long lineId = 1L;
        return new Line(lineId, "1호선", "blue");
    }

    private List<Station> createInitialStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(1L, "오이도"));
        stations.add(new Station(2L, "정왕"));
        stations.add(new Station(3L, "안산"));
        stations.add(new Station(4L, "한대앞"));
        return stations;
    }

    private List<StationPair> createInitialStationPairs() {
        List<Station> stations = createInitialStations();
        List<StationPair> stationPairs = new ArrayList<>();
        stationPairs.add(new StationPair(stations.get(3), stations.get(0)));
        stationPairs.add(new StationPair(stations.get(0), stations.get(2)));
        stationPairs.add(new StationPair(stations.get(2), stations.get(1)));
        return stationPairs;
    }

    private LineRequest convertToLineRequest(Line line, Section section) {
        return new LineRequest(line.getName(), section.getUpStationId(),
            section.getDownStationId(), section.getDistance(), line.getColor());
    }

    private List<Long> getSortedStationIds() {
        return Arrays.asList(4L, 1L, 3L, 2L);
    }
}
