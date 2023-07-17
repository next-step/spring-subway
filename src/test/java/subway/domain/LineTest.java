package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LineTest {
    @Test
    @DisplayName("해당 역이 노선의 하행 종점역인지 검증하는 기능")
    void test() {
        // given
        Station station = new Station("신대방역");
        Line line = new Line(1L, "2호선", "green", List.of(new Section("서울대입구역", "신대방역", 10)));
        
        // when & then
        assertThat(line.isTerminal(station)).isTrue();
    }
}
