package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Section 클래스 테스트")
class SectionTest {

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
    @DisplayName("구간 길이는 양수여야 한다.")
    void sectionDistanceShouldBePositive() {
        Station upStation = new Station(1L, "A");
        Station downStation = new Station(2L, "B");

        assertThatCode(() -> new Section(lineA, upStation, downStation, 1)).doesNotThrowAnyException();
        assertThatThrownBy(() -> new Section(lineA, upStation, downStation, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역은 같을 수 없다")
    void upStationAndDownStationShouldNotEqual() {
        assertThatThrownBy(() -> new Section(lineA, stationA, stationA, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자신의 하행역과 주어진 구간의 상행역이 같은지 확인한다.")
    void checkMyDownStationAndOtherUpStationEqual() {
        Section section = new Section(lineA, stationA, stationB, 1);
        Section otherSection = new Section(lineA, stationB, stationC, 1);

        assertThat(section.canPrecede(otherSection)).isTrue();
        assertThat(otherSection.canPrecede(section)).isFalse();
    }

    @Test
    @DisplayName("해당 역은 구간의 하행역과 같다")
    void hasDownStationSameAs() {
        Section section = new Section(lineA, stationA, stationB, 1);

        assertThat(section.hasDownStationSameAs(stationB)).isTrue();
        assertThat(section.hasDownStationSameAs(stationA)).isFalse();
    }
    @DisplayName("상대 구역은 현재 구역과 관련있다")
    void relatedSection() {
        Station nothingStation = new Station(5L, "any");
        Section section = new Section(lineA, stationB, stationC, 2);
        Section sectionSameUpUp = new Section(lineA, stationB, nothingStation, 3);
        Section sectionSameUpDown = new Section(lineA, nothingStation, stationB, 3);
        Section sectionSameDownUp = new Section(lineA, stationC, nothingStation, 2);
        Section sectionSameDownDown = new Section(lineA, nothingStation, stationC, 2);

        assertThat(section.isRelated(sectionSameUpUp)).isTrue();
        assertThat(section.isRelated(sectionSameUpDown)).isTrue();
        assertThat(section.isRelated(sectionSameDownUp)).isTrue();
        assertThat(section.isRelated(sectionSameDownDown)).isTrue();
    }


    @Test
    @DisplayName("구간은 주어진 라인의 소속이다")
    void sectionBelongToLine() {
        Section section = new Section(lineA, stationA, stationB, 1);
        
        assertThat(section.belongTo(lineA)).isTrue();
    }

    @Test
    @DisplayName("추가할 구간의 크기가 같거나 큰 경우 합칠 수 없다.")
    void cannotMergeLargeOrSameLengthSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section sameLengthSection = new Section(lineA, stationA, stationB, 3);
        Section largeSection = new Section(lineA, stationA, stationB, 4);

        assertThatThrownBy(() -> section.mergeWith(sameLengthSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section.mergeWith(largeSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간의 상행역과 추가할 구간의 상행역이 같은 경우 합친다")
    void mergeSameUpStationSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section targetSection = new Section(lineA, stationA, stationB, 2);

        List<Section> mergedSections = section.mergeWith(targetSection);

        Section createdSection = new Section(lineA, stationB, stationC, 1);
        assertThat(mergedSections).isEqualTo(List.of(targetSection, createdSection));
    }

    @Test
    @DisplayName("구간의 하행역과 추가할 구간의 하행역이 같은 경우 합친다")
    void mergeSameDownStationSection() {
        Section section = new Section(lineA, stationA, stationC, 3);
        Section targetSection = new Section(lineA, stationB, stationC, 2);

        List<Section> mergedSections = section.mergeWith(targetSection);

        Section createdSection = new Section(lineA, stationA, stationB, 1);
        assertThat(mergedSections).isEqualTo(List.of(createdSection, targetSection));
    }

    @Test
    @DisplayName("구간의 상행역, 하행역이 추가할 구간의 상행역, 하행역과 모두 같거나 모두 다른 경우 합칠 수 없다.")
    void cannotMergeAllSameStationOrNothingSameStationSection() {
        Section section = new Section(lineA, stationA, stationB, 3);
        Section bothSameSection = new Section(lineA, stationA, stationB, 2);
        Section nothingSameSection = new Section(lineA, stationC, stationD, 2);

        assertThatThrownBy(() -> section.mergeWith(bothSameSection))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> section.mergeWith(nothingSameSection))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간의 하행역이 연결할 구간의 상행역과 다르면 중간역을 삭제할 수 없다.")
    void cannotRemoveMiddleStationWithDifferentCurDownAndNextUpStation() {
        Section sectionA = new Section(lineA, stationA, stationB, 3);
        Section sectionC = new Section(lineA, stationC, stationD, 2);

        assertThatThrownBy(() -> sectionA.removeMiddleStation(sectionC))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("현재구간과 상대구간의 중간 역을 삭제한다")
    void removeMiddleStation() {
        Section sectionA = new Section(lineA, stationA, stationB, 3);
        Section sectionB = new Section(lineA, stationB, stationC, 2);

        Section expectedSection = new Section(lineA, stationA, stationC, 5);
        assertThat(sectionA.removeMiddleStation(sectionB)).isEqualTo(expectedSection);
    }

}
