package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DistanceTest {

    @ParameterizedTest()
    @ValueSource(ints = {-1, 0})
    @DisplayName("거리 값이 0 이하인 경우 예외 발생")
    void exceptionTest(int distance) {
        Assertions.assertThatThrownBy(() -> new Distance(distance))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
