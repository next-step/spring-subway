package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.SubwayIllegalArgumentException;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DistanceTest {

    @Test
    @DisplayName("거리를 정상적으로 생성한다.")
    void create() {
        /* given */
        final int value = 5;

        /* when & then*/
        assertDoesNotThrow(() -> new Distance(value));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.2, 3.4})
    @DisplayName("값이 정수가 아닐 경우 SubwayIllegalException을 던진다.")
    void createFailWithNotInteger(final double value) {
        /* given */


        /* when & then */
        assertThatThrownBy(() -> new Distance(value)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("거리는 정수여야합니다. 입력값: " + value);
    }

    @ParameterizedTest
    @ValueSource(ints = {-777, -1, 0})
    @DisplayName("값이 0 이하일 경우 SubwayIllegalException을 던진다.")
    void createFailWithLessThanOrEqualZero(final int value) {
        /* given */


        /* when & then */
        assertThatThrownBy(() -> new Distance(value)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("거리는 0보다 길어야합니다. 입력값: " + value);
    }

    @Test
    @DisplayName("거리의 값이 같으면 서로 같은 객체이다.")
    void equal() {
        /* given */
        final Distance from = new Distance(777);
        final Distance to = new Distance(777);

        /* when */
        final Set<Distance> distances = new HashSet<>();
        distances.add(from);
        distances.add(to);

        /* then */
        assertThat(from).isEqualTo(to);
        assertThat(distances).hasSize(1);
    }

    @Test
    @DisplayName("거리를 더할 수 있다.")
    void add() {
        /* given */
        final Distance target = new Distance(10);
        final Distance operand = new Distance(20);

        /* when */
        final Distance result = target.add(operand);

        /* then */
        assertThat(result).isEqualTo(new Distance(30));
    }

    @Test
    @DisplayName("거리를 뺄 수 있다.")
    void subtract() {
        /* given */
        final Distance target = new Distance(10);
        final Distance operand = new Distance(7);

        /* when */
        final Distance result = target.subtract(operand);

        /* then */
        assertThat(result).isEqualTo(new Distance(3));
    }

    @Test
    @DisplayName("뺄 거리가 더 긴 경우 SubwayIllegalException을 던진다.")
    void subtractFailWithLongerOperand() {
        /* given */
        final Distance target = new Distance(10);
        final Distance operand = new Distance(777);

        /* when & then */
        assertThatThrownBy(() -> target.subtract(operand)).isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("빼려는 길이가 더 깁니다. 대상 길이: " + target.getValue() + ", 빼려는 길이: " + operand.getValue());
    }
}
