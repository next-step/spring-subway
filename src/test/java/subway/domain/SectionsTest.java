package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
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

        sections = new Sections(List.of(section1, section3, section2));
    }

    @Test
    @DisplayName("Sections의 노선이 다른 경우 예외가 발생한다.")
    void validLine() {
        Section otherLineSection = new Section(
            100L,
            station3,
            station4,
            new Line(2L, "10호선", "무지개"),
            10
        );

        assertThatThrownBy(() ->
            new Sections(List.of(section1, section3, otherLineSection)).validSectionsLine())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("새로운 구간의 상행과 하행이 기존 섹션에 있을 경우 예외가 발생한다.")
    void validNewSectionDownStation() {
        assertThatThrownBy(() -> sections.makeUpdateSection(section1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 등록 정상 동작")
    void registerSection() {
        Section section4 = new Section(
            4L,
            station4,
            station5,
            line1,
            10
        );
        assertThatNoException()
            .isThrownBy(() -> sections.makeUpdateSection(section4));
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
        assertThatThrownBy(() -> sections.makeUpdateSection(section4))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 추가할 수 없음")
    void sectionRegisterReject() {
        Section section4 = new Section(
            4L,
            station5,
            station6,
            line1,
            10
        );
        assertThatThrownBy(() -> sections.validRegisterSection(section4))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Line에 역이 하나도 없다면 구간이 무조건 등록된다")
    void registerSectionInEmptyLine() {
        Sections sections = new Sections(Collections.emptyList());
        assertThatNoException().isThrownBy(() -> sections.makeUpdateSection(section1));
    }

    @Test
    @DisplayName("구간이 1개 이하인 경우 해당역을 삭제할 수 없다")
    void canNotRemoveStation() {
        Sections sections = new Sections(List.of(section1));
        assertThatThrownBy(() -> sections.validDeleteStation(station1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 짧으면 등록할 수 있다.")
    void registerMiddleUpSection() {
        Section addSection = new Section(
            station3,
            station5,
            line1,
            5
        );

        sections.validRegisterSection(addSection);

        Section modifySection = sections.makeUpdateSection(addSection).get();

        Section expectedNewSection = new Section(
            3L,
            station5,
            station4,
            line1,
            5
        );

        Assertions.assertAll(
            () -> assertThat(addSection.equals(addSection)).isTrue(),
            () -> assertThat(modifySection.equals(expectedNewSection)).isTrue()
        );
    }

    @Test
    @DisplayName("상행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 길면 예외가 발생한다.")
    void registerMiddleUpSectionException() {
        Section newSection = new Section(
            station3,
            station5,
            line1,
            15
        );

        assertThatThrownBy(() -> sections.makeUpdateSection(newSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("하행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 짧으면 등록할 수 있다.")
    void registerMiddleDownSection() {
        Section addSection = new Section(
            station5,
            station4,
            line1,
            5
        );
        Section expectedNewSection = new Section(
            3L,
            station3,
            station5,
            line1,
            5
        );
        Section modifySection = sections.makeUpdateSection(addSection).get();
        Assertions.assertAll(
            () -> assertThat(addSection.equals(addSection)).isTrue(),
            () -> assertThat(modifySection.equals(expectedNewSection)).isTrue()
        );
    }

    @Test
    @DisplayName("하행에서 추가: 새로운 구간을 등록할 때 기존 구간의 거리보다 길면 예외가 발생한다.")
    void registerMiddleDownSectionException() {
        Section newSection = new Section(
            station5,
            station4,
            line1,
            15
        );

        assertThatThrownBy(() -> sections.makeUpdateSection(newSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("계층적으로 정렬된 Sections를 반환한다.")
    void sort() {
        List<Station> result = sections.sortStations();
        assertThat(result).containsExactly(station1, station2, station3, station4);
    }
}
