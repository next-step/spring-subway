package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SectionsTest {

    final static Long LINE_ID = 1L;
    final static Sections SECTIONS = new Sections(List.of(
            new Section(1L, LINE_ID, 1L, 2L, 1L),
            new Section(2L, LINE_ID, 2L, 3L, 2L),
            new Section(3L, LINE_ID, 3L, 4L, 3L),
            new Section(4L, LINE_ID, 4L, 5L, 4L)
    ));

    @Test
    @DisplayName("Sections를 정상적으로 생성한다.")
    void create() {
        /* given */
        final List<Section> sections = List.of(
                new Section(1L, 1L, 2L, 1L),
                new Section(2L, 2L, 3L, 2L),
                new Section(3L, 3L, 4L, 3L)
        );

        /* when & then */
        assertDoesNotThrow(() -> new Sections(sections));
    }

    @Test
    @DisplayName("Sections에 두 Section이 있는지 확인할 수 있다.")
    void containsBoth() {
        /* given */

        /* when & then */
        assertThat(SECTIONS.containsBoth(4L, 5L)).isTrue();
        assertThat(SECTIONS.containsBoth(5L, 6L)).isFalse();
        assertThat(SECTIONS.containsBoth(6L, 7L)).isFalse();
    }

    @Test
    @DisplayName("Sections에 마지막 하행 종점을 찾을 수 있다.")
    void findLastPrevSection() {
        /* given */

        /* when */
        final Optional<Section> lastSection = SECTIONS.findLastSection();

        /* then */
        assertThat(lastSection).isPresent();
        assertThat(lastSection.get()).isEqualTo(new Section(4L, 1L, 4L, 5L, 4L));
    }

    @Test
    @DisplayName("Section의 크기가 1인지 확인할 수 있다.")
    void isEqualSizeToOne() {
        /* given */
        final Sections sections = new Sections(List.of(
                new Section(2L, 2L, 3L, 2L)
        ));

        /* when & then */
        assertThat(sections.isEqualSizeToOne()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"0,1", "5,6"})
    @DisplayName("Sections의 종점인 경우 true를 반환한다.")
    void isEndStation(final Long upStationId, final Long downStationId) {
        /* given */

        /* when & then */
        assertThat(SECTIONS.isEndStation(upStationId, downStationId)).isTrue();
        assertThat(SECTIONS.isEndStation(upStationId, downStationId)).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"3,4", "7,8"})
    @DisplayName("Sections의 종점이 아닌 경우 false를 반환한다.")
    void isNotEndStation(final Long upStationId, final Long downStationId) {
        /* given */

        /* when & then */
        assertThat(SECTIONS.isEndStation(upStationId, downStationId)).isFalse();
        assertThat(SECTIONS.isEndStation(upStationId, downStationId)).isFalse();
    }
}
