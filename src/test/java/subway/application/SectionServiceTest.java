package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.SectionDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.IllegalLineException;
import subway.exception.IllegalSectionException;

@DisplayName("구간 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private SectionDao sectionDao;

    @DisplayName("구간 생성 성공")
    @Test
    void createSectionTest() {
        // given
        final SectionRequest sectionRequest = new SectionRequest("2", "3", 10);
        final Line line = createInitialLine();
        final Section newSection = new Section(2L, line, 2L, 3L, 10);
        final Section oldSection = new Section(1L, line, 1L, 2L, 10);

        given(sectionDao.findAll(1L)).willReturn(List.of(oldSection));
        given(sectionDao.insert(any(Section.class))).willReturn(newSection);

        // when
        final SectionResponse sectionResponse = sectionService.saveSection(1L, sectionRequest);

        // then
        assertThat(sectionResponse.getUpStationId()).isEqualTo(2);
        assertThat(sectionResponse.getDownStationId()).isEqualTo(3);
        assertThat(sectionResponse.getDistance()).isEqualTo(10);
    }

    @DisplayName("존재하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineIdTest() {
        // given
        final long lineId = 3L;
        final SectionRequest sectionRequest = new SectionRequest("1", "2", 10);

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(lineId, sectionRequest))
            .hasMessage("해당 노선은 생성되지 않았습니다.")
            .isInstanceOf(IllegalLineException.class);

    }

    @DisplayName("새로운 구간의 상행 및 하행 역이 모두 해당 노선에 등록되어있는 역이어서 구간 생성 실패")
    @Test
    void createSectionWithBothExistInLineTest() {
        // given
        final Line line = createInitialLine();
        final Section oldSection = new Section(1L, line, 1L, 2L, 10);
        final SectionRequest sectionRequest = new SectionRequest("1", "2", 10);

        given(sectionDao.findAll(1L)).willReturn(List.of(oldSection));

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(1L, sectionRequest))
            .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.")
            .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("새로운 구간의 상행 및 하행 역이 모두 해당 노선에 등록되지 않아서 구간 생성 실패")
    @Test
    void createSectionWithBothNotExistInLineTest() {
        // given
        final Line line = createInitialLine();
        final Section oldSection = new Section(1L, line, 1L, 2L, 10);
        final SectionRequest sectionRequest = new SectionRequest("3", "4", 10);

        given(sectionDao.findAll(1L)).willReturn(List.of(oldSection));

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(1L, sectionRequest))
            .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.")
            .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("존재하지 않는 노선으로 인한 구간 삭제 실패")
    @Test
    void deleteSectionWithUnmatchedLineIdTest() {
        // given
        final long lineId = 3L;
        final long stationId = 2L;

        given(sectionDao.existByLineIdAndStationId(lineId, stationId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> sectionService.deleteSection(lineId, stationId))
            .hasMessage("해당 역은 노선에 존재하지 않습니다.")
            .isInstanceOf(IllegalSectionException.class);

    }

    @DisplayName("지하철 노선에 구간이 1개일 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineHasOneSectionTest() {
        // given
        final long lineId = 1L;
        final long stationId = 2L;

        given(sectionDao.existByLineIdAndStationId(lineId, stationId)).willReturn(true);
        given(sectionDao.count(lineId)).willReturn(1L);

        // when & then
        assertThatThrownBy(() -> sectionService.deleteSection(lineId, stationId))
            .hasMessage("해당 노선은 구간이 한개입니다.")
            .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("지하철 노선의 상행 종점역 제거 성공")
    @Test
    void deleteEndSectionTest() {
        // given
        final List<Section> sections = createSections();
        final Section deleteTarget = sections.get(0);
        final long lineId = deleteTarget.getLine().getId();
        final long startStationId = deleteTarget.getUpStationId();

        given(sectionDao.existByLineIdAndStationId(lineId, startStationId)).willReturn(true);
        given(sectionDao.count(lineId)).willReturn(3L);
        given(sectionDao.findAll(lineId)).willReturn(sections);
        doNothing().when(sectionDao).delete(deleteTarget.getId());

        // when & then
        assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(lineId, startStationId));

        verify(sectionDao).delete(anyLong());
        verify(sectionDao, never()).update(any());
    }

    @DisplayName("지하철 노선의 중간 역 삭제 성공")
    @Test
    void deleteMiddleSectionTest() {
        // given
        final List<Section> sections = createSections();
        final Section deleteTarget = sections.get(0);
        final Section updateTarget = sections.get(1);

        final long lineId = deleteTarget.getLine().getId();
        final long deleteStationId = deleteTarget.getDownStationId();
        final Section updatedResult = createUpdateResult(deleteTarget, updateTarget);

        given(sectionDao.existByLineIdAndStationId(lineId, deleteStationId)).willReturn(true);
        given(sectionDao.count(lineId)).willReturn((long) sections.size());
        given(sectionDao.findAll(lineId)).willReturn(sections);
        doNothing().when(sectionDao).delete(deleteTarget.getId());
        doNothing().when(sectionDao).update(updatedResult);

        // when & then
        assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(lineId, deleteStationId));

        verify(sectionDao).delete(deleteTarget.getId());
        verify(sectionDao).update(updatedResult);
    }

    private Line createInitialLine() {
        return new Line(1L, "1호선", "blue");
    }

    private List<Section> createSections() {
        final Line line = createInitialLine();
        final Section firstSection = new Section(1L, line, 1L, 2L, 10);
        final Section secondSection = new Section(2L, line, 2L, 3L, 10);
        final Section thirdSection = new Section(3L, line, 3L, 4L, 10);
        return Arrays.asList(firstSection, secondSection, thirdSection);
    }

    private Section createUpdateResult(final Section deleteTarget, final Section updateTarget) {
        return new Section(updateTarget.getId(), updateTarget.getLine(),
            deleteTarget.getUpStationId(), updateTarget.getDownStationId(),
            deleteTarget.getDistance() + updateTarget.getDistance());
    }
}
