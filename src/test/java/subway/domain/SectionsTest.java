package subway.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Line line1;
    private Section section1;
    private Section section2;
    private Section section3;
    private Sections sections;

    @BeforeEach
    void setup() {
        station1 = new Station(1L, "부천시청역");
        station2 = new Station(2L, "신중동역");
        station3 = new Station(3L, "춘의역");
        station4 = new Station(4L, "부천종합운동장역");
        station5 = new Station(5L, "까치울역");
        line1 = new Line(1L, "7호선", "주황");

        section1 = new Section(
            1L,
            station1,
            station2,
            line1,
            10
        );
        section2 = new Section(
            2L,
            station2,
            station3,
            line1,
            10
        );
        section3 = new Section(
            3L,
            station3,
            station4,
            line1,
            10
        );

        sections = new Sections(List.of(section1, section2, section3));
    }

    @Test
    @DisplayName("새로운 구간의 하행역은 해당 노선에 등록되어있는 역일 수 없다.")
    void validNewSectionDownStation() {
        assertThatThrownBy(() -> sections.validNewSection(section1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 한다.")
    void validNewSectionUpStation() {
        Section section4 = new Section(
            4L,
            station3,
            station5,
            line1,
            10
        );
        assertThatThrownBy(() -> sections.validNewSection(section4))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 등록 정상 동작")
    void registSection() {
        Section section4 = new Section(
            4L,
            station4,
            station5,
            line1,
            10
        );
        assertThatNoException()
            .isThrownBy(() -> sections.validNewSection(section4));
    }
}
