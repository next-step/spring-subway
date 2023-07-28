package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("Distance 도메인 단위 테스트")
class DistanceTest {
    @Test
    @DisplayName("Distance 값이 0이면 예외를 발생시킨다.")
    void distanceEqualToZero() {
        assertThatCode(() -> new Distance(0))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.DISTANCE_VALIDATE_POSITIVE.getMessage() + "0");
    }

    @Test
    @DisplayName("Distance 값이 0보다 작으면 예외를 발생시킨다.")
    void distanceLessThanZero() {
        assertThatCode(() -> new Distance(-1))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.DISTANCE_VALIDATE_POSITIVE.getMessage() + "-1");
    }

    @Test
    @DisplayName("subtract 연산은 현재 Distance에서 다른 Distance 값을 뺀 값을 가진 Distance를 반환한다.")
    void subtractSuccess() {
        // given
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(9);

        // when, then
        assertThat(distance1.subtract(distance2)).isEqualTo(new Distance(1));
    }

    @Test
    @DisplayName("subtract 시 두 Distance가 같으면 예외를 발생시킨다.")
    void subtractIfEqualFails() {
        // given
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(10);

        // when, then
        assertThatCode(() -> distance1.subtract(distance2))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.DISTANCE_VALIDATE_SUBTRACT.getMessage() + "10");
    }

    @Test
    @DisplayName("subtract 시 뺄 Distance 값이 더 크면 예외를 발생시킨다.")
    void subtractIfGreaterFails() {
        // given
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(11);

        // when, then
        assertThatCode(() -> distance1.subtract(distance2))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.DISTANCE_VALIDATE_SUBTRACT.getMessage() + "11");
    }
}