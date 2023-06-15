package subway.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.domain.Station;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
class StationDaoTest {

    private final static Station STATION_1 = new Station("정자역");
    private final static Station STATION_2 = new Station("수서역");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        stationDao = new StationDao(jdbcTemplate, dataSource);
    }

    @DisplayName("Station 테이블에 엔티티를 삽입한다")
    @Test
    void insert() {
        // when
        Station insertedStation = stationDao.insert(STATION_1);

        // then
        assertThat(insertedStation.getId()).isNotNull();
        assertThat(insertedStation)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(STATION_1);
    }

    @DisplayName("Station 테이블에서 모든 엔티티를 조회한다")
    @Test
    void findAll() {
        // given
        stationDao.insert(STATION_1);
        stationDao.insert(STATION_2);

        // when
        List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations)
                .flatExtracting(Station::getId).doesNotContainNull();
        assertThat(stations)
                .flatExtracting(Station::getName).containsOnly(STATION_1.getName(), STATION_2.getName());
    }

    @DisplayName("Station id로 Station 테이블의 엔티티를 조회한다")
    @Test
    public void findById() {
        // given
        Station insertedStation = stationDao.insert(STATION_1);

        // when
        Optional<Station> foundOptionalStation = stationDao.findById(insertedStation.getId());

        // then
        Station foundStation = assertDoesNotThrow(() -> foundOptionalStation.get());
        assertThat(foundStation)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(STATION_1);
    }

    @DisplayName("Station 테이블의 엔티티를 수정한다")
    @Test
    public void update() {
        // given
        Station insertedStation = stationDao.insert(STATION_1);

        // when
        stationDao.update(new Station(insertedStation.getId(), STATION_2.getName()));

        // then
        Station updatedStation = stationDao.findById(insertedStation.getId()).get();
        assertThat(updatedStation)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(STATION_2);
    }

    @DisplayName("Station 테이블의 엔티티를 삭제한다")
    @Test
    void deleteById() {
        // given
        Station insertedStation = stationDao.insert(STATION_1);

        // when
        stationDao.deleteById(insertedStation.getId());

        // then
        assertThat(stationDao.findById(insertedStation.getId())).isEmpty();
    }
}
