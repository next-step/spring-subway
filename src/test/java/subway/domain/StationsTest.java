package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StationsTest {

    @DisplayName("Stations 를 생성한다.")
    @Test
    void createStationsTest() {
        assertDoesNotThrow(() -> new Stations(List.of(new StationPair(new Station(1L, "범계"), new Station(2L, "장지")))));
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
