package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

class SectionTest {

    @Test
    @DisplayName("Section 생성 테스트")
    void fieldTest() {
        Assertions.assertThatNoException()
            .isThrownBy(() -> new Section(new Station(), new Station(), new Line(), 10));
    }

    @Test
    @DisplayName("Section combine 정상 실행 테스트")
    void combine() {
        Line line = new Line(1L, "7호선", "주황");
        Station station1 = new Station(1L, "부천시청역");
        Station station2 = new Station(2L, "신중동역");
        Station station3 = new Station(3L, "춘의역");
        Section section1 = new Section(station1, station2, line, 10);
        Section section2 = new Section(station2, station3, line, 10);

        Section result = section1.combineSection(section2);

        assertAll(
                () -> Assertions.assertThat(result.getDistance()).isEqualTo(new Distance(20)),
                () -> Assertions.assertThat(result.getUpStation()).isEqualTo(station1),
                () -> Assertions.assertThat(result.getDownStation()).isEqualTo(station3),
                () -> Assertions.assertThat(result.getLine()).isEqualTo(line)
        );

    }

    @Test
    @DisplayName("Section combine 예외 상황(상행과 하행이 다름) 테스트")
    void combineException() {
        Line line = new Line(1L, "7호선", "주황");
        Station station1 = new Station(1L, "부천시청역");
        Station station2 = new Station(2L, "신중동역");
        Station station3 = new Station(3L, "춘의역");
        Station station4 = new Station(4L, "상동역");
        Section section1 = new Section(station1, station4, line, 10);
        Section section2 = new Section(station2, station3, line, 10);

        Assertions.assertThatThrownBy(() -> section1.combineSection(section2))
                .isInstanceOf(IllegalArgumentException.class);

    }
}
