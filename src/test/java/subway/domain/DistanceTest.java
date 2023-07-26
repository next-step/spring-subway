package subway.domain;

import org.junit.jupiter.api.Test;
import subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class DistanceTest {
    @Test
    void distanceEqualToZero() {
        assertThatCode(() -> new Distance(0))
                .isInstanceOf(SubwayException.class)
                .hasMessage("구간의 거리는 0보다 커야 합니다. : 0");
    }

    @Test
    void distanceLessThanZero() {
        assertThatCode(() -> new Distance(-1))
                .isInstanceOf(SubwayException.class)
                .hasMessage("구간의 거리는 0보다 커야 합니다. : -1");
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
                .hasMessage("새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다. 새 구간 거리 : 10");
    }

    @Test
    void subtractIfGreaterFails() {
        // given
        Distance distance1 = new Distance(10);
        Distance distance2 = new Distance(11);

        // when, then
        assertThatCode(() -> distance1.subtract(distance2))
                .isInstanceOf(SubwayException.class)
                .hasMessage("새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다. 새 구간 거리 : 11");
    }
}