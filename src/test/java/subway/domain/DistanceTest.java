package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.IncorrectRequestException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DistanceTest {

    @DisplayName("거리가 0 이하인 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void notPositiveDistance(int distance) {
        assertThrows(IncorrectRequestException.class, () -> new Distance(distance));
    }
}
