package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import subway.domain.Station;
import subway.exception.SubwayDataAccessException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Import(StationDao.class)
class StationDaoTest extends DaoTest {

    @Autowired
    StationDao stationDao;

    @Test
    @DisplayName("존재하는 역을 모두 가져온다.")
    void findAll() {
        /* given */


        /* when */
        final List<Station> stations = stationDao.findAll();

        /* then */
        assertThat(stations).hasSize(10);
    }

    @Test
    @DisplayName("이미 존재하는 역 이름을 추가할 경우 SubwayDataAccessException을 던진다.")
    void insertFailWithDuplicateName() {
        /* given */
        final Station station = new Station("잠실");

        /* when & then */
        assertThatThrownBy(() -> stationDao.insert(station))
                .isExactlyInstanceOf(SubwayDataAccessException.class)
                .hasMessage("이미 존재하는 역 이름입니다. 입력한 이름: 잠실");
    }

    @Test
    @DisplayName("존재하지 않는 역을 수정할 경우 SubwayDataAccessException을 던진다.")
    void updateFailWithDoesNotExistStation() {
        /* given */
        final Station doesNotExistStation = new Station(123L, "으악");

        /* when & then */
        assertThatThrownBy(() -> stationDao.update(doesNotExistStation))
                .isExactlyInstanceOf(SubwayDataAccessException.class)
                .hasMessage("역이 존재하지 않습니다. 입력한 식별자: 123");
    }

    @Test
    @DisplayName("존재하지 않는 역을 삭제할 경우 SubwayDataAccessException을 던진다.")
    void deleteFailWithDoesNotExistStation() {
        /* given */
        final Long doesNotExistStationId = 123L;

        /* when & then */
        assertThatThrownBy(() -> stationDao.deleteById(doesNotExistStationId))
                .isExactlyInstanceOf(SubwayDataAccessException.class)
                .hasMessage("역이 존재하지 않습니다. 입력한 식별자: 123");
    }
}
