package subway.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.InvalidRequestException;

class NameTest {

    @DisplayName("지하철 역명이나 노선명에 사용할 이름 생성")
    @ParameterizedTest
    @ValueSource(strings = {"일", "일이", "일이삼"})
    void createName(String name) {
        assertDoesNotThrow(() -> new Name(name));
    }

    @DisplayName("이름 길이 제한을 초과하는 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 256})
    void outOfRangeNameLength(int length) {
        // given
        final String name = "가".repeat(length);

        // when & then
        assertThrows(InvalidRequestException.class, () -> new Name(name));
    }
}
