package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.ErrorType;
import subway.exception.ServiceException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


class DistanceTest {

    @DisplayName("최소 거리 값은 0이다.")
    @ParameterizedTest(name = "거리 값 {0}은 생성 불가능 하다.")
    @ValueSource(ints = {-100, -100, -10, -5, -1})
    void validateDistance(int distance) {
        // then
        assertThatThrownBy(() -> Distance.of(distance))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ErrorType.INVALID_DISTANCE.getMessage());
    }

}
