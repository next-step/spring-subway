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
        List<Section> sections = List.of(
                new Section.Builder(1L, 1L, 1L, 1L).build(),
                new Section.Builder(2L, 2L, 2L, 2L).build(),
                new Section.Builder(3L, 3L, 3L, 3L).build()
        );

        /* when & then */
        assertDoesNotThrow(() -> new Sections(sections));
    }

    @Test
    @DisplayName("Sections에 특정 Station이 있는지 확인할 수 있다.")
    void containsStation() {
        /* given */
        Sections sections = new Sections(List.of(
                new Section.Builder(1L, 1L, 1L, 1L).build(),
                new Section.Builder(2L, 2L, 2L, 2L).build(),
                new Section.Builder(3L, 3L, 3L, 3L).build()
        ));

        /* when & then */
        assertThat(sections.containsStation(3L)).isTrue();
        assertThat(sections.containsStation(1234L)).isFalse();
    }

    @Test
    @DisplayName("Sections에 마지막 하행 종점을 찾을 수 있다.")
    void findLastPrevSection() {
        /* given */
        Sections sections = new Sections(List.of(
                new Section.Builder(2L, 2L, 2L, 2L)
                        .prevSectionId(1234L)
                        .build(),
                new Section.Builder(3L, 3L, 3L, 3L)
                        .prevSectionId(null)
                        .build()
        ));

        /* when */
        Optional<Section> lastPrevSection = sections.findLastPrevSection();

        /* then */
        assertThat(lastPrevSection).isPresent();
    }
}