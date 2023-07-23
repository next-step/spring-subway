package subway.application;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Sections;
import subway.domain.Station;
import subway.dto.SectionAdditionRequest;

@SpringBootTest(classes = {SectionService.class})
@DisplayName("SectionService 단위 테스트")
class SectionServiceTest {

    @Autowired
    SectionService sectionService;
    @MockBean
    LineDao lineDao;
    @MockBean
    StationDao stationDao;
    @MockBean
    SectionDao sectionDao;

    @Test
    @DisplayName("올바른 구간 추가 요청이 들어오면, 삭제할 구간과 생성할 구간을 구한 뒤 DAO로 전달한다.")
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
        verify(sectionDao, times(1)).update(any());
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
