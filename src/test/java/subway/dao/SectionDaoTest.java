package subway.dao;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import subway.domain.Line;
import subway.domain.LineSections;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@SpringBootTest
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

        Assertions.assertThat(sectionDao.findAllByLineId(lineA.getId())).isEqualTo(new LineSections(lineA, new Sections(List.of(section, section2))));
    }
}