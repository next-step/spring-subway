package subway.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.InvalidRequestException;

class NameTest {

    @DisplayName("지하철 역명으로 사용할 이름 생성")
    @ParameterizedTest
    @ValueSource(strings = {"일", "일이", "일이삼"})
    void createStationName(String name) {
        assertDoesNotThrow(() -> new StationName(name));
    }

    @DisplayName("지하철 역명이 길이 제한을 초과하는 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 256})
    void outOfRangeStationNameLength(int length) {
        // given
        final String name = "가".repeat(length);

        // when & then
        assertThrows(InvalidRequestException.class, () -> new StationName(name));
    }

    @DisplayName("노선명으로 사용할 이름 생성")
    @ParameterizedTest
    @ValueSource(strings = {"일", "일이", "일이삼"})
    void createLineName(String name) {
        assertDoesNotThrow(() -> new LineName(name));
    }

    @DisplayName("노선명이 길이 제한을 초과하는 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 256})
    void outOfRangeLineNameLength(int length) {
        // given
        final String name = "가".repeat(length);

        // when & then
        assertThrows(InvalidRequestException.class, () -> new LineName(name));
    }
}
