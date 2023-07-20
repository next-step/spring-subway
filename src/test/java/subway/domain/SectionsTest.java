package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.SectionRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSectionsTest() {
        assertThatNoException()
                .isThrownBy(() -> new Sections(List.of(new Section(1L, 1L, 1L, 10))));
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

    @DisplayName("두 역이 모두 노선에 존재하는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailBothContainTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("1", "4", 10);
        Sections sections = new Sections(createSections());

        // when
        boolean result = sections.checkInsertion(sectionRequest.to(1L));

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("두 역이 모두 노선에 존재하지 않는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailNeitherContainTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("1", "4", 10);
        Sections sections = new Sections(createSections());

        // when
        boolean result = sections.checkInsertion(sectionRequest.to(1L));

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void validateSectionDistanceFailTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("1", "6", 10);
        Sections sections = new Sections(createSections());

        // when
        boolean result = sections.checkInsertion(sectionRequest.to(1L));

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 한다.")
    @Test
    void validateSectionDistanceTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("1", "6", 3);
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
