package subway.dao;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
            Station upStation = stationDao.insert(new Station("upStationName"));
            Station middleStation = stationDao.insert(new Station("middleStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
            Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

            Line line = lineDao.insert(new Line("line", "red", new ArrayList<>(List.of(upSection, downSection))));

            sectionDao.insert(line.getId(), upSection);
            sectionDao.insert(line.getId(), downSection);

            // when
            Line result = lineDao.findById(line.getId()).get();

            // then
            assertThat(result.getSections()).containsAll(line.getSections());
        }

        @Test
        @DisplayName("어떠한 값도 찾을 수 없다면, Optional.empty를 반환한다.")
        void Return_Empty_If_Cannot_Find_Any_Value() {
            // given
            long nonPersistId = 1000L;

            // when
            Optional<Line> result = lineDao.findById(nonPersistId);

            // then
            assertThat(result).isEmpty();
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

        @Test
        @DisplayName("어떠한 값도 찾을 수 없으면 empty list 를 반환한다.")
        void Return_Empty_List_If_Cannot_Find_Any_Lines() {
            // when
            List<Line> result = lineDao.findAll();

            // then
            assertThat(result).isEmpty();
        }

    }

    @Nested
    @DisplayName("update 메소드는")
    class Update_Method {

        @Test
        @DisplayName("Line의 name과 color를 변경한다.")
        void Update_Line_Color_Name() {
            // given
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            Station upStation = stationDao.insert(new Station("upStationName"));
            Station downStation = stationDao.insert(new Station("downStationName"));

            Section section = DomainFixture.Section.buildWithStations(upStation, downStation);

            section = sectionDao.insert(line.getId(), section);

            Line newLine = new Line(line.getId(), "updatedLine", "updatedColor", List.of(section));

            // when
            lineDao.update(newLine);
            Optional<Line> result = lineDao.findById(line.getId());

            // then
            assertThat(result).isNotEmpty().contains(newLine);
        }
    }

    @Nested
    @DisplayName("deleteById 메소드는")
    class DeleteById_Method {

        @Test
        @DisplayName("lineId와 일치하는 Line을 삭제한다.")
        void Delete_Line_Matched_LineId() {
            // given
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            // when
            lineDao.deleteById(line.getId());
            Optional<Line> result = lineDao.findById(line.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByName 메소드는")
    class FindByName_Method {

        @Test
        @DisplayName("name과 일치하는 line을 반환한다.")
        void Return_Line_Matched_Name() {
            // given
            String name = "line";
            Line line = lineDao.insert(new Line("line", "red", List.of()));

            // when
            Optional<Line> result = lineDao.findByName(name);

            // then
            assertThat(result).isNotEmpty().contains(line);
        }

        @Test
        @DisplayName("name과 일치하는 line을 찾을 수 없다면, Optional.empty()를 반환한다.")
        void Return_Empty_Optional_Cannot_Find_Matched_Name_Line() {
            // given
            String name = "line";

            // when
            Optional<Line> result = lineDao.findByName(name);

            // then
            assertThat(result).isEmpty();
        }
    }

}
