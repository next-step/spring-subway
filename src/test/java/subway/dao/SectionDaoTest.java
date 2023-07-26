package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;
import static subway.exception.ErrorCode.NOT_FOUND_SECTION;
import static subway.exception.ErrorCode.NOT_FOUND_STATION;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import subway.domain.Distance;
import subway.domain.Section;
import subway.domain.Station;
import subway.exception.SubwayException;

public class SectionDaoTest {

    private DataSource dataSource;

    private SectionDao sectionDao;

    private StationDao stationDao;

    @BeforeEach
    public void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
            .generateUniqueName(true)
            .setType(H2)
            .setScriptEncoding("UTF-8")
            .ignoreFailedDrops(true)
            .addScript("schema.sql")
            .addScripts("test.sql")
            .build();
        sectionDao = new SectionDao(new JdbcTemplate(dataSource), dataSource);
        stationDao = new StationDao(new JdbcTemplate(dataSource), dataSource);
    }

    @Test
    @DisplayName("section를 조회한다")
    void selectSection() {
        Section section = sectionDao.selectSection(1L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_SECTION));
        assertThat(section.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("section들을 조회한다")
    void selectSections() {
        // given
        Long lindId = 1L;
        // when
        List<Section> sections = sectionDao.selectSections(lindId)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_SECTION));
        // then
        assertThat(sections.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("section을 생성한다")
    void createSection() {
        // given
        Station station3 = stationDao.findById(3L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Station station4 = stationDao.findById(4L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Distance distance = new Distance(10);

        // when
        Section insertSection = sectionDao.insert(new Section(station3, station4, distance), 1L);
        // then
        assertAll(
            () -> assertThat(insertSection.getUpStation()).isEqualTo(station3),
            () -> assertThat(insertSection.getDownStation()).isEqualTo(station4),
            () -> assertThat(new Distance(insertSection.getDistance())).isEqualTo(distance)
        );
    }

    @Test
    @DisplayName("sections들을 생성한다")
    void createSections() {
        // given
        Station station3 = stationDao.findById(3L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Station station4 = stationDao.findById(4L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Station station5 = stationDao.findById(5L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        List<Section> sections = List.of(
            new Section(station3, station4, new Distance(10)),
            new Section(station4, station5, new Distance(10))
        );
        Long lindId = 1L;
        int beforeSize = sectionDao.selectSections(lindId).orElseThrow().size();

        // when
        sectionDao.insertSections(sections, lindId);
        // then
        int currentSize = beforeSize + sections.size();
        assertThat(sectionDao.selectSections(lindId).orElseThrow().size())
            .isEqualTo(currentSize);
    }

    @Test
    @DisplayName("section을 삭제한다")
    void deleteSection() {
        // given
        Station station3 = stationDao.findById(3L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Station station4 = stationDao.findById(4L)
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        Section insertSection = sectionDao.insert(
            new Section(station3, station4, new Distance(10)), 1L);

        // when
        sectionDao.deleteSections(List.of(insertSection));
        // then
        Optional<Section> section = sectionDao.selectSection(insertSection.getId());
        assertThat(section).isEmpty();
    }
}
