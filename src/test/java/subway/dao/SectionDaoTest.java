package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;

@JdbcTest(includeFilters = {
    @Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SectionDao.class, StationDao.class, LineDao.class}
    )
})
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
        lineA = new Line("A", "red");
        stationA = new Station("A");
        stationB = new Station("B");
        stationC = new Station("C");
        stationD = new Station("D");
    }

    @Test
    @DisplayName("노선의 모든 구간을 불러온다")
    void findAllByLineId() {
        Line persistLineA = lineDao.insert(lineA);
        Station persistStationA = stationDao.insert(stationA);
        Station persistStationB = stationDao.insert(stationB);
        Station persistStationC = stationDao.insert(stationC);
        Section section = sectionDao.save(
            new Section(persistLineA, persistStationA, persistStationB, 2));
        Section section2 = sectionDao.save(
            new Section(persistLineA, persistStationB, persistStationC, 2));

        assertThat(sectionDao.findAllByLine(persistLineA))
            .isEqualTo(new Sections(List.of(section, section2)));
    }

    @Test
    @DisplayName("구간을 수정한다.")
    void update() {
        Line persistLineA = lineDao.insert(lineA);
        Station persistStationA = stationDao.insert(stationA);
        Station persistStationB = stationDao.insert(stationB);
        Station persistStationC = stationDao.insert(stationC);
        Section section = sectionDao.save(
            new Section(persistLineA, persistStationA, persistStationB, 2));
        Section sectionToUpdate = new Section(section.getId(), persistLineA, persistStationB, persistStationC, 5);

        sectionDao.update(sectionToUpdate);

        Section foundSection = sectionDao.findById(section.getId()).get();
        assertThat(foundSection).isEqualTo(sectionToUpdate);
        assertThat(doSectionsHaveSameFields(foundSection, sectionToUpdate)).isTrue();
    }

    private boolean doSectionsHaveSameFields(Section section, Section other) {
        return Objects.equals(section.getLine(), other.getLine())
            && Objects.equals(section.getUpStation(), other.getUpStation())
            && Objects.equals(section.getDownStation(), other.getDownStation())
            && section.getDistance() == other.getDistance();
    }
}
