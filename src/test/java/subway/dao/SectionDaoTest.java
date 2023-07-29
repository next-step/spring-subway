package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import subway.dao.mapper.LineRowMapper;
import subway.dao.mapper.SectionRowMapper;
import subway.dao.mapper.StationRowMapper;
import subway.domain.DomainFixture;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;

@DisplayName("SectionDao 클래스")
@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
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
            Station upStation = stationDao.insert(new Station("upStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section section = DomainFixture.Section.buildWithStations(upStation, downStation);
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            // when
            Section result = sectionDao.insert(line.getId(), section);

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
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station middleStation = stationDao.insert(new Station("middleStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            upSection = sectionDao.insert(line.getId(), upSection);
            downSection = sectionDao.insert(line.getId(), downSection);

            // when
            List<Section> result = sectionDao.findAllByLineId(line.getId());

            // then
            assertThat(result).containsAll(List.of(upSection, downSection));
        }

        @Test
        @DisplayName("어떠한 값도 찾을 수 없다면, Empty List를 반환한다.")
        void Return_Empty_List_Cannot_Find_Any_Sections() {
            // given
            long lineId = 1L;

            // when
            List<Section> result = sectionDao.findAllByLineId(lineId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class Update_Method {

        @Test
        @DisplayName("SECTIONS.id에 해당하는 Section을 업데이트 한다")
        void Update_Section_Equals_Sections_Id() {
            // given
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));
            Station updateStation = stationDao.insert(new Station("updateStationName"));

            Section section = DomainFixture.Section.buildWithStations(upStation, downStation);
            section = sectionDao.insert(line.getId(), section);

            Section updatedSection = DomainFixture.Section.buildWithSectionAndStation(section, updateStation);

            // when
            sectionDao.update(updatedSection);
            Section result = sectionDao.findAllByLineId(line.getId()).get(0);

            // then
            assertThat(result).isEqualTo(updatedSection);
        }
    }

    @Nested
    @DisplayName("deleteBySectionId 메소드는")
    class DeleteBySectionId_Method {

        @Test
        @DisplayName("SECTIONS.id에 해당하는 Section을 삭제한다")
        void Delete_Section_Equals_Sections_Id() {
            // given
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section section = DomainFixture.Section.buildWithStations(upStation, downStation);
            section = sectionDao.insert(line.getId(), section);

            // when
            sectionDao.deleteBySectionId(section.getId());
            List<Section> result = sectionDao.findAllByLineId(line.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll 메소드는")
    class FindAll_Method {

        @Test
        @DisplayName("존재하는 모든 section을 반환한다.")
        void Return_All_Exist_Sections() {
            // given
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            List<Station> persistStationList = List.of(stationDao.insert(new Station("upStationName1")),
                    stationDao.insert(new Station("downStationName1")),
                    stationDao.insert(new Station("upStationName2")),
                    stationDao.insert(new Station("downStationName2")));

            Section expectedSection1 = sectionDao.insert(line.getId(),
                    DomainFixture.Section.buildWithStations(persistStationList.get(0), persistStationList.get(1)));

            Section expectedSection2 = sectionDao.insert(line.getId(),
                    DomainFixture.Section.buildWithStations(persistStationList.get(2), persistStationList.get(3)));

            // when
            List<Section> result = sectionDao.findAll();

            // then
            assertThat(result).hasSize(2)
                    .contains(expectedSection1, expectedSection2);
        }

        @Test
        @DisplayName("어떠한 section도 없다면, 빈 List를 반환한다.")
        void Return_Empty_List_When_No_Exist_Sections() {
            // when
            List<Section> result = sectionDao.findAll();

            // then
            assertThat(result).isEmpty();
        }
    }
}
