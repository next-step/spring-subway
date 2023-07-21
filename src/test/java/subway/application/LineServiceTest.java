package subway.application;

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
    private SectionDao sectionDao;

    @Autowired
    private StationDao stationDao;

    @DisplayName("지하철 노선을 생성하면서 첫 구간도 함께 생성한다.")
    @Test
    void saveLineAndSaveFirstSection() {
        // given
        Line line = lineDao.insert(new Line("5호선", "green"));
        Station upStation = stationDao.insert(new Station("1호선"));
        Station downStation = stationDao.insert(new Station("2호선"));
        CreateLineRequest createLineRequest = new CreateLineRequest(
                line.getName(),
                line.getColor(),
                upStation.getId(),
                downStation.getId(),
                10L);

        // when
        LineResponse lineResponse = lineService.saveLine(createLineRequest);

        // then
        assertThat(lineDao.findById(lineResponse.getId()))
                .extracting(Line::getName, Line::getColor)
                .contains("5호선", "green");
        assertThat(sectionDao.existByLineId(lineResponse.getId())).isTrue();
    }
}
