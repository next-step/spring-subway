package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Section 클래스 테스트")
class SectionTest {

    @Test
    @DisplayName("구간 길이는 양수여야 한다.")
    void sectionDistanceShouldBePositive() {
        Station upStation = new Station(1L, "A");
        Station downStation = new Station(2L, "B");

        assertThatCode(() -> new Section(upStation, downStation, 1)).doesNotThrowAnyException();
        assertThatThrownBy(() -> new Section(upStation, downStation, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역은 같을 수 없다")
    void upStationAndDownStationShouldNotEqual() {
        Station station = new Station(1L, "stationA");
        assertThatThrownBy(() -> new Section(station, station, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자신의 하행역과 주어진 구간의 상행역이 같은지 확인한다.")
    void checkMyDownStationAndOtherUpStationEqual() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Section section = new Section(stationA, stationB, 1);
        Section otherSection = new Section(stationB, stationC, 1);

        assertThat(section.cannotPrecede(otherSection)).isFalse();
        assertThat(otherSection.cannotPrecede(section)).isTrue();
    }

    @Test
    @DisplayName("주어진 구간의 하행역을 포함하는지 확인한다.")
    void checkContainingDownStationOfSection() {
        Station stationA = new Station(1L, "A");
        Station stationB = new Station(2L, "B");
        Station stationC = new Station(3L, "C");
        Section section = new Section(stationA, stationB, 1);
        Section targetSection = new Section(stationC, stationB, 1);

        assertThat(section.containsDownStationOf(targetSection)).isTrue();
    }
}
