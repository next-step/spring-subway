package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Station;

@DisplayName("역 Dao 테스트")
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class StationDaoTest {

    private final StationDao stationDao;

    private Station station1;
    private Station station2;

    @Autowired
    public StationDaoTest(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    @BeforeEach
    void setUp() {
        station1 = new Station("신당");
        station2 = new Station("동대문역사문화공원");
    }

    @Test
    @DisplayName("삽입에 성공하면 식별자가 포함된 Station 을 반환한다.")
    void insert() {
        // when
        Station result = stationDao.insert(station1);

        // then
        assertThat(result.getId()).isPositive();
        assertThat(result.getName()).isEqualTo(station1.getName());
    }

    @Test
    @DisplayName("모든 역을 반환한다.")
    void findAll() {
        // given
        Station result1 = stationDao.insert(station1);
        Station result2 = stationDao.insert(station2);

        // given
        List<Station> result = stationDao.findAll();

        // then
        assertThat(result)
            .hasSize(2)
            .contains(result1, result2);
    }

    @Test
    @DisplayName("식별자로 역을 조회한다.")
    void findById() {
        // given
        Long invalidStationId = 12L;
        Station station = stationDao.insert(station1);

        // when
        Optional<Station> emptyResult = stationDao.findById(invalidStationId);
        Optional<Station> result = stationDao.findById(station.getId());

        // then
        assertThat(emptyResult).isPresent();
        assertThat(result)
            .isPresent()
            .hasValue(station);
    }

    @Test
    @DisplayName("식별자와 일치하는 역 정보를 갱신한다.")
    void update() {
        // given
        Station station = stationDao.insert(station1);
        Station update = new Station(station.getId(), "잠실");

        // when
        stationDao.update(update);

        // then
        Optional<Station> result = stationDao.findById(station.getId());
        assertThat(result)
            .isPresent()
            .hasValue(update);
    }

    @Test
    @DisplayName("식별자와 일치하는 역 정보를 제거한다.")
    void deleteById() {
        // given
        Station station = stationDao.insert(station1);

        // when
        stationDao.deleteById(station.getId());

        // then
        Optional<Station> result = stationDao.findById(station.getId());
        assertThat(result).isNotPresent();
    }
}