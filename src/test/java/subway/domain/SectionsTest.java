package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.SectionRequest;

import java.util.ArrayList;
import java.util.Collections;
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
        List<Section> sortedSections = createSections();
        List<Section> sections = new ArrayList<>(sortedSections);
        Collections.shuffle(sections);

        // when
        List<Section> result = new Sections(sections).getSections();

        // then
        assertThat(result).isEqualTo(sortedSections);
    }

    @DisplayName("해당 구간이 추가 가능한 구간인지 검사한다.")
    @Test
    void validateSectionTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("5", "6", 10);
        Sections sections = new Sections(createSections());

        // when
        boolean result = sections.checkInsertion(sectionRequest.to(1L));

        // then
        assertThat(result).isTrue();
    }

    private List<Section> createSections() {
        List<Section> sections = new ArrayList<>();
        sections.add(new Section(1L, 1L, 4L, 3L, 10));
        sections.add(new Section(1L, 1L, 3L, 1L, 10));
        sections.add(new Section(1L, 1L, 1L, 2L, 10));
        sections.add(new Section(1L, 1L, 2L, 5L, 10));
        return sections;
    }
}
