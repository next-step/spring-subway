package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StationTest {

    @Test
    @DisplayName("역을 정상적으로 생성한다.")
    void create() {
        /* given */
        final StationName stationName = new StationName("잠실");

        /* when & then */
        assertDoesNotThrow(() -> new Station(stationName));
    }

    @Test
    @DisplayName("서로 다른 역의 역 이름이 같다면 서로 같다.")
    void sameIfStationIsName() {
        /* given */
        final Station station1 = new Station("잠실");
        final Station station2 = new Station("잠실");

        /* when */
        final Set<Station> stations = new HashSet<>();
        stations.add(station1);
        stations.add(station2);

        /* then */
        assertThat(station1).isEqualTo(station2);
        assertThat(stations).hasSize(1);
    }
}
