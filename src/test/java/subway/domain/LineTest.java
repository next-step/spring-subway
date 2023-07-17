package subway.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LineTest {

    @Test
    @DisplayName("name 필드가 비어있으면 예외를 던진다.")
    void test1() {
        assertThatThrownBy(() -> new Line(null, "파랑"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
