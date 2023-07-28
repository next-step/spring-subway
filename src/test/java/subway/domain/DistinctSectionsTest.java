package subway.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.domain.builder.SectionBuilder;
import subway.exception.SubwayIllegalArgumentException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DistinctSectionsTest {

    final Section section1 = SectionBuilder.createSection(1L, 2L);
    final Section section2 = SectionBuilder.createSection(2L, 3L);
    final Section section3 = SectionBuilder.createSection(3L, 4L);

    DistinctSections distinctSections;

    @BeforeEach
    void setUp() {
        distinctSections = new DistinctSections(List.of(section1, section2, section3));
    }

    @Test
    @DisplayName("중복된 구간이 있는 경우 SubwayIllegalException을 던진다.")
    void createFailWithDuplicateSection() {
        /* given */
        final Section duplicate = SectionBuilder.createSection(3L, 4L);

        /* when & then */
        assertThatThrownBy(() -> new DistinctSections(List.of(section1, section2, section3, duplicate)))
                .isInstanceOf(SubwayIllegalArgumentException.class)
                .hasMessage("중복된 구간이 존재합니다.");
    }

}
