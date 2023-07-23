package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionTest {

    private Station station1;
    private Station station2;
    private Line line;
    private int distance;

    @BeforeEach
    void setUp() {
        station1 = new Station(5L, "잠실역");
        station2 = new Station(6L, "잠실새내역");
        line = new Line(2L, "2호선", "color");
        distance = 10;
    }

    @Test
    @DisplayName("Section 생성 테스트")
    void fieldTest() {
        Assertions.assertThatNoException()
            .isThrownBy(() -> new Section(station1, station2, line, distance));
    }

}
