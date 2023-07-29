package subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.exception.ErrorCode.NOT_FOUND_STATION;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Station;
import subway.exception.SubwayException;

@JdbcTest
@Sql({"classpath:schema.sql", "classpath:test-data.sql"})
@Import(StationDao.class)
public class StationDaoTest {

    @Autowired
    private StationDao stationDao;


    @Test
    @DisplayName("station를 조회한다.")
    void stationCreateTest() {
        // given
        Station station = new Station(1L, "서울대입구역");
        // when
        Station selectStations = stationDao.findById(station.getId())
            .orElseThrow(() -> new SubwayException(NOT_FOUND_STATION));
        // then
        assertThat(station).isEqualTo(selectStations);
    }

    @Test
    @DisplayName("station을 생성한다")
    void createStation() {
        // given
        Station station = new Station("구디역");
        // when
        Station insertStation = stationDao.insert(station);
        // then
        assertThat(insertStation.getName()).isEqualTo(station.getName());
    }


    @Test
    @DisplayName("station을 삭제한다.")
    void deleteSection() {
        // given
        Station station = new Station("구디역");
        Station insertStation = stationDao.insert(station);
        // when
        stationDao.deleteById(insertStation.getId());
        // then
        Optional<Station> deleteStation = stationDao.findById(insertStation.getId());
        assertThat(deleteStation).isEmpty();
    }

}
