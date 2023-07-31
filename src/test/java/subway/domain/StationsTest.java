package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StationsTest {

    @Test
    @DisplayName("Section의 List로 Stations를 생성할 수 있다.")
    void ofSectionListTest() {
        // given
        Station station1 = new Station("서울역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("신대방역");
        Station station4 = new Station("상도역");

        Section section1 = new Section(station1, station2, 1);
        Section section2 = new Section(station3, station4, 1);
        Section section3 = new Section(station4, station1, 1);

        // when
        Stations stations = Stations.of(List.of(section1, section2, section3));

        // then
        assertThat(stations.getStations()).contains(station1, station2, station3, station4);
    }

    @Test
    @DisplayName("역이 Stations에 포함되어 있으면 contains 메서드가 true를 반환한다.")
    void containsTrue() {
        // given
        Station station1 = new Station("서울역");
        Station station2 = new Station("잠실역");

        Stations stations = new Stations(Set.of(station1, station2));

        // when, then
        assertThat(stations.contains(station1)).isTrue();
    }

    @Test
    @DisplayName("역이 Stations에 포함되어 있으면 contains 메서드가 false를 반환한다.")
    void containsFalse() {
        // given
        Station station1 = new Station("서울역");
        Station station2 = new Station("잠실역");

        Stations stations = new Stations(Set.of(station1, station2));

        // when, then
        assertThat(stations.contains(new Station("상도역"))).isFalse();
    }

    @Test
    @DisplayName("subtract 메서드는 두 Stations의 차집합을 반환한다.")
    void subtract() {
        // given
        Station station1 = new Station("서울역");
        Station station2 = new Station("잠실역");
        Station station3 = new Station("신대방역");
        Station station4 = new Station("상도역");

        Stations stations1 = new Stations(Set.of(station1, station2, station3));
        Stations stations2 = new Stations(Set.of(station3, station4));

        // when
        Stations subtractStations = stations1.subtract(stations2);

        // then
        assertThat(subtractStations.getStations()).contains(station1, station2);
        assertThat(subtractStations.getStations()).doesNotContain(station3, station4);
    }

    @Test
    @DisplayName("Stations에 역이 존재하면 그 중 하나를 Optional로 반환한다.")
    void findAny() {
        // given
        Stations stations = new Stations(Set.of(new Station("서울역")));

        // when, then
        assertThat(stations.findAny().get().getName()).isEqualTo("서울역");
    }

    @Test
    @DisplayName("Stations에 역이 존재하지 않으면 Empty Optional을 반환한다.")
    void findAnyNull() {
        // given
        Stations stations = new Stations(Set.of());

        // when, then
        assertThat(stations.findAny().isEmpty()).isTrue();
    }
}
