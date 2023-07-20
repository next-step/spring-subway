package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StationsTest {

    @DisplayName("Stations 를 생성한다.")
    @Test
    void createStations() {
        assertDoesNotThrow(() -> new Stations(List.of()));
    }
}
