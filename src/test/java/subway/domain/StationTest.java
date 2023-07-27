package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IncorrectRequestException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StationTest {

    @DisplayName("노선을 생성한다.")
    @Test
    void create() {
        // given
        String name = "강남역";

        // when & then
        assertDoesNotThrow(() -> new Station(name));
    }

    @DisplayName("이름이이 null인지 확인한다.")
    @Test
    void validateNameNotNull() {
        // given
        String name = null;

        // when & then
        assertThrows(IncorrectRequestException.class,
                () -> new Station(name));
    }

    @DisplayName("이름이 255자 이하인지 확인한다.")
    @Test
    void validateNameLength() {
        // given
        String nameOverThan255 = "이 문장은 총 스물다섯글자로 이루어져 있습니다".repeat(11);

        // when & then
        assertThrows(IncorrectRequestException.class,
                () -> new Station(nameOverThan255));
    }
}
