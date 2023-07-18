package subway.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;
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
        station6 = new Station(5L, "온수역");
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

    @Test
    @DisplayName("상행역과 하행역이 이미 Line에 모두 등록되어있다면 추가할 수 없음")
    void duplicateSection() {
        Section section4 = new Section(
            4L,
            station1,
            station2,
            line1,
            10
        );
        assertThatThrownBy(() -> sections.validNewSection(section4))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 추가할 수 없음")
    void sectionRegistReject() {
        Section section4 = new Section(
            4L,
            station5,
            station6,
            line1,
            10
        );
        assertThatThrownBy(() -> sections.validNewSection(section4))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("하행 종점역은 삭제할 수 있다.")
    void canDelete() {
        assertThatNoException()
                .isThrownBy(() -> sections.canDeleteStation(4L));
    }

    @Test
    @DisplayName("하행 종점역이 아니면 삭제할 수 없다.")
    void canNotDelete() {
        Assertions.assertAll(
            () -> assertThatThrownBy(() -> sections.canDeleteStation(1L))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> sections.canDeleteStation(2L))
                .isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(() -> sections.canDeleteStation(3L))
                .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @Test
    @DisplayName("Line에 역이 하나도 없다면 구간이 무조건 등록된다")
    void registSectionInEmptyLine() {
        Sections sections = new Sections(Collections.emptyList());
        assertThatNoException().isThrownBy(() -> sections.validNewSection(section1));
    }

    @Test
    @DisplayName("구간이 1개 이하인 경우 해당역을 삭제할 수 없다")
    void canNotRemoveStation() {
        Sections sections = new Sections(List.of(section1));
        assertThatThrownBy(() -> sections.canDeleteStation(2L))
            .isInstanceOf(IllegalStateException.class);
    }
}
