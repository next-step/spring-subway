package subway.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.application.LineService;
import subway.application.SectionService;
import subway.application.StationService;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.dto.StationRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class LineServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @Autowired
    private SectionService sectionService;


    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from section");
        jdbcTemplate.update("delete from station");
        jdbcTemplate.update("delete from line");
    }

    @Test
    @DisplayName("노선 목록 조회 테스트")
    void getLinesTest() {

        // given
        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 양재역 = stationService.saveStation(new StationRequest("양재역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red"));
        sectionService.save(신분당선.getId(), new SectionRequest(강남역, 양재역, 3));

        var 부발역 = stationService.saveStation(new StationRequest("부발역")).getId();
        var 경기광주역 = stationService.saveStation(new StationRequest("경기광주역")).getId();
        var 경강선 = lineService.saveLine(new LineRequest("경강선", "blue"));
        sectionService.save(경강선.getId(), new SectionRequest(부발역, 경기광주역, 3));

        // when
        var response = lineService.findLineResponses();

        // then
        assertAll(
                () -> assertThat(response.get(0).getId()).isEqualTo(신분당선.getId()),
                () -> assertThat(response.get(0).getColor()).isEqualTo(신분당선.getColor()),
                () -> assertThat(response.get(0).getStations().get(0).getId()).isEqualTo(강남역),
                () -> assertThat(response.get(0).getStations().get(1).getId()).isEqualTo(양재역),
                () -> assertThat(response.get(1).getId()).isEqualTo(경강선.getId()),
                () -> assertThat(response.get(1).getColor()).isEqualTo(경강선.getColor()),
                () -> assertThat(response.get(1).getStations().get(0).getId()).isEqualTo(부발역),
                () -> assertThat(response.get(1).getStations().get(1).getId()).isEqualTo(경기광주역)
        );
    }

    @Test
    @DisplayName("지하철 노선 단건 조회 테스트")
    void getLineTest() {

        // given
        var 강남역 = stationService.saveStation(new StationRequest("강남역"));
        var 양재역 = stationService.saveStation(new StationRequest("양재역"));
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red"));
        SectionResponse 구간_추가 = sectionService.save(신분당선.getId(), new SectionRequest(강남역.getId(), 양재역.getId(), 3));

        // when
        var response = lineService.findLineById(신분당선.getId());

        // then
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(신분당선.getId()),
                () -> assertThat(response.getColor()).isEqualTo(신분당선.getColor()),
                () -> assertThat(response.getSections().getSectionList().get(0).getUpStation().getName()).isEqualTo(강남역.getName()),
                () -> assertThat(response.getSections().getSectionList().get(0).getDownStation().getName()).isEqualTo(양재역.getName()),
                () -> assertThat(response.getSections().getSectionList().get(0).getDistance()).isEqualTo(구간_추가.getDistance())
        );
    }

}
