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
import subway.domain.Station;
import subway.domain.fixture.StationFixture;
import subway.dto.request.CreateLineRequest;
import subway.dto.response.LineResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService lineService;

    @Autowired
    private LineDao lineDao;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    private StationFixture stationFixture;

    @BeforeEach
    void setUp() {
        stationFixture = new StationFixture();
        stationFixture.init(stationDao);
    }

    @DisplayName("지하철 노선을 생성하면서 첫 구간도 함께 생성한다.")
    @Test
    void saveLineAndSaveFirstSection() {
        // given
        final Line line = new Line("1호선", "그린");
        final Station upStation = stationFixture.getStationA();
        final Station downStation = stationFixture.getStationB();
        final CreateLineRequest createLineRequest = new CreateLineRequest(
                line.getName(),
                line.getColor(),
                upStation.getId(),
                downStation.getId(),
                10L);

        // when
        final LineResponse lineResponse = lineService.saveLine(createLineRequest);

        // then
        assertThat(lineDao.findById(lineResponse.getId()))
                .extracting(Line::getName, Line::getColor)
                .contains(line.getName(), line.getColor());
        assertThat(sectionDao.findAllByLineId(lineResponse.getId())).hasSize(1);
    }
}
