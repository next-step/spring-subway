package subway.dao;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import subway.domain.Line;

@JdbcTest
@Import(LineDao.class)
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Test
    @DisplayName("데이터 삽입 테스트")
    void insert() {
        Line line = new Line("10호선", "무지개");

        Line result = lineDao.insert(line);

        Assertions.assertThat(result).extracting("id").isNotNull();
    }

    @Test
    @DisplayName("데이터 전체 조회 테스트")
    void findAll() {
        List<Line> result = lineDao.findAll();

        Assertions.assertThat(result).containsExactly(new Line(1L, "7호선", "주황"));
    }

    @Test
    @DisplayName("데이터 단건 조회 테스트")
    void findById() {
        Line line = lineDao.findById(1L).get();

        Assertions.assertThat(line).isEqualTo(new Line(1L, "7호선", "주황"));
    }

    @Test
    @DisplayName("데이터 단건 조회 예외 테스트")
    void findByIdException() {
        Assertions.assertThatThrownBy(
                () -> lineDao.findById(100L)
                    .orElseThrow(() -> new IllegalArgumentException("없는 데이터"))
            ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("없는 데이터");
    }

    @Test
    @DisplayName("데이터 수정 테스트")
    void update() {
        lineDao.update(new Line(1L, "7호선", "파랑"));

        Assertions.assertThat(lineDao.findById(1L).get())
            .isEqualTo(new Line(1L, "7호선", "파랑"));
    }

    @Test
    @DisplayName("데이터 삭제 테스트")
    void deleteById() {
        Line line = new Line("10호선", "무지개");
        lineDao.insert(line);

        lineDao.deleteById(2L);

        Assertions.assertThat(lineDao.findById(2L).orElse(null))
            .isNull();
    }
}