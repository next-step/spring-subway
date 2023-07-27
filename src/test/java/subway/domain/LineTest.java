package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IncorrectRequestException;

import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        // given
        String name = "신분당선";
        String color = "bg-red-600";

        // when & then
        assertDoesNotThrow(() -> new Line(name, color));
    }

    @DisplayName("이름이이 null인지 확인한다.")
    @Test
    void validateNameNotNull() {
        // given
        String name = null;
        String color = "bg-red-600";

        // when & then
        assertThrows(IncorrectRequestException.class,
                () -> new Line(name, color));
    }

    @DisplayName("색깔이 null인지 확인한다.")
    @Test
    void validateColorNotNull() {
        // given
        String name = "신분당선";
        String color = null;

        // when & then
        assertThrows(IncorrectRequestException.class,
                () -> new Line(name, color));
    }

    @DisplayName("이름이 255자 이하인지 확인한다.")
    @Test
    void validateNameLength() {
        // given
        String nameOverThan255 = "이 문장은 총 스물다섯글자로 이루어져 있습니다".repeat(11);
        String color = "bg-red-600";

        // when & then
        assertThrows(IncorrectRequestException.class,
                () -> new Line(nameOverThan255, color));
    }

    @DisplayName("색이 20자 이하인지 확인한다.")
    @Test
    void validateColorLength() {
        // given
        String name = "신분당선";
        String colorOverThan20 = "color too long! long!!!";

        // when & then
        assertThrows(IncorrectRequestException.class,
                () -> new Line(name, colorOverThan20));
    }
}
