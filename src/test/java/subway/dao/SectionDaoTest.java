package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;
import static subway.exception.ErrorCode.NOT_FOUND_SECTION;

import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import subway.domain.Distance;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.exception.SubwayException;

public class SectionDaoTest {

    private DataSource dataSource;

    private SectionDao sectionDao;

    private StationDao stationDao;
    private LineDao lineDao;

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
        lineDao = new LineDao(new JdbcTemplate(dataSource), dataSource);
    }

    @Test
    @DisplayName("section를 조회한다")
    void selectSection() {
        // given
        Station storedStation = new Station(1L, "서울대입구역");
        Station storedStation2 = new Station(2L, "상도역");
        Section storedSection = new Section(1L, storedStation, storedStation2, new Distance(10));
        // when
        Section selectSection = sectionDao.selectSection(storedSection.getId())
            .orElseThrow(() -> new SubwayException(NOT_FOUND_SECTION));
        // then
        assertThat(storedSection.getId()).isEqualTo(selectSection.getId());
    }

    @Test
    @DisplayName("section을 생성한다")
    void createSection() {
        // given
        Station station3 = new Station("신림역");
        Station station4 = new Station("수원역");
        Distance distance = new Distance(10);
        Station insertStation3 = stationDao.insert(station3);
        Station insertStation4 = stationDao.insert(station4);

        // when
        Section insertSection = sectionDao.insert(
            new Section(insertStation3, insertStation4, distance), 1L);
        // then
        assertAll(
            () -> assertThat(insertSection).extracting("upStation").isEqualTo(insertStation3),
            () -> assertThat(insertSection).extracting("downStation").isEqualTo(insertStation4),
            () -> assertThat(insertSection).extracting("distance").isEqualTo(distance.getDistance())
        );
    }

    @Test
    @DisplayName("sections들을 생성한다")
    void createSections() {
        // given
        Station station3 = new Station("신림역");
        Station station4 = new Station("수원역");
        Station station5 = new Station("수원시청역");
        Station insertStation3 = stationDao.insert(station3);
        Station insertStation4 = stationDao.insert(station4);
        Station insertStation5 = stationDao.insert(station5);
        List<Section> sections = List.of(
            new Section(insertStation3, insertStation4, new Distance(10)),
            new Section(insertStation4, insertStation5, new Distance(10))
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
        Station station3 = new Station("신림역");
        Station station4 = new Station("수원역");
        Station insertStation3 = stationDao.insert(station3);
        Station insertStation4 = stationDao.insert(station4);
        Section insertSection = sectionDao.insert(
            new Section(insertStation3, insertStation4, new Distance(10)), 1L);

        // when
        sectionDao.deleteSections(List.of(insertSection));
        // then
        Optional<Section> section = sectionDao.selectSection(insertSection.getId());
        assertThat(section).isEmpty();
    }

    @Test
    @DisplayName("모든 section을 조회한다")
    void findAllSection(){
        // given
        int beforeSize = sectionDao.findAll().orElseThrow().size();

        Station station1 = new Station("수원역");
        Station station2 = new Station("신촌역");
        Station station3 = new Station("망원역");

        Station insertStation = stationDao.insert(station1);
        Station insertStation2 = stationDao.insert(station2);
        Station insertStation3 = stationDao.insert(station3);
        
        Section section = new Section(insertStation, insertStation2, new Distance(10));
        Section section2= new Section(insertStation2, insertStation3, new Distance(20));

        List<Section> sections = List.of(section, section2);
        Line line = new Line("3호선", "yellow");
        Line insertLine = lineDao.insert(line);

        // when
        sectionDao.insertSections(List.of(section, section2), insertLine.getId());
        List<Section> allSections = sectionDao.findAll().orElseThrow();
        // then
        assertThat(allSections.size()).isEqualTo(beforeSize + sections.size());
    }
}
