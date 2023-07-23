package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalStationsException;

@DisplayName("StationPair 테스트")
class StationPairTest {

    @Test
    @DisplayName("상핵역과 하행역을 포함한 StationPair 를 생성한다.")
    void createStationPairTest() {
        // given
        Station upStation = new Station(1L, "잠실");
        Station downStation = new Station(2L, "잠실나루");

        // when
        StationPair stationPair = new StationPair(upStation, downStation);

        // then
        assertThat(stationPair.matchUpStation(upStation)).isTrue();
        assertThat(stationPair.matchDownStation(downStation)).isTrue();
    }

    @Test
    @DisplayName("상행역과 하행역이 동일하면 예외를 던진다.")
    void createStationPairSameUpAndDownExceptionTest() {
        // given
        Station station = new Station(1L, "잠실");

        // when & then
        assertThatThrownBy(() -> new StationPair(station, station))
            .hasMessage("상행역과 하행역은 동일할 수 없습니다.")
            .isInstanceOf(IllegalStationsException.class);
    }
}