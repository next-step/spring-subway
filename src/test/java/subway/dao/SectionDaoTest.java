package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import subway.DomainFixture;
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
            Line line = lineDao.insert(new Line("line", "red"));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section section = DomainFixture.Section.buildWithStations(line, upStation, downStation);

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
            Line line = lineDao.insert(new Line("line", "red"));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station middleStation = stationDao.insert(new Station("middleStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section upSection = DomainFixture.Section.buildWithStations(line, upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(line, middleStation, downStation);

            upSection = sectionDao.insert(upSection);
            downSection = sectionDao.insert(downSection);

            upSection.connectDownSection(downSection);

            // when
            List<Section> result = sectionDao.findAllByLineId(line.getId());

            // then
            assertThat(result).containsAll(List.of(upSection, downSection));
        }
    }

    @Nested
    @DisplayName("deleteByLineIdAndDownStationId 메소드는")
    class DeleteByLineIdAndDownStationId_Method {

        @Test
        @DisplayName("lineId와 stationId에 일치하는 Section을 삭제한다")
        void Delete_Section_Equals_LineId_And_StationId() {
            // given
            Line line = lineDao.insert(new Line("line", "red"));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station middleStation = stationDao.insert(new Station("middleStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section upSection = DomainFixture.Section.buildWithStations(line, upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(line, middleStation, downStation);

            upSection = sectionDao.insert(upSection);
            downSection = sectionDao.insert(downSection);

            upSection.connectDownSection(downSection);

            // when
            sectionDao.deleteByLineIdAndDownStationId(line.getId(), middleStation.getId());
            List<Section> sections = sectionDao.findAllByLineId(line.getId());

            // then
            assertThat(sections).hasSize(1).doesNotContain(downSection);
        }

    }
}
