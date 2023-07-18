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
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@DisplayName("SectionDao 클래스")
@JdbcTest
@ContextConfiguration(classes = {SectionDao.class, StationDao.class, LineDao.class, LineRowMapper.class,
        SectionRowMapper.class, StationRowMapper.class})
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

    @Nested
    @DisplayName("findAllByLineId 메소드는")
    class FindAllByLineId_Method {

        @Test
        @DisplayName("lineId에 연결된 모든 Section들을 반환한다.")
        void Return_All_Section_Connected_With_Line_Id() {
            // given
            Line line = new Line("line", "red");
            line = lineDao.insert(line);

            String upStationName = "upStation";
            Station upStation = stationDao.insert(new Station(upStationName));

            String middleStationName = "middleStation";
            Station middleStation = stationDao.insert(new Station(middleStationName));

            String downStationName = "downStation";
            Station downStation = stationDao.insert(new Station(downStationName));

            Section upSection = Section.builder()
                    .line(line)
                    .upStation(upStation)
                    .downStation(middleStation)
                    .distance(1)
                    .build();

            Section downSection = Section.builder()
                    .line(line)
                    .upStation(middleStation)
                    .downStation(downStation)
                    .distance(2)
                    .build();

            upSection.connectDownSection(downSection);

            upSection = sectionDao.insert(upSection);
            downSection = sectionDao.insert(downSection);
            upSection.connectDownSection(downSection);

            // when
            List<Section> result = sectionDao.findAllByLineId(line.getId());

            // then
            assertThat(result).containsAll(List.of(upSection, downSection));
        }

    }
}
