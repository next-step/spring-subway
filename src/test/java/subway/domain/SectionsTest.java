package subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.SectionRequest;
import subway.exception.IllegalSectionException;
import subway.exception.IllegalStationsException;

class SectionsTest {

    @DisplayName("Sections 생성에 성공한다.")
    @Test
    void createSectionsTest() {
        // given
        List<Section> sections = createSections();

        // when & then
        assertThatNoException().isThrownBy(() -> new Sections(sections));
    }

    @DisplayName("해당 구간이 추가 가능한 구간인지 검증에 성공한다.")
    @Test
    void validateSectionTest() {
        // given
        SectionRequest sectionRequest = new SectionRequest("5", "6", 10);
        List<Section> sectionList = createSections();
        Sections sections = new Sections(sectionList);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> sections.findConnectedSection(sectionRequest.to(1L)));
    }

    @DisplayName("두 역이 모두 노선에 존재하는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailBothContainTest() {
        // given
        Sections sections = new Sections(createSections());

        String duplicateUpStationId = "1";
        String duplicateDownStationId ="4";
        SectionRequest duplicateRequest = new SectionRequest(
            duplicateUpStationId, duplicateDownStationId, 10
        );

        // when & then
        assertThatThrownBy(() -> sections.findConnectedSection(duplicateRequest.to(1L)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("두 역이 모두 노선에 존재하지 않는 경우 검증에 실패한다.")
    @Test
    void validateSectionFailNeitherContainTest() {
        // given
        Sections sections = new Sections(createSections());

        String notExistUpStationId = "6";
        String notExistDownStationId = "7";
        SectionRequest request = new SectionRequest(notExistUpStationId, notExistDownStationId, 5);

        // when & then
        assertThatThrownBy(() -> sections.findConnectedSection(request.to(1L)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다.")
    @Test
    void validateSectionDistanceFailTest() {
        // given
        Sections sections = new Sections(createSections());

        int invalidDistance = 10;
        SectionRequest sectionRequest = new SectionRequest("1", "6", invalidDistance);

        // when & then
        assertThatThrownBy(() -> sections.findConnectedSection(sectionRequest.to(1L)))
                .isInstanceOf(IllegalSectionException.class)
                .hasMessage("길이는 기존 역 사이 길이보다 크거나 같을 수 없습니다.");
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 작아야 검증에 성공한다.")
    @Test
    void validateSectionDistanceTest() {
        // given
        Sections sections = new Sections(createSections());

        int validDistance = 3;
        SectionRequest sectionRequest = new SectionRequest("1", "6", validDistance);

        // when & then
        assertThatNoException()
                .isThrownBy(() -> sections.findConnectedSection(sectionRequest.to(1L)));
    }

    @DisplayName("식별자와 일치하는 종점역을 반환한다.")
    @Test
    void getLastSectionTest() {
        // given
        List<Section> sectionList = createSections();
        Sections sections = new Sections(createSections());
        long startStationId = sectionList.get(0).getUpStationId();

        // when
        Section startSection = sections.getLastSection(startStationId);

        // then
        assertThat(startSection.getUpStationId()).isEqualTo(startStationId);
    }

    @DisplayName("식별자와 일치하는 종점역이 없으면 예외를 던진다.")
    @Test
    void getLastSectionNotExistExceptionTest() {
        // given
        List<Section> sectionList = createSections();
        Sections sections = new Sections(createSections());
        long innerStationId = sectionList.get(2).getUpStationId();

        // when & then
        assertThatThrownBy(() -> sections.getLastSection(innerStationId))
            .hasMessage("종점 구간이 포함된 역이 아닙니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @DisplayName("역과 상행 방향으로 연결된 구간을 반환한다.")
    @Test
    void findUpDirectionSectionTest() {
        // given
        List<Section> sectionList = createSections();
        Sections sections = new Sections(createSections());

        Section upDirection = sectionList.get(2);
        long stationId = upDirection.getDownStationId();

        // when
        Section result = sections.findUpDirectionSection(stationId);

        // then
        assertThat(result).isEqualTo(upDirection);
    }

    @DisplayName("역과 하행 방향으로 연결된 구간을 반환한다.")
    @Test
    void findDownDirectionSectionTest() {
        // given
        List<Section> sectionList = createSections();
        Sections sections = new Sections(createSections());

        Section downDirection = sectionList.get(2);
        long stationId = downDirection.getUpStationId();

        // when
        Section result = sections.findDownDirectionSection(stationId);

        // then
        assertThat(result).isEqualTo(downDirection);
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
