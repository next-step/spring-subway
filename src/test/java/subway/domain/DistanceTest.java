package subway.domain;

import org.junit.jupiter.api.Test;
import subway.exception.ErrorCode;
import subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DistanceTest {
    @Test
    void distanceEqualToZero() {
        assertThatCode(() -> new Distance(0))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.DISTANCE_VALIDATE_POSITIVE.getMessage() + "0");
    }

    @Test
    void distanceLessThanZero() {
        assertThatCode(() -> new Distance(-1))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ErrorCode.DISTANCE_VALIDATE_POSITIVE.getMessage() + "-1");
    }

    @Test
    void subtractSuccess() {
        // given
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(9);

        // when, then
        assertThat(distance1.subtract(distance2)).isEqualTo(new Distance(1));
    }

    @Test
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