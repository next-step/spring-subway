package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.LineDao;
import subway.domain.Line;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Line 서비스 로직")
@SpringBootTest
class LineServiceTest {

    private static final LineRequest REQUEST_1 = new LineRequest("신분당선", "bg-red-600");
    private static final LineRequest REQUEST_2 = new LineRequest("수인분당선", "bg-yellow-600");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private LineService lineService;
    @Autowired
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        // reset line table
        jdbcTemplate.update("delete from line");
    }

    @DisplayName("Line을 저장한다")
    @Test
    void saveLine() {
        // when
        LineResponse response = lineService.saveLine(REQUEST_1);

        // then
        Line foundLine = assertDoesNotThrow(() -> lineDao.findById(response.getId()).get());
        assertThat(foundLine.getName()).isEqualTo(REQUEST_1.getName());
        assertThat(foundLine.getColor()).isEqualTo(REQUEST_1.getColor());
    }

    @DisplayName("id를 통해 Line을 찾는다")
    @Test
    void findLineResponseByValidId() {
        // given
        LineResponse savedResponse = lineService.saveLine(REQUEST_1);

        // when
        LineResponse foundResponse = lineService.findLineResponseById(savedResponse.getId());

        // then
        assertThat(foundResponse.getId()).isNotNull();
        assertThat(foundResponse.getName()).isEqualTo(REQUEST_1.getName());
        assertThat(foundResponse.getColor()).isEqualTo(REQUEST_1.getColor());
    }

    @DisplayName("존재하지 않는 id를 통해 Line을 찾는다")
    @Test
    void findLineResponseByNonExistenceId() {
        // given
        LineResponse savedResponse = lineService.saveLine(REQUEST_1);

        // when, then
        assertThatThrownBy(() -> lineService.findLineResponseById(savedResponse.getId() + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Line을 수정한다")
    @Test
    void updateLine() {
        // given
        LineResponse savedResponse = lineService.saveLine(REQUEST_1);

        // when
        lineService.updateLine(savedResponse.getId(), REQUEST_2);

        // then
        Line foundLine = lineDao.findById(savedResponse.getId()).get();
        assertThat(foundLine.getName()).isEqualTo(REQUEST_2.getName());
        assertThat(foundLine.getColor()).isEqualTo(REQUEST_2.getColor());
    }

    @DisplayName("Line을 삭제한다")
    @Test
    void deleteLineById() {
        // given
        LineResponse savedResponse = lineService.saveLine(REQUEST_1);

        // when
        lineService.deleteLineById(savedResponse.getId());

        // then
        assertThat(lineDao.findById(savedResponse.getId())).isEmpty();
    }
}
