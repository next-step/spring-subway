package subway.dao;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import subway.dao.mapper.LineRowMapper;
import subway.dao.mapper.SectionRowMapper;
import subway.dao.mapper.StationRowMapper;
import subway.domain.DomainFixture;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@DisplayName("LineDao 클래스")
@JdbcTest
@ContextConfiguration(classes = {SectionDao.class, StationDao.class, LineDao.class, LineRowMapper.class,
        SectionRowMapper.class, StationRowMapper.class})
class LineDaoTest {

    @Autowired
    private LineDao lineDao;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private StationDao stationDao;

    @Nested
    @DisplayName("findById 메소드는")
    class FindById_Method {

        @Test
        @DisplayName("lineId에 연결된 모든 Section이 포함된 Line을 반환한다.")
        void Return_Line_Include_All_Section_With_Line_Id() {
            // given
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station middleStation = stationDao.insert(new Station("middleStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            upSection = sectionDao.insert(line.getId(), upSection);
            downSection = sectionDao.insert(line.getId(), downSection);
            upSection.connectDownSection(downSection);

            // when
            Line result = lineDao.findById(line.getId()).get();

            // then
            assertThat(result.getSections()).containsAll(List.of(upSection, downSection));
        }

    }

    @Nested
    @DisplayName("findAll 메소드는")
    class FindAll_Method {

        @Test
        @DisplayName("저장되어있는 모든 Line과 각각의 Line에 저장된 Section을 반환한다.")
        void Return_All_Line_Include_Section() {
            // given
            Line line1 = lineDao.insert(new Line("line1", "red", List.of()));
            Line line2 = lineDao.insert(new Line("line2", "blue", List.of()));

            Station upStation1 = stationDao.insert(new Station("upStation1"));
            Station downStation1 = stationDao.insert(new Station("downStation1"));
            Station upStation2 = stationDao.insert(new Station("upStationName2"));
            Station downStation2 = stationDao.insert(new Station("downStationName2"));

            Section section1 = DomainFixture.Section.buildWithStations(upStation1, downStation1);
            Section section2 = DomainFixture.Section.buildWithStations(upStation2, downStation2);

            section1 = sectionDao.insert(line1.getId(), section1);
            section2 = sectionDao.insert(line2.getId(), section2);

            // when
            List<Line> result = lineDao.findAll();

            // then
            assertThat(result).containsAll(List.of(line1, line2));
            assertThat(result.get(0).getSections()).containsExactly(section1);
            assertThat(result.get(1).getSections()).containsExactly(section2);
        }

    }

}
