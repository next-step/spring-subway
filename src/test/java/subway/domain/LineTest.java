package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LineTest {
    @Test
    @DisplayName("해당 역이 노선의 하행 종점역인지 검증하는 기능")
    void containStationTest() {
        // given
        Station station = new Station(2L, "신대방역");
        Line line = new Line(
                1L,
                "2호선",
                "green",
                List.of(new Section(new Station(1L, "서울대입구역"), new Station(2L, "신대방역"), 10))
        );

        // when & then
        assertThat(line.isTerminal(station)).isTrue();
    }

    @Test
    @DisplayName("해당 역이 노선의 하행역이 아닌 경우")
    void notContainTest() {
        // given
        Station station = new Station(1L, "서울대입구역");
        Line line = new Line(
                1L,
                "2호선",
                "green",
                List.of(new Section(new Station(1L, "서울대입구역"), new Station(2L, "신대방역"), 10))
        );

        // when & then
        assertThat(line.isTerminal(station)).isFalse();
    }


}
