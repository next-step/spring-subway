package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SectionTest {

    @DisplayName("지하철 노선에 구간을 등록할 수 있다.")
    @Test
    void create() {
        assertDoesNotThrow(() -> new Section());
    }
}
