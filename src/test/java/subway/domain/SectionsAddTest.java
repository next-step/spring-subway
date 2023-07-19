package subway.domain;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsAddTest {

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
    @DisplayName("다른 라인의 구간이 추가될 수 없다.")
    void cannotAddSectionOfOtherLine() {
        Line otherLine = new Line(2L, "B", "green");
        Section firstSection = new Section(lineA, stationA, stationB, 5);
        Section secondSection = new Section(lineA, stationB, stationC, 5);
        Section otherLineSection = new Section(otherLine, stationC, stationD, 3);
        Sections sections = new Sections(List.of(firstSection, secondSection));

        Assertions.assertThatThrownBy(() -> sections.add(otherLineSection))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
