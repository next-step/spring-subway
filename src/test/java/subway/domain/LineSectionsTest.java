package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LineSections 단위 테스트")
class LineSectionsTest {

    Line lineA;
    Station stationA;
    Station stationB;
    Station stationC;
    Station stationD;

    @BeforeEach
    void setUp() {
        lineA = new Line(1L, "A", "red");
        stationA = new Station(1L, "A");
        stationB = new Station(2L, "B");
        stationC = new Station(3L, "C");
        stationD = new Station(4L, "D");
    }

    @Test
    @DisplayName("노선을 생성할 때 한 구간을 생성한다")
    void createLineWithOneSection() {
        Section section = new Section(lineA, stationA, stationB, 3);

        Assertions.assertThatCode(() -> new LineSections(lineA, section))
            .doesNotThrowAnyException();
    }
}
