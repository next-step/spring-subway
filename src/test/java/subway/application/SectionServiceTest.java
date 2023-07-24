package subway.application;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
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
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionAdditionRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("SectionService 단위 테스트")
class SectionServiceTest {

    @InjectMocks
    SectionService sectionService;
    @Mock
    LineDao lineDao;
    @Mock
    StationDao stationDao;
    @Mock
    SectionDao sectionDao;

    @Test
    @DisplayName("올바른 구간 추가 요청이 들어오면, 업데이트할 구간과 생성할 구간을 구한 뒤 DAO로 전달한다.")
    void addSection() {
        //given
        final Station stationA = new Station(1L, "stationA");
        final Station stationB = new Station(2L, "stationB");
        final Station stationC = new Station(3L, "stationC");
        final Line lineA = new Line(1L, "lineA", "#ff0000");
        final Sections sections = new Sections(
            List.of(new Section(1L, lineA, stationA, stationB, 5)));
        final SectionAdditionRequest request = new SectionAdditionRequest(
            stationA.getId(), stationC.getId(), 3);
        doReturn(Optional.of(lineA)).when(lineDao).findById(lineA.getId());
        doReturn(sections).when(sectionDao).findAllByLine(lineA);
        doReturn(Optional.of(stationA)).when(stationDao).findById(stationA.getId());
        doReturn(Optional.of(stationC)).when(stationDao).findById(stationC.getId());

        //when
        sectionService.addSection(lineA.getId(), request);

        //then
        verify(sectionDao).update(any());
        verify(sectionDao).save(any());
    }

    @Test
    @DisplayName("올바른 구간 삭제 요청이 들어오면, 삭제할 구간을 구한 뒤 DAO로 전달한다.")
    void removeLast() {
        //given
        final Station stationA = new Station(1L, "stationA");
        final Station stationB = new Station(2L, "stationB");
        final Station stationC = new Station(3L, "stationC");
        final Line lineA = new Line(1L, "lineA", "#ff0000");
        final Section sectionA = new Section(1L, lineA, stationA, stationB, 5);
        final Section sectionB = new Section(2L, lineA, stationB, stationC, 5);
        final Sections sections = new Sections(List.of(sectionA, sectionB));
        doReturn(Optional.of(lineA)).when(lineDao).findById(lineA.getId());
        doReturn(sections).when(sectionDao).findAllByLine(lineA);
        doReturn(Optional.of(stationC)).when(stationDao).findById(stationC.getId());

        //when
        sectionService.removeLast(lineA.getId(), stationC.getId());

        //then
        verify(sectionDao).delete(sectionB);
    }
}
