package subway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.application.LineService;
import subway.application.PathFindService;
import subway.application.SectionService;
import subway.application.StationService;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class PathFindServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private PathFindService findService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from section");
        jdbcTemplate.update("delete from station");
        jdbcTemplate.update("delete from line");
    }


    @DisplayName("최단 경로 조회 기능 테스트")
    @Test
    void findShortPathTest() {

        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 양재역 = stationService.saveStation(new StationRequest("양재역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red"));
        sectionService.save(신분당선.getId(), new SectionRequest(강남역, 양재역, 3));

        var 부발역 = stationService.saveStation(new StationRequest("부발역")).getId();
        var 경기광주역 = stationService.saveStation(new StationRequest("경기광주역")).getId();
        var 경강선 = lineService.saveLine(new LineRequest("경강선", "blue"));
        sectionService.save(경강선.getId(), new SectionRequest(강남역, 경기광주역, 15));
        sectionService.save(경강선.getId(), new SectionRequest(경기광주역, 부발역, 8));
        sectionService.save(신분당선.getId(), new SectionRequest(양재역, 경기광주역, 10));

        var response = findService.findShortPath(강남역, 부발역);

        assertAll(
                () -> assertThat(response.getDistatnce()).isEqualTo(21),
                () -> assertThat(response.getStations().get(0).getId()).isEqualTo(강남역),
                () -> assertThat(response.getStations().get(1).getId()).isEqualTo(양재역),
                () -> assertThat(response.getStations().get(2).getId()).isEqualTo(경기광주역),
                () -> assertThat(response.getStations().get(3).getId()).isEqualTo(부발역)
        );
    }


    @DisplayName("이용거리가 10km 이내일 경우 운행 요금 테스트")
    @Test
    void basicChargeTest() {

        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 양재역 = stationService.saveStation(new StationRequest("양재역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red"));
        sectionService.save(신분당선.getId(), new SectionRequest(강남역, 양재역, 3));

        var response = findService.findShortPath(강남역, 양재역);

        assertThat(response.getCharge()).isEqualTo(1250);
    }

    @DisplayName("이용거리가 10km~50km 사이일 경우 운행 요금 테스트")
    @Test
    void over10ChargeTest() {

        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 부발역 = stationService.saveStation(new StationRequest("부발역")).getId();
        var 경기광주역 = stationService.saveStation(new StationRequest("경기광주역")).getId();
        var 경강선 = lineService.saveLine(new LineRequest("경강선", "blue"));
        sectionService.save(경강선.getId(), new SectionRequest(강남역, 경기광주역, 15));
        sectionService.save(경강선.getId(), new SectionRequest(경기광주역, 부발역, 8));

        var response = findService.findShortPath(강남역, 부발역);

        assertThat(response.getCharge()).isEqualTo(1550);
    }

    @DisplayName("이용거리가 50km 초과일 경우 운행 요금 테스트")
    @Test
    void over50ChargeTest() {

        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 부발역 = stationService.saveStation(new StationRequest("부발역")).getId();
        var 경기광주역 = stationService.saveStation(new StationRequest("경기광주역")).getId();
        var 경강선 = lineService.saveLine(new LineRequest("경강선", "blue"));
        sectionService.save(경강선.getId(), new SectionRequest(강남역, 경기광주역, 15));
        sectionService.save(경강선.getId(), new SectionRequest(경기광주역, 부발역, 50));
        var response = findService.findShortPath(강남역, 부발역);

        assertThat(response.getCharge()).isEqualTo(2250);
    }
}
