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
    @DisplayName("구간에서 다른 구간을 뺄 수 있다.")
    void subtract() {
        /* given */
        final Section from = new Section(1L, 11L, 13L, 777L);
        final Section to = new Section(1L, 11L, 12L, 700L);

        /* when */
        final Section subtracted = from.subtract(to);

        /* then */
        assertThat(subtracted).isEqualTo(new Section(1L, 12L, 13L, 77L));
    }
}
