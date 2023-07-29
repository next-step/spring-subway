package subway.dao;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import subway.domain.Station;

@JdbcTest
@Import(StationDao.class)
class StationDaoTest {

    @Autowired
    private StationDao stationDao;

    @Test
    @DisplayName("데이터 삽입 테스트")
    void insert() {
        Station station = new Station("송내역");

        Station result = stationDao.insert(station);

        Assertions.assertThat(result).extracting("id").isEqualTo(5L);
    }

    @Test
    @DisplayName("데이터 전체 조회 테스트")
    void findAll() {
        List<Station> result = stationDao.findAll();

        Assertions.assertThat(result)
            .contains(
                new Station(1L, "부천시청역"),
                new Station(2L, "신중동역"),
                new Station(3L, "춘의역"),
                new Station(4L, "부천종합운동장역")
            );
    }

    @Test
    @DisplayName("데이터 단건 조회 테스트")
    void findById() {
        Station result = stationDao.findById(1L).get();

        Assertions.assertThat(result).isEqualTo(new Station(1L, "부천시청역"));
    }

    @Test
    @DisplayName("데이터 단건 조회 예외 테스트")
    void findByIdException() {
        Assertions.assertThatThrownBy(
                () -> stationDao.findById(100L)
                    .orElseThrow(() -> new IllegalArgumentException("없는 데이터"))
            ).isInstanceOf(IllegalArgumentException.class)
            .hasMessage("없는 데이터");
    }

    @Test
    @DisplayName("데이터 수정 테스트")
    void update() {
        stationDao.update(new Station(1L, "상동역"));

        Assertions.assertThat(stationDao.findById(1L).get())
            .isEqualTo(new Station(1L, "상동역"));
    }

    @Test
    @DisplayName("데이터 삭제 테스트")
    void deleteById() {
        stationDao.deleteById(4L);

        Assertions.assertThat(stationDao.findById(4L).orElse(null))
            .isNull();
    }
}