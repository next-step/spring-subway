package subway.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;

class SectionTest {

    @DisplayName("생성 테스트")
    @Test
    void createSectionTest() {
        assertThatNoException().isThrownBy(() -> new Section(1L,  2L, 4L,10));
    }

    @DisplayName("길이 유효성 검증 테스트")
    @Test
    void validateDistanceTest() {
        assertThatThrownBy(() -> new Section(1L, 2L, 4L,0))
                .hasMessage("구간 길이는 0보다 커야한다.")
                .isInstanceOf(IllegalSectionException.class);
    }
}
