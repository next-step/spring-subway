package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Section 클래스 테스트")
public class SectionTest {

    @Test
    @DisplayName("구간 길이는 양수여야 한다.")
    void sectionDistanceShouldBePositive() {
        assertThatCode(() -> new Section(1)).doesNotThrowAnyException();
        assertThatThrownBy(() -> new Section(-1)).isInstanceOf(IllegalArgumentException.class);
    }
}
