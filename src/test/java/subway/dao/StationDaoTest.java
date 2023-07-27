package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import subway.domain.Station;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(StationDao.class)
class StationDaoTest {

    @Autowired
    StationDao stationDao;

    @Test
    @DisplayName("존재하는 역을 모두 가져온다.")
    void findAll() {
        /* given */


        /* when */
        final List<Station> stations = stationDao.findAll();

        /* then */
        assertThat(stations).hasSize(40);
    }
}
