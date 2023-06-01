package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("SectionDao DB CRUD 기능")
@Sql(value = "classpath:section-testdata.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@JdbcTest
class SectionDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;
    private StationDao stationDao;
    private SectionDao sectionDao;

    private List<Line> lines;
    private List<Station> stations;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate, dataSource);
        sectionDao = new SectionDao(jdbcTemplate, dataSource, stationDao);
        lineDao = new LineDao(jdbcTemplate, dataSource, sectionDao);

        lines = lineDao.findAll();
        stations = stationDao.findAll();
    }

    @DisplayName("Section 테이블에 엔티티를 삽입한다")
    @Test
    void insert() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);

        // when
        Section insertedSection = sectionDao.insert(line.getId(), new Section(station1, station2, 10));

        // then
        assertThat(insertedSection.getId()).isNotNull();
    }

    @DisplayName("Section 테이블의 모든 엔티티를 조회한다")
    @Test
    void findAll() {
        // given
        Line line = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        Station station3 = stations.get(2);
        sectionDao.insert(line.getId(), new Section(station1, station2, 5));
        sectionDao.insert(line.getId(), new Section(station2, station3, 10));

        // when
        List<Section> sections = sectionDao.findAll();

        // then
        assertThat(sections)
                .flatExtracting(Section::getId).doesNotContainNull();
        assertThat(sections)
                .flatExtracting(Section::getUpStation).containsExactly(station1, station2);
        assertThat(sections)
                .flatExtracting(Section::getDownStation).containsExactly(station2, station3);
        assertThat(sections)
                .flatExtracting(Section::getDistance).containsExactly(5, 10);
    }

    @DisplayName("Line id로 Section 테이블의 엔티티를 조회한다")
    @Test
    void findAllByLineId() {
        // given
        Line line1 = lines.get(0);
        Line line2 = lines.get(1);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        Station station3 = stations.get(2);
        sectionDao.insert(line1.getId(), new Section(station1, station2, 5));
        sectionDao.insert(line2.getId(), new Section(station2, station3, 5));

        // when
        List<Section> sections = sectionDao.findAllByLineId(line1.getId());

        // then
        assertThat(sections).hasSize(1);
        assertThat(sections)
                .flatExtracting(Section::getUpStation).containsExactly(station1);
        assertThat(sections)
                .flatExtracting(Section::getDownStation).containsExactly(station2);
    }

    @DisplayName("Section id로 Section 테이블의 엔티티를 조회한다")
    @Test
    void findById() {
        // given
        Line line1 = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        Section insertedSection = sectionDao.insert(line1.getId(), new Section(station1, station2, 5));

        // when
        Optional<Section> foundOptionalSection = sectionDao.findById(insertedSection.getId());

        // then
        Section foundSection = assertDoesNotThrow(() -> foundOptionalSection.get());
        assertThat(foundSection.getId()).isNotNull();
        assertThat(foundSection.getUpStation()).isEqualTo(station1);
        assertThat(foundSection.getDownStation()).isEqualTo(station2);
        assertThat(foundSection.getDistance()).isEqualTo(5);
    }

    @DisplayName("Section id로 Section 테이블의 엔티티를 삭제한다")
    @Test
    void deleteById() {
        // given
        Line line1 = lines.get(0);
        Station station1 = stations.get(0);
        Station station2 = stations.get(1);
        Section insertedSection = sectionDao.insert(line1.getId(), new Section(station1, station2, 5));

        // when
        sectionDao.deleteById(insertedSection.getId());

        // then
        assertThat(sectionDao.findById(insertedSection.getId())).isEmpty();
    }
}
