package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        Section section = new Section.Builder(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, DISTANCE).build();

        /* when & then */
        assertThat(section.getUpStationId()).isEqualTo(UP_STATION_ID);
        assertThat(section.getDownStationId()).isEqualTo(DOWN_STATION_ID);
        assertThat(section.getDistance()).isEqualTo(DISTANCE);
    }

    @ParameterizedTest
    @CsvSource({",4", "2,", ","})
    @DisplayName("상행 역과 하행 역 중 하나 이상 없을 경우 구간 생성 시 IllegalArgumentException을 던진다.")
    void createFailWithoutUpStation(final Long upStationId, final Long downStationId) {
        /* given */
        Section.Builder sectionBuilder = new Section.Builder(LINE_ID, upStationId, downStationId, DISTANCE);

        /* when & then */
        assertThatThrownBy(sectionBuilder::build).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("길이 정보가 없는 경우 구간 생성 시 IllegalArgumentException을 던진다.")
    void distanceExceptionWithNull() {
        /* given */
        Section.Builder sectionBuilder = new Section.Builder(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, null);

        /* when & then */
        assertThatThrownBy(sectionBuilder::build).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L})
    @DisplayName("길이 정보가 0 이하일 경우 구간 생성 시 IllegalArgumentException을 던진다.")
    void distanceExceptionWithLessThanZero(final Long distance) {
        /* given */
        Section.Builder sectionBuilder = new Section.Builder(LINE_ID, UP_STATION_ID, DOWN_STATION_ID, distance);

        /* when & then */
        assertThatThrownBy(sectionBuilder::build).isInstanceOf(IllegalArgumentException.class);
    }
}
