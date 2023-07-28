package subway.application;

import static org.assertj.core.api.Assertions.assertThat;

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
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

@DisplayName("노선 서비스 테스트 - 진짜 협력 객체 사용")
@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private StationDao stationDao;

    private Station station1;
    private Station station2;

    @BeforeEach
    void setUp() {
        station1 = stationDao.insert(new Station("암사"));
        station2 = stationDao.insert(new Station("모란"));
    }

    @DisplayName("지하철 노선을 생성하면서 첫 구간도 함께 생성한다.")
    @Test
    void saveLine() {
        // given

        LineRequest lineRequest = new LineRequest(
                "5호선",
                "green",
                station1.getId(),
                station2.getId(),
                10L
        );

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then

        assertThat(lineDao.findById(lineResponse.getId()).get()).extracting(
                Line::getName,
                Line::getColor
        ).contains("5호선", "green");

        assertThat(sectionDao.existByLineId(lineResponse.getId())).isTrue();
    }

}
