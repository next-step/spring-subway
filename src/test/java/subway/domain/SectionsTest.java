package subway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SectionsTest {

    @DisplayName("빈 리스트로 Sections 생성시 빈 리스트 반환")
    @Test
    void emptySectionsThenEmptyStation() {
        // given
        List<Section> sectionList = List.of();

        // when
        Sections sections = new Sections(sectionList);

        // then
        assertThat(sections.toStations()).isEmpty();
    }

    
}
