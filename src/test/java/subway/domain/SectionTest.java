package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SectionTest {

    @DisplayName("지하철 노선에 구간을 등록할 수 있다.")
    @Test
    void create() {
        assertDoesNotThrow(() -> new Section());
    }

    @DisplayName("구간 상행역과 하행역은 다른 역이어야 한다.")
    @Test
    void validateDifferent() {
        assertThrows(IllegalArgumentException.class,
                () -> new Section(2L, 2L, 1L, 10));
    }
}
