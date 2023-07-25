package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.SubwayException;

class SectionTest {

    private static final Long LINE_ID = 1L;
    private static final Long UP_STATION_ID = 1L;
    private static final Long DOWN_STATION_ID = 2L;
    private static final Long DISTANCE = 10L;

    @Test
    @DisplayName("구간을 정상적으로 생성한다.")
    void create() {
        /* given */

        /* when */
        Section section = new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

        /* when & then */
        assertThat(section.getUpStationId()).isEqualTo(UP_STATION_ID);
        assertThat(section.getDownStationId()).isEqualTo(DOWN_STATION_ID);
        assertThat(section.getDistance()).isEqualTo(DISTANCE);
    }

    @ParameterizedTest
    @CsvSource({",4", "2,", ","})
    @DisplayName("상행 역과 하행 역 중 하나 이상 없을 경우 구간 생성 시 SubwayException을 던진다.")
    void createFailWithoutUpStation(final Long upStationId, final Long downStationId) {
        /* given */

        /* when & then */
        assertThatThrownBy(() -> new Section(LINE_ID, upStationId, downStationId, DISTANCE)).isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("길이 정보가 없는 경우 구간 생성 시 SubwayException을 던진다.")
    void distanceExceptionWithNull() {
        /* given */

        /* when & then */
        assertThatThrownBy(() -> new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, null)).isInstanceOf(SubwayException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    @DisplayName("길이 정보가 0 이하일 경우 구간 생성 시 SubwayException을 던진다.")
    void distanceExceptionWithLessThanZero(final Long distance) {
        /* given */

        /* when & then */
        assertThatThrownBy(() -> new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, distance)).isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("상행역과 하행역이 같은 구간 생성 시 SubwayException을 던진다.")
    void distanceExceptionWithSameUpStationAndDownStation() {
        /* given */

        /* when & then */
        assertThatThrownBy(() -> new Section(LINE_ID, UP_STATION_ID, UP_STATION_ID, DISTANCE))
                .isInstanceOf(SubwayException.class);
        assertThatThrownBy(() -> new Section(LINE_ID, DOWN_STATION_ID, DOWN_STATION_ID, DISTANCE))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("구간의 상행역과 하행역에 특정 역이 있는지 확인할 수 있다.")
    void containsStation() {
        /* given */
        Section section = new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

        /* when & then */
        assertThat(section.containsStation(UP_STATION_ID)).isTrue();
        assertThat(section.containsStation(DOWN_STATION_ID)).isTrue();
        assertThat(section.containsStation(123L)).isFalse();
    }

    @Test
    @DisplayName("구간의 하행역이 특정 역과 같은 지 확인할 수 있다.")
    void isSameDownStationId() {
        /* given */
        Section section = new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);

        /* when & then */
        assertThat(section.isSameDownStationId(DOWN_STATION_ID)).isTrue();
        assertThat(section.isSameDownStationId(UP_STATION_ID)).isFalse();
        assertThat(section.isSameUpStationId(DOWN_STATION_ID)).isFalse();
        assertThat(section.isSameUpStationId(UP_STATION_ID)).isTrue();
    }

    @Test
    @DisplayName("구간에 역이 중간에 추가 되는 경우 잘리는 구간을 반환한다.(추가되는 구간의 상행역이 같은 경우)")
    void subtractWithSameUpStationId() {
        final Long newDistance = 6L;
        final Long newDownStationId = 3L;
        final Section section = new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);
        final Section requestSection = new Section(LINE_ID, UP_STATION_ID, newDownStationId, newDistance);

        final Section subtract = section.subtract(requestSection);
        assertThat(subtract.getDistance()).isEqualTo(DISTANCE - newDistance);
        assertThat(subtract.getUpStationId()).isEqualTo(newDownStationId);
        assertThat(subtract.getDownStationId()).isEqualTo(DOWN_STATION_ID);
    }

    @Test
    @DisplayName("구간에 역이 중간에 추가 되는 경우 잘리는 구간을 반환한다.(추가되는 구간의 하행역이 같은 경우)")
    void subtractWithSameDownStationId() {
        final Long newDistance = 6L;
        final Long newUpStationId = 3L;
        final Section section = new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);
        final Section requestSection = new Section(LINE_ID, newUpStationId, DOWN_STATION_ID, newDistance);

        final Section subtract = section.subtract(requestSection);
        assertThat(subtract.getDistance()).isEqualTo(DISTANCE - newDistance);
        assertThat(subtract.getUpStationId()).isEqualTo(UP_STATION_ID);
        assertThat(subtract.getDownStationId()).isEqualTo(newUpStationId);
    }

    @Test
    @DisplayName("구간에 역이 중간에 삭제 되는 경우 합쳐지는 구간을 반환한다.")
    void merge() {
        final Long newDownStationId = 3L;
        final Section nextSection = new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE);
        final Section prevSection = new Section(LINE_ID, DOWN_STATION_ID, newDownStationId, DISTANCE);

        final Section merge = nextSection.merge(prevSection);
        assertThat(merge.getDistance()).isEqualTo(DISTANCE + DISTANCE);
        assertThat(merge.getUpStationId()).isEqualTo(UP_STATION_ID);
        assertThat(merge.getDownStationId()).isEqualTo(newDownStationId);
    }
}
