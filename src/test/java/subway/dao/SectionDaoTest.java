package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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

    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void beforeEach() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
    }

    @Nested
    @DisplayName("insert 메소드는")
    class Insert_Section {

        @Test
        @DisplayName("Section을 받아 아이디를 생성하고 저장한다.")
        void Insert_Section_And_Return_Section() {
            // given
            Line line = lineDao.insert(new Line("line", "red"));

            Section section = DomainFixture.Section.buildWithStations(station1, station2);

            // when
            Section result = sectionDao.insert(section, line.getId());

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

            stationDao.insert(station1);
            stationDao.insert(station2);
            stationDao.insert(station3);

            Section section1 = DomainFixture.Section.buildWithStations(station1, station2);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3);

            section1 = sectionDao.insert(section1, line.getId());
            section2 = sectionDao.insert(section2, line.getId());

            // when
            List<Section> result = sectionDao.findAllByLineId(line.getId());

            // then
            assertThat(result).containsAll(List.of(section1, section2));
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

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            upSection = sectionDao.insert(upSection, line.getId());
            downSection = sectionDao.insert(downSection, line.getId());

            upSection.connectDownSection(downSection);

            // when
            sectionDao.deleteByLineIdAndDownStationId(line.getId(), downStation.getId());
            List<Section> sections = sectionDao.findAllByLineId(line.getId());

            // then
            assertThat(sections).hasSize(1).doesNotContain(downSection);
        }

    }

    @Nested
    @DisplayName("update 메소드는")
    class Update_Method {

        @Test
        @DisplayName("SECTIONS.id에 해당하는 Section을 업데이트 한다")
        void Update_Section_Equals_Sections_Id() {
            // given
            Line line = lineDao.insert(new Line("line", "red"));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));
            Station updateStation = stationDao.insert(new Station("updateStationName"));

            Section section = DomainFixture.Section.buildWithStations(upStation, downStation);
            section = sectionDao.insert(section, line.getId());

            Section updatedSection = DomainFixture.Section.buildWithSectionAndStation(section, updateStation);

            // when
            sectionDao.update(updatedSection);
            Section result = sectionDao.findAllByLineId(line.getId()).get(0);

            // then
            assertThat(result).isEqualTo(updatedSection);
        }
    }
}
