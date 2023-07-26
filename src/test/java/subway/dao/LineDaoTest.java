package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import subway.domain.Line;
import subway.domain.Section;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LineDaoTest {
    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();

        lineDao = new LineDao(new JdbcTemplate(dataSource), dataSource);
    }

    @Test
    @DisplayName("id로 Line을 조회하면 Line이 포함하는 Sections, Station들을 함께 Optional을 반환한다.")
    void findByIdWithSectionsAndStations() {
        // when
        Line line = lineDao.findById(1L).get();
        Section section = line.getSections().getSections().get(0);

        // then
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("green");
        assertThat(line.getSections().getSections()).hasSize(1);
        assertThat(section.getUpStation().getId()).isEqualTo(1L);
        assertThat(section.getDownStation().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("id가 존재하지 않는 경우 Empty Optional을 반환한다.")
    void findByNonExistId() {
        assertThat(lineDao.findById(3L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("name이 중복되지 않는 Line을 삽입할 수 있다.")
    void insertNoDuplicateName() {
        // given
        Line line = new Line("3호선", "orange");

        // when
        Line persistentLine = lineDao.insert(line);

        // then
        assertThat(persistentLine.getId()).isEqualTo(3L);
        assertThat(persistentLine.getName()).isEqualTo("3호선");
        assertThat(persistentLine.getColor()).isEqualTo("orange");
    }

    @Test
    @DisplayName("name이 중복되는 Line은 삽입할 수 없다.")
    void insertDuplicateName() {
        // given
        Line line = new Line("2호선", "orange");

        // when, then
        assertThatCode(() -> lineDao.insert(line)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("id를 파라미터로 Line을 수정할 수 있다.")
    void updateLine() {
        // given
        Line line = new Line(1L, "3호선", "orange");

        // when
        lineDao.update(line);
        Line persistenceLine = lineDao.findById(1L).get();

        // then
        assertThat(persistenceLine.getId()).isEqualTo(1L);
        assertThat(persistenceLine.getName()).isEqualTo("3호선");
        assertThat(persistenceLine.getColor()).isEqualTo("orange");
    }

    @Test
    @DisplayName("이미 존재하는 name으로 Line을 수정할 수 없다.")
    void updateDuplicateNameLine() {
        // given
        Line otherLine = new Line("3호선", "orange");
        lineDao.insert(otherLine);
        Line line = new Line(1L, "3호선", "orange");

        // when, then
        assertThatCode(() -> lineDao.update(line)).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("id를 파라미터로 Line과 연관된 Section들을 삭제할 수 있다.")
    void deleteLine() {
        // when
        lineDao.deleteById(1L);

        // then
        assertThat(lineDao.findById(1L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("존재하는 모든 Line을 연관관계 없이 조회할 수 있다")
    void findAllLines() {
        // when
        List<Line> all = lineDao.findAll();

        // then
        assertThat(all).contains(
                new Line(1L, "2호선", "green"),
                new Line(2L, "4호선", "cyan")
        );
    }
}