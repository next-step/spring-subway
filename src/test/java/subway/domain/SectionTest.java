package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class SectionTest {

    private Line line;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "1호선", "green");
        upStation = new Station(1L, "낙성대");
        downStation = new Station(2L, "사당");
        distance = new Distance(10L);

    }

    @DisplayName("구간 정상 생성")
    @Test
    void validationSuccess() {
        // given, when, then
        assertThatCode(() -> new Section(line, upStation, downStation, distance))
                .doesNotThrowAnyException();
    }

    @DisplayName("노선 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationLineIdNotNull() {
        // given, when, then
        assertThatCode(() -> new Section(null,
                upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선은 null일 수 없습니다.");
    }

    @DisplayName("상행역 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationUpStationIdNotNull() {
        // given , when, then
        assertThatCode(() -> new Section(
                line, null, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 null일 수 없습니다.");
    }

    @DisplayName("하행역 아이디가 null 일시 구간 생성 실패")
    @Test
    void validationDownStationIdNotNull() {
        // given,  when, then
        assertThatCode(() -> new Section(
                line, upStation, null, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역 null일 수 없습니다.");
    }

    @DisplayName("거리가 null 일시 구간 생성 실패")
    @Test
    void validationDistanceNotNull() {
        // given , when, then
        assertThatCode(() ->
                new Section(line, upStation, downStation, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 null일 수 없습니다.");
    }

    @DisplayName("상행역 아이디와 하행역 아이디는 같을 시 구간 생성 실패")
    @Test
    void validationUpStationIdNotEqualDownStationId() {
        // given, when, then
        assertThatCode(() ->
                new Section(line, upStation, upStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }

}
