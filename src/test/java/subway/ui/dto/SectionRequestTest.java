package subway.ui.dto;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalRequestException;

@DisplayName("구간 요청 관련 기능 테스트")

class SectionRequestTest {

    @Test
    @DisplayName("구간 요청 생성에 성공한다.")
    void createSectionRequest_noException() {
        assertThatNoException()
            .isThrownBy(() -> new SectionRequest("1", "2", 2));
    }

    @Test
    @DisplayName("상행역은 공백일 수 없다.")
    void createLineRequest_blackName_throwException() {
        assertThatThrownBy(() -> new SectionRequest("", "2", 2))
            .hasMessage("상행역을 입력해야 합니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

    @Test
    @DisplayName("하행역은 공백일 수 없다.")
    void createLineRequest_blackColor_throwException() {
        assertThatThrownBy(() -> new SectionRequest("1", "", 2))
            .hasMessage("하행역을 입력해야 합니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

    @Test
    @DisplayName("거리는 0보다 커야한다.")
    void createLineRequest_underThanZero_throwException() {
        assertThatThrownBy(() -> new SectionRequest("1", "2", 0))
            .hasMessage("구간 거리는 0보다 커야합니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

}