package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LineTest {
    @Test
    @DisplayName("해당 역이 노선의 하행 종점역인지 검증하는 기능")
    void terminalTest() {
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
    void notTerminalTest() {
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

    @Test
    @DisplayName("입력 구간의 하행역이 노선에 포함되어 있는 경우")
    void containStationTest() {
        //given
        Station upward = new Station(1L, "서울대입구역");
        Station downward = new Station(2L, "신대방역");
        Section section = new Section(upward, downward, 10);
        Line line = new Line(
                1L,
                "2호선",
                "green",
                List.of(section)
        );

        //when
        assertThat(line.contains(section.getDownward())).isTrue();
    }

    @Test
    @DisplayName("입력 구간의 하행역이 노선에 포함되어 있지 않은 경우")
    void notContainStationTest() {
        //given
        Station upward = new Station(1L, "서울대입구역");
        Station downward = new Station(2L, "신대방역");
        Section section = new Section(upward, downward, 10);
        Line line = new Line(
                1L,
                "2호선",
                "green",
                List.of(section)
        );

        //when
        assertThat(line.contains(new Station(3L, "상도역"))).isFalse();
    }
}
