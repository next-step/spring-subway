package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import subway.domain.Line;
import subway.domain.LineSections;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class SectionDaoTest {

    @Autowired
    SectionDao sectionDao;

    @Autowired
    StationDao stationDao;

    @Autowired
    LineDao lineDao;

    Line lineA;
    Station stationA;
    Station stationB;
    Station stationC;
    Station stationD;

    @BeforeEach
    void setUp() {
        lineA = new Line(1L, "A", "red");
        stationA = new Station(1L, "A");
        stationB = new Station(2L, "B");
        stationC = new Station(3L, "C");
        stationD = new Station(4L, "D");
    }

    @Test
    @DisplayName("노선의 모든 구간을 불러온다")
    void findAllByLineId() {
        lineDao.insert(lineA);
        stationDao.insert(stationA);
        stationDao.insert(stationB);
        stationDao.insert(stationC);
        Section section = sectionDao.save(new Section(lineA, stationA, stationB, 2));
        Section section2 = sectionDao.save(new Section(lineA, stationB, stationC, 2));

        assertThat(sectionDao.findAllByLine(lineA)).isEqualTo(new LineSections(lineA, new Sections(List.of(section, section2))));
    }

    @Test
    @DisplayName("모든 노선 구간 구간을 불러온다")
    void findAll() {

        Line lineB = new Line(2L, "B", "blue");
        lineDao.insert(lineA);
        lineDao.insert(lineB);
        stationDao.insert(stationA);
        stationDao.insert(stationB);
        stationDao.insert(stationC);
        stationDao.insert(stationD);
        Section section = sectionDao.save(new Section(lineA, stationA, stationB, 2));
        Section section2 = sectionDao.save(new Section(lineB, stationB, stationC, 2));
        Section section3 = sectionDao.save(new Section(lineB, stationC, stationD, 3));

        List<LineSections> lineSections = sectionDao.findAll();
        LineSections lineSectionsA = new LineSections(lineA, section);
        LineSections lineSectionsB = new LineSections(lineB, new Sections(List.of(section2, section3)));

        assertThat(lineSections).contains(lineSectionsA, lineSectionsB);
    }


}
