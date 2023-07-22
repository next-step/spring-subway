package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SectionsTest {

    @Test
    @DisplayName("Sections를 정상적으로 생성한다.")
    void create() {
        /* given */
        final List<Section> sections = List.of(
                new Section(1L, 1L, 1L, 1L),
                new Section(2L, 2L, 2L, 2L),
                new Section(3L, 3L, 3L, 3L)
        );

        /* when & then */
        assertDoesNotThrow(() -> new Sections(sections));
    }

    @Test
    @DisplayName("Sections에 두 Section이 있는지 확인할 수 있다.")
    void containsBoth() {
        /* given */
        final Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 1L, 1L),
                new Section(2L, 2L, 2L, 2L, 2L),
                new Section(3L, 3L, 3L, 3L, 3L)
        ));

        /* when & then */
        assertThat(sections.containsBoth(2L, 3L)).isTrue();
        assertThat(sections.containsBoth(3L, 4L)).isFalse();
        assertThat(sections.containsBoth(4L, 5L)).isFalse();
    }

    @Test
    @DisplayName("Sections에 두 Section이 없는지 확인할 수 있다.")
    void containsNeither() {
        /* given */
        final Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 1L, 1L),
                new Section(2L, 2L, 2L, 2L, 2L),
                new Section(3L, 3L, 3L, 3L, 3L)
        ));

        /* when & then */
        assertThat(sections.containsNeither(4L, 5L)).isTrue();
        assertThat(sections.containsNeither(3L, 4L)).isFalse();
        assertThat(sections.containsNeither(2L, 3L)).isFalse();
    }

    @Test
    @DisplayName("Sections에 마지막 하행 종점을 찾을 수 있다.")
    void findLastPrevSection() {
        /* given */
        final Sections sections = new Sections(List.of(
                new Section(2L, 2L, 3L, 2L),
                new Section(3L, 3L, 4L, 3L)
        ));

        /* when */
        final Optional<Section> lastSection = sections.findLastSection();

        /* then */
        assertThat(lastSection).isPresent();
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
    @DisplayName("Sections의 종점인지 확인할 수 있다.")
    void isEndStation() {
        /* given */
        final Sections sections = new Sections(List.of(
                new Section(1L, 1L, 1L, 2L, 1L),
                new Section(2L, 1L, 2L, 3L, 2L),
                new Section(3L, 1L, 3L, 4L, 3L),
                new Section(4L, 1L, 4L, 5L, 4L)
        ));

        /* when & then */
        assertThat(sections.isEndStation(0L, 1L)).isTrue();
        assertThat(sections.isEndStation(5L, 6L)).isTrue();
        assertThat(sections.isEndStation(3L, 4L)).isFalse();
        assertThat(sections.isEndStation(7L, 8L)).isFalse();
    }
}
