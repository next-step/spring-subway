package subway.application;

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
        final SectionRequest sectionRequest = new SectionRequest("2", "1", 10.0);
        final Section section = new Section(1L, 1L, 2L, 1L, 10.0);

        given(sectionDao.insert(any(Section.class))).willReturn(section);
        given(sectionDao.findByDownStationIdAndLineId(1L, 1L))
                .willReturn(Optional.of(section));

        // when
        final SectionResponse sectionResponse = sectionService.saveSection(1L, sectionRequest);

        // then
        assertThat(sectionResponse.getId()).isNotNull();
        assertThat(sectionResponse.getLineId()).isNotNull();
        assertThat(sectionResponse.getDownStationId()).isEqualTo(2);
        assertThat(sectionResponse.getUpStationId()).isEqualTo(1);
        assertThat(sectionResponse.getDistance()).isEqualTo(10, withPrecision(1d));
    }

    @DisplayName("일치하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // given
        final Long lineId = 3L;
        final SectionRequest sectionRequest = new SectionRequest("2", "1", 10.0);

        given(sectionDao.findByDownStationIdAndLineId(lineId, 1L))
                .willReturn(Optional.empty());

        // when
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sectionService.saveSection(lineId, sectionRequest));
    }

    @DisplayName("새로운 구간의 상행 역이 해당 노선의 하행 종점역이 아니어서 구간 생성 실패")
    @Test
    void createSectionWithNotEndStation() {
        // given
        final Section section = new Section(1L, 1L, 2L, 1L, 10.0);
        final SectionRequest sectionRequest = new SectionRequest("2", "1", 10.0);

        given(sectionDao.findByDownStationIdAndLineId(1L, 1L))
                .willReturn(Optional.of(section));
        given(sectionDao.findByUpStationIdAndLineId(1L, 1L))
                .willReturn(Optional.of(section));

        // when
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sectionService.saveSection(1L, sectionRequest));
    }
}
