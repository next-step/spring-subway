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
        long upStationId = 2L;
        long downStationId = 4L;
        int distance = 10;

        // when & then
        assertThatNoException().isThrownBy(
            () -> new Section(line, upStationId, downStationId, distance));
    }

    @DisplayName("길이 유효성 검증 테스트")
    @Test
    void validateDistanceTest() {
        // given
        Line line = createInitialLine();
        long upStationId = 2L;
        long downStationId = 4L;
        int invalidDistance = 0;

        // when & then
        assertThatThrownBy(() -> new Section(line, upStationId, downStationId, invalidDistance))
            .hasMessage("구간 길이는 0보다 커야한다.")
            .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("현재구간을 상행 방향으로 축소한다.")
    @Test
    void narrowToUpDirectionTest() {
        // given
        Line line = createInitialLine();
        long overlapStationId = 1L;
        Section upDirection = new Section(1L, line, overlapStationId, 2L, 6);
        Section base = new Section(2L, line, overlapStationId, 3L, 10);

        // when
        Section narrow = base.narrowToUpDirection(upDirection);

        // then
        assertThat(narrow.getId()).isEqualTo(base.getId());
        assertThat(narrow.getLine()).isEqualTo(line);
        assertThat(narrow.getUpStationId()).isEqualTo(upDirection.getDownStationId());
        assertThat(narrow.getDownStationId()).isEqualTo(base.getDownStationId());
        assertThat(narrow.getDistance()).isEqualTo(base.getDistance() - upDirection.getDistance());
    }

    @DisplayName("현재구간을 하행 방향으로 축소한다.")
    @Test
    void narrowToDownDirectionTest() {
        // given
        long overlapStationId = 3L;
        Line line = createInitialLine();
        Section base = new Section(1L, line, 1L, overlapStationId, 10);
        Section downDirection = new Section(1L, line, 2L, overlapStationId, 6);

        // when
        Section narrowed = base.narrowToDownDirection(downDirection);

        // then
        assertThat(narrowed.getId()).isEqualTo(base.getId());
        assertThat(narrowed.getLine()).isEqualTo(line);
        assertThat(narrowed.getUpStationId()).isEqualTo(base.getUpStationId());
        assertThat(narrowed.getDownStationId()).isEqualTo(downDirection.getUpStationId());
        assertThat(narrowed.getDistance())
            .isEqualTo(base.getDistance() - downDirection.getDistance());
    }

    @DisplayName("현재구간을 상행 방향으로 확장한다.")
    @Test
    void extendToUpDirectionTest() {
        // given
        long overlapStationId = 2L;
        Line line = createInitialLine();
        Section upDirection = new Section(1L, line, 1L, overlapStationId, 5);
        Section base = new Section(2L, line, overlapStationId, 3L, 10);

        // when
        Section extended = base.extendToUpDirection(upDirection);

        // then
        assertThat(extended.getId()).isEqualTo(base.getId());
        assertThat(extended.getLine()).isEqualTo(line);
        assertThat(extended.getUpStationId()).isEqualTo(upDirection.getUpStationId());
        assertThat(extended.getDownStationId()).isEqualTo(base.getDownStationId());
        assertThat(extended.getDistance())
            .isEqualTo(upDirection.getDistance() + base.getDistance());
    }

    private Line createInitialLine() {
        return new Line(1, "1호선", "blue");
    }
}
