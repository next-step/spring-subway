package subway.application;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

/**
 * 교대역  --- *2호선*[1] --   강남역
 * |                        |
 * *3호선* [2]              *신분당선* [5]
 * |                        |
 * 남부터미널역 -- *3호선*[3] --   양재
 *
 * {
 *     "stations": [
 *         {
 *             "id": 5,
 *             "name": "교대역"
 *         },
 *         {
 *             "id": 7,
 *             "name": "남부터미널역"
 *         },
 *         {
 *             "id": 8,
 *             "name": "양재역"
 *         }
 *     ],
 *     "distance": 5
 * }
 */

@DisplayName("PathService 테스트")
@ExtendWith(MockitoExtension.class)
public class PathServiceTest {

    private Long 출발역_아이디;
    private Long 도착역_아이디;
    private Station 교대역 = new Station(5L, "교대역");
    private Station 강남역 = new Station(6L, "강남역");
    private Station 남부터미널역 = new Station(7L, "남부터미널역");
    private Station 양재역 = new Station(8L, "양재역");
    private Line 이호선 = new Line(2L, "2호선", "빨강");
    private Line 삼호선 = new Line(3L, "삼호선", "노랑");
    private Line 신분당선 = new Line(4L, "신분당선", "파랑");
    private Section 교대_강남 = new Section(2L, 교대역, 강남역, 이호선, 1);
    private Section 교대_남부터미널 = new Section(3L, 교대역, 남부터미널역, 삼호선, 2);
    private Section 강남_양재 = new Section(4L, 강남역, 양재역, 신분당선, 5);
    private Section 남부터미널_양재 = new Section(5L, 남부터미널역, 양재역, 삼호선, 3);

    @Mock
    private SectionDao sectionDao;
    @Mock
    private StationDao stationDao;
    @InjectMocks
    private PathService pathService;

    @Test
    @DisplayName("성공 : 출발역과 도착역 사이의 최단 거리 역 정보를 리턴")
    void findMinimumDistanceStations() {
        // given
        출발역_아이디 = 5L;
        도착역_아이디 = 8L;

        // when
        when(sectionDao.findAll()).thenReturn(
            List.of(교대_강남, 교대_남부터미널, 강남_양재, 남부터미널_양재)
        );
        when(stationDao.findById(출발역_아이디).get()).thenReturn(교대역);
        when(stationDao.findById(도착역_아이디).get()).thenReturn(양재역);

        // then
        Assertions.assertThat(pathService.findShortestPath(출발역_아이디, 도착역_아이디).getDistance()).isEqualTo(5D);
        Mockito.verify(sectionDao).findAll();
        Mockito.verify(stationDao).findById(출발역_아이디);
        Mockito.verify(stationDao).findById(도착역_아이디);
    }

    @Test
    @DisplayName("예외 : 존재하지 않은 출발역이나 도착역을 조회 할 경우")
    void exceptionNoStatoin() {
        // when
        when(stationDao.findById(5L)).thenReturn(Optional.of(교대역));
        when(stationDao.findById(100L)).thenReturn(Optional.empty());

        // then
        Assertions.assertThatThrownBy(() -> pathService.findShortestPath(5L, 100L))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
