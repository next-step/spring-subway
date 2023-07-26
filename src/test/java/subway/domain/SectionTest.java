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
    public static final Distance DISTANCE_777 = new Distance(777);

    @Test
    @DisplayName("구간을 정상적으로 생성한다.")
    void create() {
        /* given */

        /* when */
        Section section = new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE_777);

        /* when & then */
        assertThat(section.getUpStationId()).isEqualTo(UP_STATION_ID);
        assertThat(section.getDownStationId()).isEqualTo(DOWN_STATION_ID);
        assertThat(section.getDistance()).isEqualTo(DISTANCE_777);
    }

    @ParameterizedTest
    @CsvSource({",4", "2,", ","})
    @DisplayName("상행 역과 하행 역 중 하나 이상 없을 경우 구간 생성 시 SubwayException을 던진다.")
    void createFailWithoutUpStation(final Long upStationId, final Long downStationId) {
        /* given */

        /* when & then */
        assertThatThrownBy(() -> new Section(LINE_ID, upStationId, downStationId, DISTANCE_777))
                .isInstanceOf(SubwayException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("길이 정보가 0 이하일 경우 구간 생성 시 SubwayException을 던진다.")
    void distanceExceptionWithLessThanZero(final int value) {
        /* given */

        /* when & then */
        assertThatThrownBy(() -> new Section(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, new Distance(value)))
                .isInstanceOf(SubwayException.class);
    }

    @Test
    @DisplayName("구간에서 다른 구간을 뺄 수 있다.")
    void subtract() {
        /* given */
        final Section from = new Section(1L, 11L, 13L, DISTANCE_777);
        final Section to = new Section(1L, 11L, 12L, new Distance(700));

        /* when */
        final Section subtracted = from.subtract(to);

        /* then */
        assertThat(subtracted)
                .isEqualTo(new Section(1L, 12L, 13L, new Distance(77)));
    }
}
