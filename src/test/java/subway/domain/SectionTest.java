package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;

class SectionTest {

    @DisplayName("생성 테스트")
    @Test
    void createSectionTest() {
        // given
        Line line = createInitialLine();
        List<Station> stations = createInitialStations();
        int distance = 10;

        // when & then
        assertThatNoException()
            .isThrownBy(() -> new Section(line, stations.get(0), stations.get(1), distance));
    }

    @DisplayName("길이 유효성 검증 테스트")
    @Test
    void validateDistanceTest() {
        // given
        Line line = createInitialLine();
        List<Station> stations = createInitialStations();
        int invalidDistance = 0;

        // when & then
        assertThatThrownBy(() -> new Section(line, stations.get(0), stations.get(1), invalidDistance))
            .hasMessage("구간 길이는 0보다 커야한다.")
            .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("기존 구간의 상행역을 새로운 구간의 하행 역 방향으로 축소한다.")
    @Test
    void narrowToUpDirectionTest() {
        // given
        Station station = new Station(3L, "guui");
        Section current = createInitialSection();
        Section newSection = new Section(2L, current.getLine(), current.getUpStation(), station,
            5);

        // when
        Section narrowed = current.narrowToUpDirection(newSection.getDownStation(),
            newSection.getDistance());

        // then
        assertThat(narrowed.getId()).isEqualTo(current.getId());
        assertThat(narrowed.getLine()).isEqualTo(current.getLine());
        assertThat(narrowed.getUpStation()).isEqualTo(newSection.getDownStation());
        assertThat(narrowed.getDownStation()).isEqualTo(current.getDownStation());
        assertThat(narrowed.getDistance()).isEqualTo(
            current.getDistance() - newSection.getDistance());
    }

    @DisplayName("기존 구간의 하행역을 새로운 구간의 싱헹 역 방향으로 축소한다.")
    @Test
    void narrowToDownDirectionTest() {
        // given

        Station station = new Station(3L, "guui");
        Section current = createInitialSection();
        Section newSection = new Section(2L, current.getLine(), station, current.getDownStation(),
            5);

        // when
        Section narrowed = current.narrowToUpDirection(newSection.getUpStation(),
            newSection.getDistance());

        // then
        assertThat(narrowed.getId()).isEqualTo(current.getId());
        assertThat(narrowed.getLine()).isEqualTo(current.getLine());
        assertThat(narrowed.getUpStation()).isEqualTo(current.getUpStation());
        assertThat(narrowed.getDownStation()).isEqualTo(newSection.getUpStation());
        assertThat(narrowed.getDistance())
            .isEqualTo(current.getDistance() - newSection.getDistance());
    }

    @DisplayName("현재구간을 상행 방향으로 확장한다.")
    @Test
    void extendToUpDirectionTest() {
        // given
        Station station = new Station(3L, "guui");
        Section current = createInitialSection();
        Section upDirection = new Section(2L, current.getLine(), station, current.getDownStation(),
            5);

        // when
        Section extended = current.extendToUpDirection(upDirection);

        // then
        assertThat(extended.getId()).isEqualTo(current.getId());
        assertThat(extended.getLine()).isEqualTo(current.getLine());
        assertThat(extended.getUpStation()).isEqualTo(upDirection.getUpStation());
        assertThat(extended.getDownStation()).isEqualTo(current.getDownStation());
        assertThat(extended.getDistance())
            .isEqualTo(upDirection.getDistance() + current.getDistance());
    }

    private List<Station> createInitialStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(1L, "jamsil"));
        stations.add(new Station(2L, "jamsilnaru"));
        return stations;
    }

    private Section createInitialSection() {
        Line line = createInitialLine();
        List<Station> stations = createInitialStations();
        return new Section(1L, line, stations.get(0), stations.get(1), 10);
    }

    private Line createInitialLine() {
        return new Line(1, "1호선", "blue");
    }
}
