package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import subway.dao.mapper.StationRowMapper;
import subway.domain.Station;

@DisplayName("StationDao 클래스")
@JdbcTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = {StationDao.class, StationRowMapper.class})
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @Nested
    @DisplayName("findAll 메소드는")
    class FindAll_Method {

        @Test
        @DisplayName("생성된 station들을 모두 반환한다")
        void Create_Station() {
            // given
            Station station1 = stationDao.insert(new Station("station1"));
            Station station2 = stationDao.insert(new Station("station2"));

            // when
            List<Station> result = stationDao.findAll();

            // then
            assertThat(result).containsAll(List.of(station1, station2));
        }

        @Test
        @DisplayName("생성된 station이 없다면, empty list를 반환한다")
        void Return_Empty_List_If_No_Exist_Station() {
            // when
            List<Station> result = stationDao.findAll();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("update 메소드는")
    class Update_Method {

        @Test
        @DisplayName("station의 이름을 변경한다.")
        void Change_Station_Name() {
            // given
            Station station = stationDao.insert(new Station("before"));
            Station newStation = new Station(station.getId(), "after");

            // when
            stationDao.update(newStation);
            Optional<Station> result = stationDao.findById(station.getId());

            // then
            assertThat(result).isNotEmpty().contains(newStation);
        }
    }

    @Nested
    @DisplayName("deleteById 메소드는")
    class DeleteById_Method {

        @Test
        @DisplayName("id에 해당하는 station을 삭제한다.")
        void Delete_Station_Matched_Id() {
            // given
            Station station = stationDao.insert(new Station("before"));

            // when
            stationDao.deleteById(station.getId());
            Optional<Station> result = stationDao.findById(station.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByName 메소드는")
    class FindByName_Method {

        @Test
        @DisplayName("name에 해당하는 Station을 반환한다.")
        void Return_Station_Matched_Name() {
            // given
            String name = "matched";
            Station station = stationDao.insert(new Station(name));

            // when
            Optional<Station> result = stationDao.findByName(name);

            // then
            assertThat(result).isNotEmpty().contains(station);
        }

        @Test
        @DisplayName("name에 해당하는 Station이 없으면 Optional.empty를 반환한다.")
        void Return_Empty_Optional_Not_Exists_Station() {
            // given
            String name = "non exists";

            // when
            Optional<Station> result = stationDao.findByName(name);

            // then
            assertThat(result).isEmpty();
        }
    }
}
