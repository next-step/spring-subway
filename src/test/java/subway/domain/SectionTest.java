package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SectionTest {

    @DisplayName("생성 테스트")
    @Test
    void createSectionTest() {
        assertThatNoException().isThrownBy(() -> new Section(4L, 2L, 10.0));
    }
}
