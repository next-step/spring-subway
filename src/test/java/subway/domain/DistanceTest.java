package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.IncorrectRequestException;

import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {

    @DisplayName("거리가 0 이하인 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void notPositiveDistance(int distance) {
        assertThrows(IncorrectRequestException.class, () -> new Distance(distance));
    }

    @DisplayName("거리와 거리를 뺀다.")
    @Test
    void subtract() {
        // given
        Distance distance = new Distance(10);
        Distance subtract = new Distance(7);

        // when
        Distance actual = distance.subtract(subtract);

        // then
        assertEquals(actual, new Distance(3));
    }

    @DisplayName("거리와 거리를 뺐을 때 0 이하이면 예외를 던진다.")
    @Test
    void subtractLessThanZero() {
        // given
        Distance distance = new Distance(10);
        Distance subtract = new Distance(17);

        // when & then
        assertThrows(IncorrectRequestException.class, () -> distance.subtract(subtract));
    }

    @DisplayName("거리와 거리를 더한다.")
    @Test
    void add() {
        // given
        Distance distance = new Distance(3);
        Distance add = new Distance(7);

        // when
        Distance actual = distance.add(add);

        // then
        assertEquals(actual, new Distance(10));
    }

    @DisplayName("한 거리가 다른 거리보다 같거나 작은지 판단한다.")
    @Test
    void shorter() {
        // given
        Distance distance = new Distance(5);
        Distance longer = new Distance(7);
        Distance shorter = new Distance(3);

        // when & then
        assertTrue(distance.shorterOrEqualTo(longer));
        assertTrue(distance.shorterOrEqualTo(distance));
        assertFalse(distance.shorterOrEqualTo(shorter));
    }
}
