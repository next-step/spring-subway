package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

import javax.sql.DataSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LineDao DB CRUD 기능")
@JdbcTest
class LineDaoTest {

    private static final Line LINE_1 = new Line("신분당선", "bg-red-600");
    private static final Line LINE_2 = new Line("수인분당선", "bg-yellow-600");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate, dataSource);
        sectionDao = new SectionDao(jdbcTemplate, dataSource, stationDao);
        lineDao = new LineDao(jdbcTemplate, dataSource, sectionDao);
    }

    @DisplayName("Line 테이블에 엔티티를 삽입한다")
    @Test
    void insert() {
        // when
        Line insertedLine = lineDao.insert(LINE_1);

        // then
        assertThat(insertedLine.getId()).isNotNull();
        assertThat(insertedLine.getName()).isEqualTo(LINE_1.getName());
        assertThat(insertedLine.getColor()).isEqualTo(LINE_1.getColor());
        assertThat(insertedLine.getSections()).hasSize(0);
    }

    @DisplayName("Line 테이블에서 모든 엔티티를 조회한다")
    @Test
    void findAll() {
        // given
        lineDao.insert(LINE_1);
        lineDao.insert(LINE_2);

        // when
        List<Line> foundLines = lineDao.findAll();

        // then
        assertThat(foundLines)
                .flatExtracting(Line::getId).doesNotContainNull();
        assertThat(foundLines)
                .flatExtracting(Line::getName).containsExactly(LINE_1.getName(), LINE_2.getName());
        assertThat(foundLines)
                .flatExtracting(Line::getColor).containsExactly(LINE_1.getColor(), LINE_2.getColor());
    }

    @DisplayName("Line id로 Line 테이블의 엔티티를 조회한다")
    @Test
    public void findById() {
        // given
        Line insertedLine = lineDao.insert(LINE_1);

        // when
        Optional<Line> foundOptionalLine = lineDao.findById(insertedLine.getId());

        // then
        Line foundLine = assertDoesNotThrow(() -> foundOptionalLine.get());
        assertThat(foundLine.getId()).isNotNull();
        assertThat(foundLine.getName()).isEqualTo(LINE_1.getName());
        assertThat(foundLine.getColor()).isEqualTo(LINE_1.getColor());
    }

    @DisplayName("Line id로 section 리스트를 갖는 Line 테이블의 엔티티를 조회한다")
    @Test
    public void findByIdWithSections() {
        // given
        Line insertedLine = lineDao.insert(LINE_1);
        Station insertedStation1 = stationDao.insert(new Station("강남역"));
        Station insertedStation2 = stationDao.insert(new Station("정자역"));
        Section insertedSection = sectionDao.insert(insertedLine.getId(), new Section(insertedStation1, insertedStation2, 10));

        // when
        Optional<Line> foundOptionalLine = lineDao.findById(insertedLine.getId());

        // then
        Line foundLine = assertDoesNotThrow(() -> foundOptionalLine.get());
        assertThat(foundLine.getSections().get(0)).isEqualTo(insertedSection);
    }

    @DisplayName("Line 테이블의 엔티티를 수정한다")
    @Test
    public void update() {
        // given
        Line insertedLine = lineDao.insert(LINE_1);

        // when
        lineDao.update(new Line(insertedLine.getId(), LINE_2.getName(), LINE_2.getColor()));

        // then
        Line updatedLine = lineDao.findById(insertedLine.getId()).get();
        assertThat(updatedLine.getName()).isEqualTo(LINE_2.getName());
        assertThat(updatedLine.getColor()).isEqualTo(LINE_2.getColor());
    }

    @DisplayName("Line 테이블의 엔티티를 삭제한다")
    @Test
    void deleteById() {
        // given
        Line insertedLine = lineDao.insert(LINE_1);

        // when
        lineDao.deleteById(insertedLine.getId());

        // then
        assertThat(lineDao.findById(insertedLine.getId())).isEmpty();
    }
}
