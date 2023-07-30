package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.request.PathFindRequest;
import subway.dto.response.PathFindResponse;
import subway.dto.response.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static subway.domain.fixture.LineFixture.createLine;
import static subway.domain.fixture.SectionFixture.createSection;
import static subway.domain.fixture.StationFixture.createStation;

@SpringBootTest
@Transactional
class PathServiceTest {


    @Autowired
    private PathService pathService;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private LineDao lineDao;

    private Line lineA;
    private Line lineB;
    private Line lineC;

    private Station stationA;
    private Station stationB;
    private Station stationC;
    private Station stationD;
    private Station stationE;
    private Station stationF;

    private Section sectionA;
    private Section sectionB;
    private Section sectionC;
    private Section sectionD;
    private Section sectionE;
    private Section sectionF;

    /**
     * StationA                 StationF
     * |                        |
     * *LineA*                   *LineA*
     * |                        |
     * StationB  --- *LineA* ---StationC
     * |                        |
     * *LineB*                  *LineC*
     * |                        |
     * StationD --- *LineB* --- StationE
     */

    @BeforeEach
    void setUp() {
        lineA = lineDao.insert(createLine("2호선"));
        lineB = lineDao.insert(createLine("3호선"));
        lineC = lineDao.insert(createLine("7호선"));

        stationA = stationDao.insert(createStation("낙성대"));
        stationB = stationDao.insert(createStation("사당"));
        stationC = stationDao.insert(createStation("방배"));
        stationD = stationDao.insert(createStation("서초"));
        stationE = stationDao.insert(createStation("교대"));
        stationF = stationDao.insert(createStation("잠실"));

        sectionA = sectionDao.insert(createSection(lineA, stationA, stationB, 10));
        sectionB = sectionDao.insert(createSection(lineA, stationB, stationC, 20));
        sectionC = sectionDao.insert(createSection(lineB, stationB, stationD, 5));
        sectionD = sectionDao.insert(createSection(lineB, stationD, stationE, 10));
        sectionE = sectionDao.insert(createSection(lineA, stationC, stationF, 15));
        sectionF = sectionDao.insert(createSection(lineC, stationC, stationE, 1));
    }

    @Test
    @DisplayName("시작역부터 도착역까지 가장 짦은 거리로 갈 수 있는 역 목록을 반환한다.")
    void getShortPathTest() {
        // given
        final PathFindRequest request = new PathFindRequest(stationF.getId(), stationD.getId());

        // when
        final PathFindResponse response = pathService.findShortPath(request);

        // then
        assertThat(response.getDistance()).isEqualTo(26);
        assertThat(response.getStations()).hasSize(4)
                .extracting(StationResponse::getName)
                .containsExactly(stationF.getName(), stationC.getName(), stationE.getName(), stationD.getName());

    }


    @Test
    @DisplayName("시작역과 도착역이 같은 경우 예외를 던진다.")
    void SourceEqualsTargetThenThrow() {
        // given
        final PathFindRequest request = new PathFindRequest(stationA.getId(), stationA.getId());

        // when , then
        assertThatCode(() -> pathService.findShortPath(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 같은 경우 최단 거리를 구할 수 없습니다.");
    }

    @Test
    @DisplayName("시작역과 도착역이 연결되어 있지 않은 예외를 던진다.")
    void SourceNotConnectionTargetThenThrow() {
        // given
        Station station = stationDao.insert(createStation("어린이 대공원역"));
        final PathFindRequest request = new PathFindRequest(stationA.getId(), station.getId());

        // when , then
        assertThatCode(() -> pathService.findShortPath(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.");
    }

    @Test
    @DisplayName("경로 조회 하고자하는 역이 없는 경우 예외를 던진다.")
    void SourceOrTargetNotFoundThenThrow() {
        // given
        Station station = stationDao.insert(createStation("어린이 대공원역"));
        final PathFindRequest request = new PathFindRequest(-1L, 99L);

        // when , then
        assertThatCode(() -> pathService.findShortPath(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역을 찾을 수 없습니다.");
    }
}
