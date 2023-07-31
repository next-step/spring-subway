package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import subway.exception.SubwayException;

import java.util.HashSet;
import java.util.Set;

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

    @Test
    @DisplayName("다른 구간 앞에 연결된 구간인지 확인한다.")
    void isConnectedForward() {
        /* given */
        final Section from11To12 = new Section(1L, 11L, 12L, DISTANCE_777);
        final Section from12To13 = new Section(1L, 12L, 13L, DISTANCE_777);
        final Section from11To14 = new Section(1L, 11L, 14L, DISTANCE_777);

        /* when & then */
        assertThat(from11To12.isConnectedForward(from12To13)).isTrue();
        assertThat(from11To12.isConnectedForward(from11To14)).isFalse();
    }

    @Test
    @DisplayName("다른 구간 뒤에 연결된 구간인지 확인한다.")
    void isHead() {
        /* given */
        final Section from11To12 = new Section(1L, 11L, 12L, DISTANCE_777);
        final Section from12To13 = new Section(1L, 12L, 13L, DISTANCE_777);
        final Section from11To14 = new Section(1L, 11L, 14L, DISTANCE_777);

        /* when & then */
        assertThat(from12To13.isConnectedBack(from11To12)).isTrue();
        assertThat(from12To13.isConnectedBack(from11To14)).isFalse();
    }

    @Test
    @DisplayName("연결된 두 구간을 하나의 구간으로 합칠 수 있다.")
    void merge() {
        /* given */
        final Section from11To12 = new Section(1L, 11L, 12L, DISTANCE_777);
        final Section from12To13 = new Section(1L, 12L, 13L, DISTANCE_777);

        /* when */
        final Section from11To13 = from11To12.merge(from12To13);

        /* then */
        assertThat(from11To13)
                .isEqualTo(new Section(1L, 11L, 13L, new Distance(777 * 2)));
    }

    @Test
    @DisplayName("연결되지 않은 두 구간을 합칠 시 SubwayIllegalException을 던진다.")
    void mergeFailWithNotConnected() {
        /* given */
        final Section from11To14 = new Section(1L, 11L, 14L, DISTANCE_777);
        final Section from12To15 = new Section(1L, 12L, 15L, DISTANCE_777);

        /* when & then */
        assertThatThrownBy(() -> from11To14.merge(from12To15)).isInstanceOf(SubwayException.class)
                .hasMessage("구간이 연결되어있지 않습니다. 입력 구간: (11, 14), (12, 15)");
    }

    @Test
    @DisplayName("모든 값이 같으면 같은 구간이다.")
    void sameSection() {
        /* given */
        final Section from = new Section(1L, 2L, 3L, 4L, DISTANCE_777);
        final Section to = new Section(1L, 2L, 3L, 4L, DISTANCE_777);

        /* when */
        final Set<Section> sections = new HashSet<>();
        sections.add(from);
        sections.add(to);

        /* then */
        assertThat(from).isEqualTo(to);
        assertThat(sections).hasSize(1);
    }
}
