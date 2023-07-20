package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionTest {

    @DisplayName("구간 상행역과 하행역은 다른 역이어야 한다.")
    @Test
    void validateDifferent() {
        Station station = new Station(4L, "잠실역");

        assertThrows(IllegalArgumentException.class,
                () -> new Section(station, station, 10));
    }
}
