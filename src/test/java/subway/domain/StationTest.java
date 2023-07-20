package subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StationTest {

    @Test
    @DisplayName("name 필드가 빈 값이면 예외를 던진다.")
    void fieldTest1() {
        assertThatThrownBy(() -> new Station("   "))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("name 필드가 null이면 예외를 던진다.")
    void fieldTest2() {
        assertThatThrownBy(() -> new Station(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
