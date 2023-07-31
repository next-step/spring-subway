package subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.Station;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("StationDao 단위 테스트")
class StationDaoTest extends DaoTest {
    @Test
    @DisplayName("Station id로 Station을 조회할 수 있다.")
    void findStationById() {
        // when
        Station station = stationDao.findById(1L).get();

        // then
        assertThat(station.getId()).isEqualTo(1L);
        assertThat(station.getName()).isEqualTo("서울대입구역");
    }

    @Test
    @DisplayName("Station id가 존재하지 않는 경우 Empty Optional을 반환한다.")
    void findByNonExistId() {
        assertThat(sectionDao.findById(4L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Station name이 중복되지 않는 Station을 삽입할 수 있다.")
    void insertNoDuplicateName() {
        // given
        Station station = new Station("잠실새내역");

        // when
        Station persistentStation = stationDao.insert(station);

        // then
        assertThat(persistentStation.getId()).isEqualTo(4L);
        assertThat(persistentStation.getName()).isEqualTo("잠실새내역");
    }

    @Test
    @DisplayName("Station name이 중복되는 Station은 삽입할 수 없다.")
    void insertDuplicateName() {
        // given
        Station station = new Station("잠실역");

        // when, then
        assertThatCode(() -> stationDao.insert(station))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("Station id를 파라미터로 Station을 수정할 수 있다.")
    void updateStation() {
        // given
        Station station = new Station(1L, "강남역");

        // when
        stationDao.update(station);
        Station persistentStation = stationDao.findById(1L).get();

        // then
        assertThat(persistentStation.getId()).isEqualTo(1L);
        assertThat(persistentStation.getName()).isEqualTo("강남역");
    }

    @Test
    @DisplayName("이미 존재하는 Station name으로 Station을 수정할 수 없다.")
    void updateDuplicateNameStation() {
        // given
        Station otherStation = new Station("강남역");
        stationDao.insert(otherStation);
        Station station = new Station(1L, "강남역");

        // when, then
        assertThatCode(() -> stationDao.update(station))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.STATION_NAME_DUPLICATE.getMessage() + "강남역");
    }

    @Test
    @DisplayName("Station id로 Station을 삭제할 수 있다.")
    void deleteStation() {
        // given
        Station station = stationDao.insert(new Station("몽촌토성역"));
        // when
        stationDao.deleteById(station.getId());

        // then
        assertThat(stationDao.findById(station.getId()).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Station id를 참조하는 경우 Station을 삭제할 수 없다.")
    void deleteReferencedStation() {
        assertThatCode(() -> stationDao.deleteById(1L))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.STATION_REFERENCED.getMessage() + "1");
    }

    @Test
    @DisplayName("존재하는 모든 Station을 연관관계 없이 조회할 수 있다")
    void findAllStations() {
        // when
        List<Station> all = stationDao.findAll();

        // then
        assertThat(all).contains(
                new Station(1L, "서울대입구역"),
                new Station(2L, "잠실역"),
                new Station(3L, "상도역")
        );
    }
}
