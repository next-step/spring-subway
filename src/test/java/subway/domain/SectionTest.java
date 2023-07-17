package subway.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Section 클래스 테스트")
class SectionTest {

    @Test
    @DisplayName("구간 길이는 양수여야 한다.")
    void sectionDistanceShouldBePositive() {
        Station upStation = new Station(1L, "stationA");
        Station downStation = new Station(2L, "stationB");

        assertThatCode(() -> new Section(upStation, downStation, 1)).doesNotThrowAnyException();
        assertThatThrownBy(() -> new Section(upStation, downStation, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상행역과 하행역은 같을 수 없다")
    void upStationAndDownStationShouldNotEqual() {
        Station station = new Station(1L, "stationA");
        assertThatThrownBy(() -> new Section(station, station, 1)).isInstanceOf(IllegalArgumentException.class);
    }
}
