package subway.domain.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import subway.domain.EntityFactoryForTest;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.vo.Distance;
import subway.persistence.jdbcDao.LineDao;
import subway.persistence.jdbcDao.SectionDao;
import subway.persistence.jdbcDao.StationDao;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static subway.domain.EntityFactoryForTest.*;

@DisplayName("sectionRepository Test")
@Sql(value = "/truncate.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@JdbcTest
class SectionRepositoryTest {

    private SectionRepository sectionRepository;
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void init() {
        sectionRepository = new SectionDao(dataSource);
        lineRepository = new LineDao(dataSource);
        stationRepository = new StationDao(dataSource);
    }

    @Test
    @DisplayName("등록")
    void insert() {
        Section section = makeOneSection();

        Section inserted = sectionRepository.insert(section);

        section.setId(1L);
        assertThat(inserted).usingRecursiveComparison().isEqualTo(section);
    }

    @Test
    @DisplayName("전체 조회")
    void findAll() {
        Section section = makeOneSection();
        lineRepository.insert(section.getLine());
        stationRepository.insert(section.getUpStation());
        stationRepository.insert(section.getDownStation());
        Section inserted = sectionRepository.insert(section);

        List<Section> sections = sectionRepository.findAll();

        assertThat(sections).hasSize(1);
    }

    @Test
    @DisplayName("단건 조회")
    void findById() {
        Section section = makeOneSection();
        lineRepository.insert(section.getLine());
        stationRepository.insert(section.getUpStation());
        stationRepository.insert(section.getDownStation());
        Section inserted = sectionRepository.insert(section);

        Section findSection = sectionRepository.findById(inserted.getId());

        assertThat(findSection).usingRecursiveComparison()
                .isEqualTo(inserted);
    }

    @Test
    @DisplayName("삭제")
    void deleteById() {
        Section section = makeOneSection();
        Section inserted = sectionRepository.insert(section);

        assertThatCode(() -> {
            sectionRepository.deleteById(inserted.getId());
        }).doesNotThrowAnyException();
    }

    private static Section makeOneSection() {
        Line line = new Line(1L,"1호선", "red");
        Station upStation = new Station(1L, "노량진역");
        Station downStation = new Station(2L, "강남역");
        return Section.of(line, upStation, downStation, 10);
    }
}
