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
import subway.dto.request.LineCreateRequest;
import subway.dto.response.LineResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.domain.fixture.StationFixture.createStation;

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

    @DisplayName("지하철 노선을 생성하면서 첫 구간도 함께 생성한다.")
    @Test
    void saveLineAndSaveFirstSection() {
        // given
        final Station upStation = stationDao.insert(createStation("낙성대"));
        final Station downStation = stationDao.insert(createStation("사당"));
        final Line line = new Line("7호선", "청색");
        final LineCreateRequest lineCreateRequest = new LineCreateRequest(
                line.getName(),
                line.getColor(),
                upStation.getId(),
                downStation.getId(),
                10L);

        // when
        final LineResponse lineResponse = lineService.createLineAndFirstSection(lineCreateRequest);

        // then
        final Optional<Line> optionalLine = lineDao.findById(lineResponse.getId());
        assertThat(optionalLine).isPresent();
        assertThat(optionalLine.get())
                .extracting(Line::getName, Line::getColor)
                .contains(line.getName(), line.getColor());
        assertThat(sectionDao.findAllByLineId(lineResponse.getId())).hasSize(1);
    }
}
