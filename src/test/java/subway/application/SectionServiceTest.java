package subway.application;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import subway.dto.request.SectionRegistRequest;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {

    private Long lineId;
    private Line line;
    private Station 부천시청역;
    private Station 신중동역;
    private Station 춘의역;
    private Station 부천종합운동장역;
    private Station 까치울역;
    private Station 온수역;
    private Section 부천시청역_신중동역;
    private Section 신중동역_춘의역;
    private Section 춘의역_부천종합운동장역;
    private Sections 부천종합운동장역_까치울역;

    @Mock
    private SectionDao sectionDaoMock;
    @Mock
    private StationDao stationDaoMock;
    @Mock
    private LineDao lineDaoMock;
    @InjectMocks
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        lineId = 1L;
        line = new Line(lineId, "7호선", "red");

        부천시청역 = new Station(1L, "부천시청역");
        신중동역 = new Station(2L, "신중동역");
        춘의역 = new Station(3L, "춘의역");
        부천종합운동장역 = new Station(4L, "부천종합운동장역");
        까치울역 = new Station(5L, "까치울역");
        온수역 = new Station(6L, "온수역");

        부천시청역_신중동역 = new Section(
            1L,
            부천시청역,
            신중동역,
            line,
            10
        );
        신중동역_춘의역 = new Section(
            2L,
            신중동역,
            춘의역,
            line,
            10
        );
        춘의역_부천종합운동장역 = new Section(
            3L,
            춘의역,
            부천종합운동장역,
            line,
            10
        );

        부천종합운동장역_까치울역 = new Sections(List.of(부천시청역_신중동역, 춘의역_부천종합운동장역, 신중동역_춘의역));
    }

    @Test
    @DisplayName("상행 종점역의 구간 삭제")
    void deleteUpTerminusSection() {
        // given
        Long deletedStationId = 1L;
        Long deletedSectionId = 1L;
        Station deletedStation = 부천시청역;
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(부천종합운동장역_까치울역);
        when(stationDaoMock.findById(deletedStationId)).thenReturn(deletedStation);

        // when
        assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(deletedStationId, lineId));

        // then
        verify(sectionDaoMock).findAllByLineId(lineId);
        verify(stationDaoMock).findById(deletedStationId);
        verify(sectionDaoMock).deleteById(deletedSectionId);
    }

    @Test
    @DisplayName("상행역과 하행역이 존재하는 역 삭제")
    void deleteDownTerminusSection() {
        // given
        Long deletedStationId = 4L;
        Long deletedSectionId = 3L;
        Station deletedStation = 춘의역;
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(부천종합운동장역_까치울역);
        when(stationDaoMock.findById(deletedStationId)).thenReturn(deletedStation);

        // when
        assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(deletedStationId, lineId));

        // then
        verify(sectionDaoMock).findAllByLineId(lineId);
        verify(stationDaoMock).findById(deletedStationId);
        verify(sectionDaoMock).deleteById(deletedSectionId);
    }

    @Test
    @DisplayName("하행 종점역의 구간 삭제")
    void deleteMiddleSection() {
        // given
        Long deletedStationId = 3L;
        Long deletedSectionId2 = 2L;
        Long deletedSectionId3 = 3L;
        Station deletedStation = 춘의역;
        Section section = new Section(
            신중동역,
            부천종합운동장역,
            line,
            20
        );

        // when
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(부천종합운동장역_까치울역);
        when(stationDaoMock.findById(deletedStationId)).thenReturn(deletedStation);

        // then
        assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(deletedStationId, lineId));
        verify(sectionDaoMock).findAllByLineId(lineId);
        verify(stationDaoMock).findById(deletedStationId);
        verify(sectionDaoMock).deleteById(deletedSectionId2);
        verify(sectionDaoMock).deleteById(deletedSectionId3);
        verify(sectionDaoMock).insert(section);
    }

    @Test
    @DisplayName("예외 : 구간이 하나만 있을 때, 역을 삭제할 경우")
    void exceptionDeleteStationInOneSection() {
        // given
        Long deletedStationId = 1L;
        Sections sections = new Sections(List.of(부천시청역_신중동역));
        Station deletedStation = 부천시청역;

        // when
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(sections);
        when(stationDaoMock.findById(deletedStationId)).thenReturn(deletedStation);

        // then
        assertThatThrownBy(() -> sectionService.deleteSection(deletedStationId, lineId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(sectionDaoMock).findAllByLineId(lineId);
        verify(stationDaoMock).findById(deletedStationId);
    }

    @Test
    @DisplayName("예외 : 해당 노선에 포함되지 않은 역이 삭제 될 때")
    void exceptionDeleteStationNotInSections() {
        // given
        Long deletedStationId = 1L;
        Station deletedStation = 까치울역;

        // when
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(부천종합운동장역_까치울역);
        when(stationDaoMock.findById(deletedStationId)).thenReturn(deletedStation);

        // then
        assertThatThrownBy(() -> sectionService.deleteSection(deletedStationId, lineId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(sectionDaoMock).findAllByLineId(lineId);
        verify(stationDaoMock).findById(deletedStationId);
    }

    @Test
    @DisplayName("정상 : 구간의 하행역과 기존 노선의 상행 종점역 일치하는 구간 추가")
    void registUpTerminusSection() {
        // given
        Long upStationID = 1L;
        Long downStationId = 2L;
        int distance = 10;
        Sections sections = new Sections(List.of(신중동역_춘의역, 춘의역_부천종합운동장역));
        SectionRegistRequest sectionRegistRequest = new SectionRegistRequest(
            upStationID,
            downStationId,
            distance
        );

        // when
        when(stationDaoMock.findById(upStationID)).thenReturn(부천시청역);
        when(stationDaoMock.findById(downStationId)).thenReturn(신중동역);
        when(lineDaoMock.findById(lineId)).thenReturn(line);
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(sections);

        // then
        assertThatNoException()
            .isThrownBy(() -> sectionService.registerSection(sectionRegistRequest, lineId));
        verify(stationDaoMock).findById(upStationID);
        verify(stationDaoMock).findById(downStationId);
        verify(lineDaoMock).findById(lineId);
        verify(sectionDaoMock).findAllByLineId(lineId);
    }

    @Test
    @DisplayName("정상 : 구간의 하행역과 기존 노선의 하행 종점역 일치하는 구간 추가")
    void registDownTerminusSection() {
        // given
        Long upStationID = 4L;
        Long downStationId = 5L;
        int distance = 10;
        Sections sections = new Sections(List.of(신중동역_춘의역, 춘의역_부천종합운동장역));
        SectionRegistRequest sectionRegistRequest = new SectionRegistRequest(
            upStationID,
            downStationId,
            distance
        );

        // when
        when(stationDaoMock.findById(upStationID)).thenReturn(부천종합운동장역);
        when(stationDaoMock.findById(downStationId)).thenReturn(까치울역);
        when(lineDaoMock.findById(lineId)).thenReturn(line);
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(sections);

        // then
        assertThatNoException()
            .isThrownBy(() -> sectionService.registerSection(sectionRegistRequest, lineId));
        verify(stationDaoMock).findById(upStationID);
        verify(stationDaoMock).findById(downStationId);
        verify(lineDaoMock).findById(lineId);
        verify(sectionDaoMock).findAllByLineId(lineId);
    }

    @Test
    @DisplayName("애외 : 새로 추가하는 구간이 기존 구간 역보다 긴 경우")
    void exceptionLongerThanExistedSection() {
        // given
        Long upStationID = 1L;
        Long downStationId = 5L;
        Station upStation = 부천시청역;
        Station downStation = 까치울역;
        int distance = 11;
        SectionRegistRequest sectionRegistRequest = new SectionRegistRequest(
            upStationID,
            downStationId,
            distance
        );

        // when
        when(stationDaoMock.findById(upStationID)).thenReturn(upStation);
        when(stationDaoMock.findById(downStationId)).thenReturn(downStation);
        when(lineDaoMock.findById(lineId)).thenReturn(line);
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(부천종합운동장역_까치울역);

        // then
        assertThatThrownBy(() -> sectionService.registerSection(sectionRegistRequest, lineId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(stationDaoMock).findById(upStationID);
        verify(stationDaoMock).findById(downStationId);
        verify(lineDaoMock).findById(lineId);
        verify(sectionDaoMock).findAllByLineId(lineId);
    }

    @Test
    @DisplayName("애외 : 새로 추가하는 구간의 상행 하행역이 이미 존재한 경우")
    void exceptionExistedSection() {
        // given
        Long upStationID = 1L;
        Long downStationId = 3L;
        Station upStation = 부천시청역;
        Station downStation = 춘의역;
        int distance = 5;
        SectionRegistRequest sectionRegistRequest = new SectionRegistRequest(
            upStationID,
            downStationId,
            distance
        );

        // when
        when(stationDaoMock.findById(upStationID)).thenReturn(upStation);
        when(stationDaoMock.findById(downStationId)).thenReturn(downStation);
        when(lineDaoMock.findById(lineId)).thenReturn(line);
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(부천종합운동장역_까치울역);

        // then
        assertThatThrownBy(() -> sectionService.registerSection(sectionRegistRequest, lineId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(stationDaoMock).findById(upStationID);
        verify(stationDaoMock).findById(downStationId);
        verify(lineDaoMock).findById(lineId);
        verify(sectionDaoMock).findAllByLineId(lineId);
    }

    @Test
    @DisplayName("예외 : 새로 추가할 구간의 상행역과 하행역이 기존 노선헤 하나도 없는 경우")
    void exceptionNotExistedSection() {
        // given
        Long upStationID = 5L;
        Long downStationId = 6L;
        Station upStation = 까치울역;
        Station downStation = 온수역;
        int distance = 5;
        SectionRegistRequest sectionRegistRequest = new SectionRegistRequest(
            upStationID,
            downStationId,
            distance
        );

        // when
        when(stationDaoMock.findById(upStationID)).thenReturn(upStation);
        when(stationDaoMock.findById(downStationId)).thenReturn(downStation);
        when(lineDaoMock.findById(lineId)).thenReturn(line);
        when(sectionDaoMock.findAllByLineId(lineId)).thenReturn(부천종합운동장역_까치울역);

        // then
        assertThatThrownBy(() -> sectionService.registerSection(sectionRegistRequest, lineId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(stationDaoMock).findById(upStationID);
        verify(stationDaoMock).findById(downStationId);
        verify(lineDaoMock).findById(lineId);
        verify(sectionDaoMock).findAllByLineId(lineId);
    }
}
