package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.SectionDao;
import subway.domain.Section;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.IllegalSectionException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("구간 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @InjectMocks
    private SectionService sectionService;

    @Mock private SectionDao sectionDao;

    @DisplayName("구간 생성 성공")
    @Test
    void createSection() {
        // given
        final Section lastSection = new Section(1L, 1L,  1L, 2L,10);
        final SectionRequest sectionRequest = new SectionRequest( "2", "3",10);
        final Section newSection = new Section(2L, 1L,  2L, 3L,10);

        given(sectionDao.findLastSection(1L)).willReturn(Optional.of(lastSection));
        given(sectionDao.findByLineIdAndStationId(1L, 3L)).willReturn(Optional.empty());
        given(sectionDao.insert(any(Section.class))).willReturn(newSection);

        // when
        final SectionResponse sectionResponse = sectionService.saveSection(1L, sectionRequest);

        // then
        assertThat(sectionResponse.getId()).isNotNull();
        assertThat(sectionResponse.getLineId()).isNotNull();
        assertThat(sectionResponse.getUpStationId()).isEqualTo(2);
        assertThat(sectionResponse.getDownStationId()).isEqualTo(3);
        assertThat(sectionResponse.getDistance()).isEqualTo(10);
    }

    @DisplayName("존재하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // given
        final long lineId = 3L;
        final SectionRequest sectionRequest = new SectionRequest( "1", "2",10);

        given(sectionDao.findLastSection(lineId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(lineId, sectionRequest))
                .describedAs("해당 노선은 생성되지 않았습니다.")
                .isInstanceOf(IllegalSectionException.class);

    }

    @DisplayName("새로운 구간의 상행 역이 해당 노선의 하행 종점역이 아니어서 구간 생성 실패")
    @Test
    void createSectionWithNotEndStation() {
        // given
        final Section lastSection = new Section(1L, 1L, 1L, 2L,  10);
        final SectionRequest sectionRequest = new SectionRequest( "3", "1",10);

        given(sectionDao.findLastSection(1L)).willReturn(Optional.of(lastSection));

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(1L, sectionRequest))
                .describedAs("새로운 구간의 상행 역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.")
                .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("새로운 구간의 하행 역이 해당 노선에 등록되어있는 역이어서 구간 생성 실패")
    @Test
    void createSectionWithDuplicateStationInLine() {
        // given
        final Section lastSection = new Section(1L, 1L,  1L, 2L,10);
        final SectionRequest sectionRequest = new SectionRequest( "2", "1",10);

        given(sectionDao.findLastSection(1L)).willReturn(Optional.of(lastSection));
        given(sectionDao.findByLineIdAndStationId(1L, 1L)).willReturn(Optional.of(lastSection));

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(1L, sectionRequest))
                .describedAs("새로운 구간의 하행 역은 해당 노선에 등록되어있는 역일 수 없습니다.")
                .isInstanceOf(IllegalSectionException.class);
    }

    @DisplayName("구간 삭제 성공")
    @Test
    void deleteSection() {
        // given
        final long stationId = 3;

        // when & then
        assertThatNoException().isThrownBy(() -> sectionService.deleteSection(1L, stationId));
    }

    @DisplayName("지하철 노선의 하행 종점역이 아닐 때 구간 제거 실패")
    @Test
    void deleteSectionWithNotLastStation() {
        // given
        final long lineId = 1L;
        final long stationId = 1L;

        given(sectionDao.findLastSection(lineId))
                .willReturn(Optional.of(new Section(lineId, 2L, 3L, 10)));

        // when & then
        assertThatThrownBy(() -> sectionService.deleteSection(lineId, stationId))
                .describedAs("")
                .isInstanceOf(IllegalSectionException.class);
    }
}
