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
import subway.domain.Section;
import subway.dto.*;

import java.util.List;

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
        var 신사역 = stationService.saveStation(new StationRequest("신사역")).getId();
        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red")).getId();

        // when
        var response = sectionService.save(신분당선, new SectionRequest(신사역, 강남역, 12));

        // then

        assertAll(
                () -> assertThat(response.getId()).isNotNull(),
                () -> assertThat(response.getUpStationId()).isEqualTo(신사역),
                () -> assertThat(response.getDownStationId()).isEqualTo(강남역)
        );
    }

    @Test
    @DisplayName("구간 추가 예외 테스트 - 추가하려는 상행역이 노선에 등록되어있지 않은 하행 종점역일 경우")
    void saveSectionExceptionTest() {

        // given
        var 신사역 = stationService.saveStation(new StationRequest("신사역")).getId();
        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red")).getId();
        sectionService.save(신분당선, new SectionRequest(신사역, 강남역, 12));

        var 양재역 = stationService.saveStation(new StationRequest("양재역")).getId();
        var 미금역 = stationService.saveStation(new StationRequest("미금역")).getId();


        var 추가하려는_구간 = new SectionRequest(양재역, 미금역, 5);

        // when & then
        assertThatThrownBy(() -> sectionService.save(신분당선, 추가하려는_구간))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 추가 예외 테스트 - 추가하려는 하행역이 노선에 이미 추가되어 있는 경우")
    void saveSectionExceptionTest1() {

        // given
        var 신사역 = stationService.saveStation(new StationRequest("신사역")).getId();
        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red")).getId();
        sectionService.save(신분당선, new SectionRequest(신사역, 강남역, 12));

        var 추가하려는_구간 = new SectionRequest(강남역, 신사역, 3);

        // when & then
        assertThatThrownBy(() -> sectionService.save(신분당선, 추가하려는_구간))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("구간 삭제 테스트")
    void deleteSectionTest() {

        // given
        var 신사역 = stationService.saveStation(new StationRequest("신사역")).getId();
        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red")).getId();
        sectionService.save(신분당선, new SectionRequest(신사역, 강남역, 3));

        // when
        sectionService.delete(신분당선, 강남역);

        // then
        List<Section> sections = sectionDao.findAllByLineId(신분당선);

        assertThat(sections).isEmpty();
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트 - 삭제하려는 구간이 마지막 구간이 아닐 경우")
    void deleteSectionExceptionTest() {

        // given
        var 신사역 = stationService.saveStation(new StationRequest("신사역")).getId();
        var 강남역 = stationService.saveStation(new StationRequest("강남역")).getId();
        var 신분당선 = lineService.saveLine(new LineRequest("신분당선", "red")).getId();
        sectionService.save(신분당선, new SectionRequest(신사역, 강남역, 3));

        // when & then
        assertThatThrownBy(() -> sectionService.delete(신분당선, 신사역)).isInstanceOf(IllegalArgumentException.class);
    }


}
