package subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("name 필드가 비어있으면 예외를 던진다.")
    void fieldTest1() {
        assertThatThrownBy(() -> new Line(null, "파랑"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("color 필드가 비어있으면 예외를 던진다.")
    void fieldTest2() {
        assertThatThrownBy(() -> new Line("신중동", " "))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("name&color 필드가 비어있으면 예외를 던진다.")
    void fieldTest3() {
        assertThatThrownBy(() -> new Line(" ", null))
            .isInstanceOf(IllegalArgumentException.class);
    }

}
