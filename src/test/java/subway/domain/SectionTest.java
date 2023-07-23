package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.SubwayException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SectionTest {

    public static final Long LINE_ID = 1L;
    public static final Long UP_STATION_ID = 1L;
    public static final Long DOWN_STATION_ID = 2L;
    public static final Long DISTANCE = 10L;

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
    }
}
