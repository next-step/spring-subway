package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.exception.IllegalSectionException;

class SectionTest {

    @DisplayName("생성 테스트")
    @Test
    void createSectionTest() {
        // given
        Line line = createInitialLine();
        Station upStation = new Station(1L, "jamsil");
        Station downStation = new Station(2L, "jamsilnaru");
        int distance = 10;

        // when & then
        assertThatNoException().isThrownBy(
            () -> new Section(line, upStation, downStation, distance));
    }

    @DisplayName("길이 유효성 검증 테스트")
    @Test
    void validateDistanceTest() {
        // given
        Line line = createInitialLine();
        Station upStation = new Station(1L, "jamsil");
        Station downStation = new Station(2L, "jamsilnaru");
        int invalidDistance = 0;

        // when & then
        assertThatThrownBy(() -> new Section(line, upStation, downStation, invalidDistance))
            .hasMessage("구간 길이는 0보다 커야한다.")
            .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("기존 구간의 상행역을 새로운 구간의 하행 역 방향으로 축소한다.")
    @Test
    void narrowToUpDirectionTest() {
        // given
        Line line = createInitialLine();
        Station overlapStation = new Station(3L, "guui");
        Station jamsil = new Station(1L, "jamsil");
        Station jamsilnaru = new Station(2L, "jamsilnaru");

        Section newSection = new Section(1L, line, overlapStation, jamsil, 6);
        Section overlapped = new Section(2L, line, overlapStation, jamsilnaru, 10);

        // when
        Section narrow = overlapped.narrowToDownDirection(newSection.getDownStation(),
            newSection.getDistance());

        // then
        assertThat(narrow.getId()).isEqualTo(overlapped.getId());
        assertThat(narrow.getLine()).isEqualTo(line);
        assertThat(narrow.getUpStation()).isEqualTo(newSection.getDownStation());
        assertThat(narrow.getDownStation()).isEqualTo(overlapped.getDownStation());
        assertThat(narrow.getDistance()).isEqualTo(overlapped.getDistance() - newSection.getDistance());
    }

    @DisplayName("기존 구간의 하행역을 새로운 구간의 싱헹 역 방향으로 축소한다.")
    @Test
    void narrowToDownDirectionTest() {
        // given
        Line line = createInitialLine();
        Station jamsil = new Station(1L, "jamsil");
        Station jamsilnaru = new Station(2L, "jamsilnaru");
        Station overlapStation = new Station(3L, "guui");

        Section overlapped = new Section(1L, line, jamsil, overlapStation, 10);
        Section newSection = new Section(1L, line, jamsilnaru, overlapStation, 6);

        // when
        Section narrowed = overlapped.narrowToUpDirection(newSection.getUpStation(),
            newSection.getDistance());

        // then
        assertThat(narrowed.getId()).isEqualTo(overlapped.getId());
        assertThat(narrowed.getLine()).isEqualTo(line);
        assertThat(narrowed.getUpStation()).isEqualTo(overlapped.getUpStation());
        assertThat(narrowed.getDownStation()).isEqualTo(newSection.getUpStation());
        assertThat(narrowed.getDistance())
            .isEqualTo(overlapped.getDistance() - newSection.getDistance());
    }

    @DisplayName("현재구간을 상행 방향으로 확장한다.")
    @Test
    void extendToUpDirectionTest() {
        // given
        Line line = createInitialLine();
        Station jamsil = new Station(1L, "jamsil");
        Station jamsilnaru = new Station(2L, "jamsilnaru");
        Station overlapStation = new Station(3L, "guui");

        Section upDirection = new Section(1L, line, jamsil, overlapStation, 5);
        Section base = new Section(2L, line, overlapStation, jamsilnaru, 10);

        // when
        Section extended = base.extendToUpDirection(upDirection);

        // then
        assertThat(extended.getId()).isEqualTo(base.getId());
        assertThat(extended.getLine()).isEqualTo(line);
        assertThat(extended.getUpStation()).isEqualTo(upDirection.getUpStation());
        assertThat(extended.getDownStation()).isEqualTo(base.getDownStation());
        assertThat(extended.getDistance())
            .isEqualTo(upDirection.getDistance() + base.getDistance());
    }

    private Line createInitialLine() {
        return new Line(1, "1호선", "blue");
    }
}
