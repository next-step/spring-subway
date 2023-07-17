package subway.domain;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    @DisplayName("구간 정상 생성")
    @Test
    void validationSuccess() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 10L;

        // when, then
        assertThatCode(() -> new Section(lineId, upStationId, downStationId, distance))
                .doesNotThrowAnyException();
    }

    @DisplayName("노선 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationLineIdNotNull() {
        // given
        Long lineId = null;
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = 10L;

        // when, then
        assertThatCode(() -> new Section(lineId, upStationId, downStationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선 아이디는 null일 수 없습니다.");
    }

    @DisplayName("상행역 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationUpStationIdNotNull() {
        // given
        Long lineId = 1L;
        Long upStationId = null;
        Long downStationId = 2L;
        Long distance = 10L;

        // when, then
        assertThatCode(() -> new Section(lineId, upStationId, downStationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 아이디는 null일 수 없습니다.");
    }

    @DisplayName("하행역 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationDownStationIdNotNull() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = null;
        Long distance = 10L;

        // when, then
        assertThatCode(() -> new Section(lineId, upStationId, downStationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역 아이디는 null일 수 없습니다.");
    }

    @DisplayName("거리가 null 일시 구간 생성 실패")
    @Test
    void validationDistanceNotNull() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = null;

        // when, then
        assertThatCode(() -> new Section(lineId, upStationId, downStationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 null일 수 없습니다.");
    }

    @DisplayName("상행역 아이디와 하행역 아이디는 같을 시 구간 생성 실패")
    @Test
    void validationUpStationIdNotEqualDownStationId() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 1L;
        Long distance = 10L;

        // when, then
        assertThatCode(() -> new Section(lineId, upStationId, downStationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 아이디와 하행역 아이디는 같을 수 없습니다.");
    }

    @DisplayName("상행역 아이디와 하행역 아이디는 같을 시 구간 생성 실패")
    @Test
    void validationPositiveDistance() {
        // given
        Long lineId = 1L;
        Long upStationId = 1L;
        Long downStationId = 2L;
        Long distance = -1L;

        // when, then
        assertThatCode(() -> new Section(lineId, upStationId, downStationId, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 " + 1L + "이상이어야 합니다.");
    }
}
