package subway.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class FareCalculatorTest {

    @DisplayName("10km 이하는 기본 요금을 부과한다.")
    @Test
    void calculateByDistance1() {
        int distance = 10;

        int fare = FareCalculator.calculateByDistance(distance);

        assertThat(fare).isEqualTo(1250);
    }

    @DisplayName("10km ~ 50km 요금을 계산한다. (5로 나누어 떨어지는 경우)")
    @Test
    void calculateByDistance2() {
        int distance = 50;

        int fare = FareCalculator.calculateByDistance(distance);

        assertThat(fare).isEqualTo(2050);
    }

    @DisplayName("10km ~ 50km 요금을 계산한다. (5로 나누어 떨어지지 않는 경우)")
    @Test
    void calculateByDistance3() {
        int distance = 43;

        int fare = FareCalculator.calculateByDistance(distance);

        assertThat(fare).isEqualTo(1950);
    }

    @DisplayName("50km를 초과하는 요금을 계산한다. (8로 나누어 떨어지는 경우)")
    @Test
    void calculateByDistance4() {
        int distance = 58;

        int fare = FareCalculator.calculateByDistance(distance);

        assertThat(fare).isEqualTo(2150);
    }

    @DisplayName("50km를 초과하는 요금을 계산한다. (8로 나누어 떨어지지 않는 경우)")
    @Test
    void calculateByDistance5() {
        int distance = 60;

        int fare = FareCalculator.calculateByDistance(distance);

        assertThat(fare).isEqualTo(2250);
    }
}
