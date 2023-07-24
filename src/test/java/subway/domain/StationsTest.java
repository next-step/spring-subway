package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalStationsException;

class StationsTest {

    @DisplayName("Stations 를 생성한다.")
    @Test
    void createStationsTest() {
        // given
        Station upStation = new Station(1L, "범계");
        Station downStation = new Station(2L, "장지");
        StationPair stationPair = new StationPair(upStation, downStation);

        // when & then
        assertDoesNotThrow(() -> new Stations(List.of(stationPair)));
    }

    @DisplayName("동일한 상행역이 포함되어 있으면 예외를 던진다.")
    @Test
    void createStationsSameUpStationExceptionTest() {
        // given
        Station duplicateUpStation = new Station(2L, "장지");
        Station downStation1 = new Station(1L, "범계");
        Station downStation2 = new Station(3L, "문정");

        List<StationPair> stationPairs = List.of(
            new StationPair(duplicateUpStation, downStation1),
            new StationPair(duplicateUpStation, downStation2)
        );

        // when & then
        assertThatThrownBy(() -> new Stations(stationPairs))
            .hasMessage("중복된 역은 노선에 포함될 수 없습니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @DisplayName("동일한 하행역이 포함되어 있으면 예외를 던진다.")
    @Test
    void createStationsSameDownStationExceptionTest() {
        // given
        Station upStation1 = new Station(1L, "범계");
        Station upStation2 = new Station(3L, "문정");
        Station duplicateDownStation = new Station(2L, "장지");

        List<StationPair> stationPairs = List.of(
            new StationPair(upStation1, duplicateDownStation),
            new StationPair(upStation2, duplicateDownStation)
        );

        assertThatThrownBy(() -> new Stations(stationPairs))
            .hasMessage("중복된 역은 노선에 포함될 수 없습니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @DisplayName("순환하는 역이 포함되어 있으면 예외를 던진다.")
    @Test
    void createStationsCircleStationExceptionTest() {
        // given
        Station upStation = new Station(1L, "범계");
        Station downStation = new Station(2L, "장지");
        StationPair stationPair1 = new StationPair(upStation, downStation);
        StationPair stationPair2 = new StationPair(downStation, upStation);

        // when & then
        assertThatThrownBy(() -> new Stations(List.of(stationPair1, stationPair2)))
            .hasMessage("역들이 제대로 연결되지 않았습니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @DisplayName("정상적으로 연결되지 않은 역이 존재할 경우 예외를 던진다.")
    @Test
    void createStationsUnconnectedStationExceptionTest() {
        // given
        Station upStation = new Station(1L, "범계");
        Station downStation = new Station(2L, "장지");
        Station unConnectedStation1 = new Station(3L, "잠실");
        Station unConnectedStation2 = new Station(4L, "잠실나루");

        StationPair stationPair1 = new StationPair(upStation, downStation);
        StationPair stationPair2 = new StationPair(unConnectedStation1, unConnectedStation2);

        // when & then
        assertThatThrownBy(() -> new Stations(List.of(stationPair1, stationPair2)))
            .hasMessage("역들이 제대로 연결되지 않았습니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @DisplayName("Stations 를 정렬한다.")
    @Test
    void getSortedStationsTest() {
        // given
        Station station1 = new Station(1L, "오이도");
        Station station2 = new Station(2L, "정왕");
        Station station3 = new Station(3L, "안산");
        Station station4 = new Station(4L, "한대앞");

        List<Station> sorted = List.of(station4, station1, station3, station2);
        List<StationPair> stationPairs = List.of(
            new StationPair(station3, station2),
            new StationPair(station1, station3),
            new StationPair(station4, station1));

        // when
        List<Station> stations = new Stations(stationPairs).getStations();

        // then
        assertThat(stations).isEqualTo(sorted);

    }
}
