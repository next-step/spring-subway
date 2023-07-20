package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSectionsTest() {
        assertThatNoException()
                .isThrownBy(() -> new Sections(List.of()));
    }

    @DisplayName("Sections 정렬에 성공한다.")
    @Test
    void createSectionsSortingTest() {
        // given
        Section section1 = new Section(1L, 1L, 4L, 3L, 10);
        Section section2 = new Section(1L, 1L, 3L, 1L, 10);
        Section section3 = new Section(1L, 1L, 1L, 2L, 10);
        Section section4 = new Section(1L, 1L, 2L, 5L, 10);

        List<Section> sections = List.of(section2, section4, section1, section3);
        List<Section> sortedSections = List.of(section1, section2, section3, section4);

        // when
        List<Section> result = new Sections(sections).getSections();

        // then
        assertThat(result).isSameAs(sortedSections);
    }
}
