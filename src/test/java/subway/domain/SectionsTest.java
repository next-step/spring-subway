package subway.domain;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.SectionRequest;
import subway.exception.IllegalSectionException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSectionsTest() {
        assertThatNoException()
                .isThrownBy(() -> new Sections(List.of(new Section(1L, 1L, 1L, 10))));
    }

    @DisplayName("해당 구간이 추가 가능한 구간인지 검증에 성공한다.")
    @Test
    void validateSectionTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("5", "6", 10);
        Sections sections = new Sections(createSections());

        // when & then
        assertThatNoException()
                .isThrownBy(() -> sections.updateBeforeInsert(sectionRequest.to(1L)));
    }

    @DisplayName("두 역이 모두 노선에 존재하는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailBothContainTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("1", "4", 10);
        Sections sections = new Sections(createSections());

        // when & then
        assertThatThrownBy(() -> sections.updateBeforeInsert(sectionRequest.to(1L)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("두 역이 모두 노선에 존재하지 않는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailNeitherContainTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("6", "7", 5);
        Sections sections = new Sections(createSections());

        // when & then
        assertThatThrownBy(() -> sections.updateBeforeInsert(sectionRequest.to(1L)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void validateSectionDistanceFailTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("1", "6", 10);
        Sections sections = new Sections(createSections());

        // when & then
        assertThatThrownBy(() -> sections.updateBeforeInsert(sectionRequest.to(1L)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 검증에 성공한다.")
    @Test
    void validateSectionDistanceTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("1", "6", 3);
        Sections sections = new Sections(createSections());

        // when & then
        assertThatNoException()
                .isThrownBy(() -> sections.updateBeforeInsert(sectionRequest.to(1L)));
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
