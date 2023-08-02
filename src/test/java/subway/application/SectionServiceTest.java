package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.SectionRequest;
import subway.dto.SectionResponse;
import subway.exception.SubwayException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("구간 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private LineDao lineDao;

    @Mock
    private StationDao stationDao;

    @DisplayName("구간 생성 성공")
    @Test
    void createSection() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Station station3 = new Station(3L, "한대앞");
        final Section newSection = new Section(2L, line, station2, station3, 10);
        final Section oldSection = new Section(1L, line, station1, station2, 10);

        given(lineDao.findById(line.getId())).willReturn(Optional.of(line));
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(oldSection));
        given(stationDao.findById(station2.getId())).willReturn(Optional.of(station2));
        given(stationDao.findById(station3.getId())).willReturn(Optional.of(station3));
        given(sectionDao.insert(any(Section.class))).willReturn(newSection);

        // when
        final SectionRequest sectionRequest = new SectionRequest("2", "3", 10);
        final SectionResponse sectionResponse = sectionService.saveSection(1L, sectionRequest);

        // then
        assertThat(sectionResponse.getUpStationId()).isEqualTo(2);
        assertThat(sectionResponse.getDownStationId()).isEqualTo(3);
        assertThat(sectionResponse.getDistance()).isEqualTo(10);
    }

    @DisplayName("존재하지 않는 노선으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // given
        final long lineId = 3L;
        final SectionRequest sectionRequest = new SectionRequest("1", "2", 10);

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(lineId, sectionRequest))
                .hasMessage("해당 노선은 생성되지 않았습니다.")
                .isInstanceOf(SubwayException.class);

    }

    @DisplayName("새로운 구간의 상행 및 하행 역이 모두 해당 노선에 등록되어있는 역이어서 구간 생성 실패")
    @Test
    void createSectionWithBothExistInLine() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Section oldSection = new Section(1L, line, station1, station2, 10);

        given(lineDao.findById(line.getId())).willReturn(Optional.of(line));
        given(stationDao.findById(station1.getId())).willReturn(Optional.of(station1));
        given(stationDao.findById(station2.getId())).willReturn(Optional.of(station2));
        given(sectionDao.findAllByLineId(1L)).willReturn(List.of(oldSection));

        // when & then
        final SectionRequest sectionRequest = new SectionRequest("1", "2", 10);
        assertThatThrownBy(() -> sectionService.saveSection(1L, sectionRequest))
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("새로운 구간의 상행 및 하행 역이 모두 해당 노선에 등록되지 않아서 구간 생성 실패")
    @Test
    void createSectionWithBothNotExistInLine() {
        // given
        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Section oldSection = new Section(1L, line, station1, station2, 10);
        final SectionRequest sectionRequest = new SectionRequest("3", "4", 10);

        given(sectionDao.findAllByLineId(line.getId())).willReturn(List.of(oldSection));
        given(lineDao.findById(line.getId())).willReturn(Optional.of(line));
        given(stationDao.findById(sectionRequest.getUpStationId())).willReturn(Optional.of(new Station(3L, "한대앞")));
        given(stationDao.findById(sectionRequest.getDownStationId())).willReturn(Optional.of(new Station(4L, "중앙")));

        // when & then
        assertThatThrownBy(() -> sectionService.saveSection(line.getId(), sectionRequest))
                .hasMessage("상행역과 하행역 중 하나만 노선에 등록되어 있어야 합니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("하행 마지막 구간 삭제 성공")
    @Test
    void deleteLastSection() {
        // given
        final long stationId = 3;

        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Station station3 = new Station(3L, "한대앞");
        final Section section1 = new Section(line, station1, station2, 10);
        final Section section2 = new Section(line, station2, station3, 10);

        given(sectionDao.findAllByLineId(line.getId())).willReturn(List.of(section1, section2));

        // when & then
        assertThatNoException().isThrownBy(() -> sectionService.deleteSection(line.getId(), stationId));
    }

    @DisplayName("하행 마지막 구간이 아닌 구간 삭제 성공")
    @Test
    void deleteAnySectionNotLast() {
        // given
        final long stationId = 2;

        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Station station3 = new Station(3L, "한대앞");
        final Section section1 = new Section(line, station1, station2, 10);
        final Section section2 = new Section(line, station2, station3, 10);

        given(sectionDao.findAllByLineId(line.getId())).willReturn(List.of(section1, section2));

        // when & then
        assertThatNoException().isThrownBy(() -> sectionService.deleteSection(line.getId(), stationId));
    }

    @DisplayName("지하철 노선에 구간이 1개일 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineHasOneSection() {
        // given
        final long stationId = 2;

        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Section section1 = new Section(line, station1, station2, 10);

        given(sectionDao.findAllByLineId(line.getId())).willReturn(List.of(section1));

        // when & then
        assertThatThrownBy(() -> sectionService.deleteSection(line.getId(), stationId))
                .hasMessage("노선에 구간이 최소 2개가 있어야 삭제가 가능합니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("지하철 노선에 삭제할 역이 존재하지 않아 구간 제거 실패")
    @Test
    void deleteSectionNotInLine() {
        // given
        final long stationId = 4;

        final Line line = new Line(1L, "4호선", "blue");
        final Station station1 = new Station(1L, "오이도");
        final Station station2 = new Station(2L, "안산");
        final Station station3 = new Station(3L, "한대앞");
        final Section section1 = new Section(line, station1, station2, 10);
        final Section section2 = new Section(line, station2, station3, 10);

        given(sectionDao.findAllByLineId(line.getId())).willReturn(List.of(section1, section2));

        // when & then
        assertThatThrownBy(() -> sectionService.deleteSection(line.getId(), stationId))
                .hasMessage("해당 노선에 삭제할 역이 존재하지 않습니다.")
                .isInstanceOf(SubwayException.class);
    }
}
