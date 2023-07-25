package subway.application;

import org.assertj.core.api.Assertions;
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
import subway.dto.request.SectionRegisterRequest;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private SectionDao sectionDao;
    @Mock
    private LineDao lineDao;
    @Mock
    private StationDao stationDao;
    @InjectMocks
    private SectionService sectionService;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;
    private Station station5;
    private Station station6;
    private Line line1;
    private Section section1;
    private Section section2;
    private Section section3;
    private Sections sections;

    @BeforeEach
    void setup() {
        station1 = new Station(1L, "부천시청역");
        station2 = new Station(2L, "신중동역");
        station3 = new Station(3L, "춘의역");
        station4 = new Station(4L, "부천종합운동장역");
        station5 = new Station(5L, "까치울역");
        station6 = new Station(6L, "온수역");

        line1 = new Line(1L, "7호선", "주황");

        section1 = new Section(
                1L,
                station1,
                station2,
                line1,
                10
        );
        section2 = new Section(
                2L,
                station2,
                station3,
                line1,
                10
        );
        section3 = new Section(
                3L,
                station3,
                station4,
                line1,
                10
        );

        sections = new Sections(List.of(section1, section3, section2));

        when(sectionDao.findAllByLineId(1L)).thenReturn(sections);
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 양 옆에 구간이 있는 경우")
    void delete() {
        when(stationDao.findById(2L)).thenReturn(station2);
        when(sectionDao.insert(section1.combineSection(section2))).thenReturn(section1.combineSection(section2));

        Assertions.assertThatNoException()
                .isThrownBy(() -> sectionService.deleteSection(2L, 1L));

        verify(sectionDao).deleteById(1L);
        verify(sectionDao).deleteById(2L);
        verify(sectionDao).insert(new Section(station1, station3, line1, 20));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 하행 종점역을 삭제하는 경우")
    void delete2() {
        when(stationDao.findById(1L)).thenReturn(station1);

        Assertions.assertThatNoException()
                .isThrownBy(() -> sectionService.deleteSection(1L, 1L));

        verify(sectionDao).deleteById(1L);
        verify(sectionDao, never()).insert(new Section(station1, station3, line1, 20));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 상행 종점역을 삭제하는 경우")
    void delete3() {
        when(stationDao.findById(3L)).thenReturn(station3);

        Assertions.assertThatNoException()
                .isThrownBy(() -> sectionService.deleteSection(3L, 1L));

        verify(sectionDao).deleteById(2L);
        verify(sectionDao, never()).insert(new Section(station1, station3, line1, 20));
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트: 구간이 하나인 노선에서 마지막 구간을 제거할 때")
    void deleteException1() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(new Sections(List.of(section1)));
        when(stationDao.findById(2L)).thenReturn(station2);

        Assertions.assertThatThrownBy(() -> sectionService.deleteSection(2L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 1개 이하이므로 해당역을 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트: 구간에 없는 역을 제거하려고 할 때")
    void deleteException2() {
        when(stationDao.findById(5L)).thenReturn(station5);

        Assertions.assertThatThrownBy(() -> sectionService.deleteSection(5L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록되지 않은 역을 제거할 수 없습니다.");
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 구간 중간에 추가되는 경우(상행이 겹치는 경우)")
    void add1() {
        when(stationDao.findById(2L)).thenReturn(station2);
        when(stationDao.findById(5L)).thenReturn(station5);
        when(lineDao.findById(1L)).thenReturn(line1);

        sectionService.registerSection(new SectionRegisterRequest(2L, 5L, 3), 1L);

        verify(sectionDao).insert(new Section(station2, station5, line1, 3));
        verify(sectionDao).update(new Section(2L, station5, station3, line1, 7));
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 구간 중간에 추가되는 경우(하행이 겹치는 경우)")
    void add2() {
        when(stationDao.findById(2L)).thenReturn(station2);
        when(stationDao.findById(5L)).thenReturn(station5);
        when(lineDao.findById(1L)).thenReturn(line1);

        sectionService.registerSection(new SectionRegisterRequest(5L, 2L, 3), 1L);

        verify(sectionDao).insert(new Section(station5, station2, line1, 3));
        verify(sectionDao).update(new Section(1L, station1, station5, line1, 7));
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 역 시작점에 추가되는 경우")
    void add3() {
        when(stationDao.findById(1L)).thenReturn(station1);
        when(stationDao.findById(5L)).thenReturn(station5);
        when(lineDao.findById(1L)).thenReturn(line1);

        sectionService.registerSection(new SectionRegisterRequest(5L, 1L, 3), 1L);

        verify(sectionDao).insert(new Section(station5, station1, line1, 3));
        verify(sectionDao, never()).update(new Section(1L, station1, station5, line1, 7));
        verify(sectionDao, never()).update(new Section(1L, station5, station2, line1, 7));
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 역 끝점에 추가되는 경우")
    void add4() {
        when(stationDao.findById(3L)).thenReturn(station3);
        when(stationDao.findById(5L)).thenReturn(station5);
        when(lineDao.findById(1L)).thenReturn(line1);

        sectionService.registerSection(new SectionRegisterRequest(3L, 5L, 3), 1L);

        verify(sectionDao).insert(new Section(station3, station5, line1, 3));
        verify(sectionDao, never()).update(new Section(1L, station2, station5, line1, 7));
        verify(sectionDao, never()).update(new Section(1L, station5, station2, line1, 7));
    }

    @Test
    @DisplayName("구간 추가 예외 테스트: 추가하려는 역의 Distance가 겹치는 역보다 긴 경우")
    void addException1() {
        when(stationDao.findById(2L)).thenReturn(station2);
        when(stationDao.findById(5L)).thenReturn(station5);
        when(lineDao.findById(1L)).thenReturn(line1);

        Assertions.assertThatThrownBy(
                () -> sectionService.registerSection(
                        new SectionRegisterRequest(2L, 5L, 20), 1L)
                )
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 추가 예외 테스트: 기존 구간과 겹치는 경우")
    void addException2() {
        when(stationDao.findById(1L)).thenReturn(station1);
        when(stationDao.findById(2L)).thenReturn(station2);
        when(lineDao.findById(1L)).thenReturn(line1);

        Assertions.assertThatThrownBy(
                        () -> sectionService.registerSection(
                                new SectionRegisterRequest(1L, 2L, 10), 1L)
                )
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구간 추가 예외 테스트: 등록하고자 하는 구간의 2개의 역이 모두 노선에 포함되지 않아 추가할 수 없는 경우")
    void addException3() {
        when(stationDao.findById(5L)).thenReturn(station5);
        when(stationDao.findById(6L)).thenReturn(station6);
        when(lineDao.findById(1L)).thenReturn(line1);

        Assertions.assertThatThrownBy(
                        () -> sectionService.registerSection(
                                new SectionRegisterRequest(5L, 6L, 10), 1L)
                )
                .isInstanceOf(IllegalArgumentException.class);
    }
}
