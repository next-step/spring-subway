package subway.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.SectionMinDistanceException;
import subway.exception.SectionSameStationException;

public class SectionTest {

    @DisplayName("상행역과 하행역이 서로 다르고 거리가 1이상일 때 구간 아이디, 노선 아이디, 상행역 아이디, 하행역 아이디, 거리를 입력하여 생성하면 구간이 생성된다.")
    @Test
    void create() {
        // given

        // when

        // then
        assertDoesNotThrow(() -> new Section(1L, 1L, 1L, 2L, 1));
    }

    @DisplayName("상행역과 하행역이 같고 거리가 1이상일 때 구간 아이디, 노선 아이디, 상행역 아이디, 하행역 아이디, 거리를 입력하여 생성하면 에러를 반환한다.")
    @Test
    void createFalse() {
        // given

        // when

        // then
        assertThrows(SectionSameStationException.class,
            () -> new Section(1L, 1L, 1L, 1L, 1));
    }

    @DisplayName("상행역과 하행역이 다르고 거리가 1미만일 때 구간 아이디, 노선 아이디, 상행역 아이디, 하행역 아이디, 거리를 입력하여 생성하면 에러를 반환한다.")
    @Test
    void createFalse2() {
        // given

        // when

        // then
        assertThrows(SectionMinDistanceException.class,
            () -> new Section(1L, 1L, 1L, 2L, 0));
    }
}
