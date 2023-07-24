package subway.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @DisplayName("거리가 0 이하인 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void notPositiveDistance(int distance) {
        assertThrows(IllegalArgumentException.class, () -> new Distance(distance));
    }

    @DisplayName("Distance 간 차이를 구하는 경우")
    @ParameterizedTest
    @ValueSource(ints = {5, 3, 1})
    void subtract(int distance) {
        // given
        final Distance fixedDistance = new Distance(10);
        final Distance varDistance = new Distance(distance);
        final int expectedValue = 10 - distance;

        // when
        final Distance difference = fixedDistance.subtract(varDistance);

        // then
        assertEquals(difference.getValue(), expectedValue);
    }

    @DisplayName("Distance 간 대소 관계를 비교하는 경우")
    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15})
    void shorterOrEqualTo(int distance) {
        // given
        final Distance fixedDistance = new Distance(10);
        final Distance varDistance = new Distance(distance);
        final boolean expectedValue = 10 <= distance;

        // when & then
        assertEquals(fixedDistance.shorterOrEqualTo(varDistance), expectedValue);
    }
}
