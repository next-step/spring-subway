package subway.ui.dto;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalRequestException;

@DisplayName("역 요청 관련 기능 테스트")
class StationRequestTest {

    @Test
    @DisplayName("역 요청 생성에 성공한다.")
    void createLineRequest_noException() {
        assertThatNoException()
            .isThrownBy(() -> new StationRequest("인천역"));
    }

    @Test
    @DisplayName("이름은 공백일 수 없다.")
    void createLineRequest_blackName_throwException() {
        assertThatThrownBy(() -> new StationRequest(""))
            .hasMessage("이름을 입력해야 합니다.")
            .isInstanceOf(IllegalRequestException.class);
    }

    @Test
    @DisplayName("이름 길이는 255자를 초과할 수 없다.")
    void createLineRequest_NameLongerThan255_throwException() {
        // given
        String invalidName = "invalidName".repeat(30);

        // when & then
        assertThatThrownBy(() -> new StationRequest(invalidName))
            .hasMessage("이름 길이는 255자를 초과할 수 없습니다.")
            .isInstanceOf(IllegalRequestException.class);
    }
}