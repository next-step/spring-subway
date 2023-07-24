package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        final Section lastSection = SECTIONS.getLastSection();

        /* then */
        assertThat(lastSection).isEqualTo(new Section(4L, 1L, 4L, 5L, 4L));
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

    @Test
    @DisplayName("현재 구간들 중 상행 종점역인지 확인한다.")
    void isFirstStation() {
        assertThat(SECTIONS.isFirstStation(1L)).isTrue();
        assertThat(SECTIONS.isFirstStation(5L)).isFalse();
    }

    @Test
    @DisplayName("현재 구간들 중 하행 종점역인지 확인한다.")
    void isLastStation() {
        assertThat(SECTIONS.isLastStation(5L)).isTrue();
        assertThat(SECTIONS.isLastStation(1L)).isFalse();
    }

}
