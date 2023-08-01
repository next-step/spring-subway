package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.exception.ErrorCode.NOT_FOUND_LINE;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.exception.SubwayException;

@JdbcTest
@Import(LineDao.class)
@Sql({"classpath:schema.sql", "classpath:test-data.sql"})
public class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Test
    @DisplayName("라인 하나를 조회한다")
    void selectLine() {
        // given
        Station station = new Station(1L, "서울대입구역");
        Station station2 = new Station(2L, "상도역");
        Section section = new Section(1L, station, station2, new Distance(10));
        Line line = new Line(1L, "2호선", "green", new Sections(List.of(section)));

        // when
        Line newLine = lineDao.findById(line.getId())
            .orElseThrow(() -> new SubwayException(NOT_FOUND_LINE));

        // then
        assertThat(newLine.getId()).isEqualTo(line.getId());
        assertThat(newLine.getName()).isEqualTo(line.getName());
    }

    @Test
    @DisplayName("라인을 생성한다")
    void insertLine() {
        // given
        Line line = new Line("3호선", "blue");
        // when
        Line insert = lineDao.insert(line);
        // then
        assertThat(line.getName()).isEqualTo(insert.getName());
    }

    @Test
    @DisplayName("라인 하나를 삭제한다.")
    void insertSection() {
        // given
        Line line = new Line("3호선", "blue");
        Line insertLine = lineDao.insert(line);

        // when
        lineDao.deleteById(insertLine.getId());

        // then
        Optional<Line> selectLine = lineDao.findById(insertLine.getId());
        assertThat(selectLine.isEmpty()).isTrue();
    }
}
