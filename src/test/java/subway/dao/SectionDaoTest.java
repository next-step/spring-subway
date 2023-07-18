package subway.dao;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@DisplayName("SectionDao 클래스")
@JdbcTest
@ContextConfiguration(classes = {SectionDao.class, StationDao.class, LineDao.class})
class SectionDaoTest {

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private LineDao lineDao;

    @Nested
    @DisplayName("insert 메소드는")
    class Insert_Section {

        @Test
        @DisplayName("Section을 받아 아이디를 생성하고 저장한다.")
        void Insert_Section_And_Return_Section() {
            // given
            Line line = new Line("line", "red");
            line = lineDao.insert(line);

            String upStationName = "upStation";
            Station upStation = stationDao.insert(new Station(upStationName));

            String downStationName = "downStation";
            Station downStation = stationDao.insert(new Station(downStationName));

            Integer distance = 10;

            Section section = Section.builder()
                    .line(line)
                    .upStation(upStation)
                    .downStation(downStation)
                    .distance(distance)
                    .build();

            // when
            Section result = sectionDao.insert(section);

            // then
            assertThat(result.getId()).isNotNull();
        }
    }
}
