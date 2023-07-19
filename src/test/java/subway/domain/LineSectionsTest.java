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

    @Test
    @DisplayName("현재 구간은 해당 노선에 속하지 않습니다")
    void sectionDoesNotBelongToLine() {
        Line otherLine = new Line(2L,"lineB", "red");
        Section section = new Section(lineA, stationA, stationB, 3);

        Assertions.assertThatThrownBy(() -> new LineSections(otherLine, section))
            .isInstanceOf(IllegalArgumentException.class);
    }


}
