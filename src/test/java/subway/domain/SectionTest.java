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

    @DisplayName("노선 정보 , 상행역 , 하행역 , 상행역과 하행역 사이 거리가 입력으로 주어지면 구간을 생성하는데 성공한다.")
    @Test
    void validationSuccess() {
        // given, when, then
        assertThatCode(() -> new Section(line, upStation, downStation, distance))
                .doesNotThrowAnyException();
    }

    @DisplayName("노선 정보가 null 이면 구간 생성 실패하고 예외를 던진다.")
    @Test
    void validationLineIdNotNull() {
        // given, when, then
        assertThatCode(() -> new Section(null,
                upStation, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("노선은 null일 수 없습니다.");
    }

    @DisplayName("상행역 정보가 null 이면 구간 생성 실패하고 예외를 던진다.")
    @Test
    void validationUpStationIdNotNull() {
        // given , when, then
        assertThatCode(() -> new Section(
                line, null, downStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역 null일 수 없습니다.");
    }

    @DisplayName("하행역 정보가 null 이면 구간 생성 실패하고 예외를 던진다.")
    @Test
    void validationDownStationIdNotNull() {
        // given,  when, then
        assertThatCode(() -> new Section(
                line, upStation, null, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("하행역 null일 수 없습니다.");
    }

    @DisplayName("구간 정보가 null 이면 구간 생성 실패하고 예외를 던진다.")
    @Test
    void validationDistanceNotNull() {
        // given , when, then
        assertThatCode(() ->
                new Section(line, upStation, downStation, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 null일 수 없습니다.");
    }

    @DisplayName("상행역과 하행역이 같을 시 구간 생성 실패하고 예외를 던진다.")
    @Test
    void validationUpStationIdNotEqualDownStationId() {
        // given, when, then
        assertThatCode(() ->
                new Section(line, upStation, upStation, distance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역은 같을 수 없습니다.");
    }

}
