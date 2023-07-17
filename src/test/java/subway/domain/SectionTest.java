package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @Test
    @DisplayName("Section 생성 테스트")
    void fieldTest() {
        Assertions.assertThatNoException()
            .isThrownBy(() -> new Section(new Station(), new Station(), new Line(), 10));
    }

}
