package subway.ui.dto;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalRequestException;

@DisplayName("노선 요청 관련 기능 테스트")

class LineRequestTest {

    @Test
    @DisplayName("라인 요청 생성에 성공한다.")
    void createLineRequest_noException() {
        assertThatNoException()
            .isThrownBy(() -> new LineRequest("1호선", 1, 2, 10, "blue"));
    }

    @Test
    @DisplayName("이름은 공백일 수 없다.")
    void createLineRequest_blackName_throwException() {
        assertThatThrownBy(() -> new LineRequest("", 1, 2, 10, "blue"))
            .hasMessage("이름을 입력해야 합니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

    @Test
    @DisplayName("이름 길이는 255자를 초과할 수 없다.")
    void createLineRequest_NameLongerThan255_throwException() {
        // given
        String invalidName = "invalidName".repeat(30);

        // when & then
        assertThatThrownBy(() -> new LineRequest(invalidName, 1, 2, 10, "blue"))
            .hasMessage("이름 길이는 255자를 초과할 수 없습니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

    @Test
    @DisplayName("생삭은 공백일 수 없다.")
    void createLineRequest_blackColor_throwException() {
        assertThatThrownBy(() -> new LineRequest("1호선", 1, 2, 10, ""))
            .hasMessage("색상을 입력해야 합니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

    @Test
    @DisplayName("색상 길이는 20자를 초과할 수 없다.")
    void createLineRequest_ColorLongerThan20_throwException() {
        // given
        String invalidColor = "invalidColor".repeat(3);

        // when & then
        assertThatThrownBy(() -> new LineRequest("1호선", 1, 2, 10, invalidColor))
            .hasMessage("색상 길이는 20자를 초과할 수 없습니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

    @Test
    @DisplayName("거리는 0보다 커야한다.")
    void createLineRequest_underThanZero_throwException() {
        assertThatThrownBy(() -> new LineRequest("1호선", 1, 2, 0, "blue"))
            .hasMessage("구간 거리는 0보다 커야합니다.")
            .isInstanceOf(IllegalRequestException.class);
    }
}