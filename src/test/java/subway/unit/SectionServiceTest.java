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
import subway.dao.SectionDao;
import subway.dto.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SectionService sectionService;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private LineService lineService;

    @Autowired
    private StationService stationService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from section");
        jdbcTemplate.update("delete from station");
        jdbcTemplate.update("delete from line");
    }

    @Test
    @DisplayName("구간 추가 테스트")
    void saveSectionTest() {

        // given
        var 신사역 = stationService.saveStation(new StationRequest("신사역"));
        var 강남역 = stationService.saveStation(new StationRequest("강남역"));
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red"));

        // when
        var response = sectionService.save(신분당선.getId(), new SectionRequest(신사역.getId(), 강남역.getId(), 12));

        // then

        assertAll(
                () -> assertThat(response.getId()).isNotNull(),
                () -> assertThat(response.getUpStationId()).isEqualTo(신사역.getId()),
                () -> assertThat(response.getDownStationId()).isEqualTo(강남역.getId())
        );
    }

    @Test
    @DisplayName("구간 추가 예외 테스트 - 추가하려는 상행역이 노선에 등록되어있지 않은 하행 종점역일 경우")
    void saveSectionExceptionTest() {

        // given
        var 신사역 = stationService.saveStation(new StationRequest("신사역"));
        var 강남역 = stationService.saveStation(new StationRequest("강남역"));
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red"));
        sectionService.save(신분당선.getId(), new SectionRequest(신사역.getId(), 강남역.getId(), 12));

        var 양재역 = stationService.saveStation(new StationRequest("양재역"));
        var 미금역 = stationService.saveStation(new StationRequest("미금역"));


        var 추가하려는_구간 = new SectionRequest(양재역.getId(), 미금역.getId(), 5);

        // when & then
        assertThatThrownBy(() -> sectionService.save(신분당선.getId(), 추가하려는_구간))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 추가 예외 테스트 - 추가하려는 하행역이 노선에 이미 추가되어 있는 경우")
    void saveSectionExceptionTest1() {

        // given
        var 신사역 = stationService.saveStation(new StationRequest("신사역"));
        var 강남역 = stationService.saveStation(new StationRequest("강남역"));
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red"));
        sectionService.save(신분당선.getId(), new SectionRequest(신사역.getId(), 강남역.getId(), 12));

        var 추가하려는_구간 = new SectionRequest(강남역.getId(), 신사역.getId(), 3);

        // when & then
        assertThatThrownBy(() -> sectionService.save(신분당선.getId(), 추가하려는_구간))
                .isInstanceOf(IllegalArgumentException.class);
    }



}
