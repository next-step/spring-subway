package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

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

    @DisplayName("지하철 노선을 생성하면서 첫 구간도 함께 생성한다.")
    @Test
    void saveLine() {
        // given
        LineRequest lineRequest = new LineRequest(
                "5호선",
                "green",
                1L,
                2L,
                10L
        );

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then

        assertThat(lineDao.findById(lineResponse.getId())).extracting(
                Line::getName,
                Line::getColor
        ).contains("5호선", "green");

        assertThat(sectionDao.existByLineId(lineResponse.getId())).isTrue();
    }

}
