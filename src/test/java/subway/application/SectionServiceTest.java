package subway.application;

import java.util.Optional;
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
import subway.dto.request.PathRequest;
import subway.dto.request.SectionRegisterRequest;

import java.util.List;
import subway.dto.response.PathResponse;
import subway.dto.response.StationResponse;

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

    private Station 부천시청역;
    private Station 신중동역;
    private Station 춘의역;
    private Station 부천종합운동장역;
    private Line 칠호선;
    private Section 부천시청_신중동_구간;
    private Section 신중동_춘의_구간;
    private Section 춘의_부천종합운동장_구간;
    private Sections 칠호선_구간들;

    @BeforeEach
    void setup() {
        부천시청역 = new Station(1L, "부천시청역");
        신중동역 = new Station(2L, "신중동역");
        춘의역 = new Station(3L, "춘의역");
        부천종합운동장역 = new Station(4L, "부천종합운동장역");

        칠호선 = new Line(1L, "7호선", "주황");

        부천시청_신중동_구간 = new Section(
            1L,
            부천시청역,
            신중동역,
            칠호선,
            10
        );
        신중동_춘의_구간 = new Section(
            2L,
            신중동역,
            춘의역,
            칠호선,
            10
        );
        춘의_부천종합운동장_구간 = new Section(
            3L,
            춘의역,
            부천종합운동장역,
            칠호선,
            10
        );

        칠호선_구간들 = new Sections(List.of(부천시청_신중동_구간, 춘의_부천종합운동장_구간, 신중동_춘의_구간));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 양 옆에 구간이 있는 경우")
    void delete() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(2L)).thenReturn(Optional.of(신중동역));
        when(sectionDao.insert(부천시청_신중동_구간.combineSection(Optional.of(신중동_춘의_구간)).get()))
            .thenReturn(부천시청_신중동_구간.combineSection(Optional.of(신중동_춘의_구간)).get());

        Assertions.assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(2L, 1L));

        verify(sectionDao).deleteById(1L);
        verify(sectionDao).deleteById(2L);
        verify(sectionDao).insert(new Section(부천시청역, 춘의역, 칠호선, 20));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 하행 종점역을 삭제하는 경우")
    void delete2() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(1L)).thenReturn(Optional.of(부천시청역));

        Assertions.assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(1L, 1L));

        verify(sectionDao).deleteById(1L);
        verify(sectionDao, never()).insert(new Section(부천시청역, 춘의역, 칠호선, 20));
    }

    @Test
    @DisplayName("구간 삭제 정상 테스트: 상행 종점역을 삭제하는 경우")
    void delete3() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(3L)).thenReturn(Optional.of(춘의역));

        Assertions.assertThatNoException()
            .isThrownBy(() -> sectionService.deleteSection(3L, 1L));

        verify(sectionDao).deleteById(2L);
        verify(sectionDao, never()).insert(new Section(부천시청역, 춘의역, 칠호선, 20));
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트: 구간이 하나인 노선에서 마지막 구간을 제거할 때")
    void deleteException1() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(sectionDao.findAllByLineId(1L)).thenReturn(new Sections(List.of(부천시청_신중동_구간)));
        when(stationDao.findById(2L)).thenReturn(Optional.of(신중동역));

        Assertions.assertThatThrownBy(() -> sectionService.deleteSection(2L, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("구간이 1개 이하이므로 해당역을 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("구간 삭제 예외 테스트: 구간에 없는 역을 제거하려고 할 때")
    void deleteException2() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        Station 까치울역 = new Station(5L, "까치울역");

        when(stationDao.findById(5L)).thenReturn(Optional.of(까치울역));

        Assertions.assertThatThrownBy(() -> sectionService.deleteSection(5L, 1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("등록되지 않은 역을 제거할 수 없습니다.");
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 구간 중간에 추가되는 경우(상행이 겹치는 경우)")
    void add1() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        Station 까치울역 = new Station(5L, "까치울역");

        when(stationDao.findById(2L)).thenReturn(Optional.of(신중동역));
        when(stationDao.findById(5L)).thenReturn(Optional.of(까치울역));
        when(lineDao.findById(1L)).thenReturn(Optional.of(칠호선));

        sectionService.registerSection(new SectionRegisterRequest(2L, 5L, 3), 1L);

        verify(sectionDao).insert(new Section(신중동역, 까치울역, 칠호선, 3));
        verify(sectionDao).update(new Section(2L, 까치울역, 춘의역, 칠호선, 7));
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 구간 중간에 추가되는 경우(하행이 겹치는 경우)")
    void add2() {
        Station 까치울역 = new Station(5L, "까치울역");

        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(2L)).thenReturn(Optional.of(신중동역));
        when(stationDao.findById(5L)).thenReturn(Optional.of(까치울역));
        when(lineDao.findById(1L)).thenReturn(Optional.of(칠호선));

        sectionService.registerSection(new SectionRegisterRequest(5L, 2L, 3), 1L);

        verify(sectionDao).insert(new Section(까치울역, 신중동역, 칠호선, 3));
        verify(sectionDao).update(new Section(1L, 부천시청역, 까치울역, 칠호선, 7));
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 역 시작점에 추가되는 경우")
    void add3() {
        Station 까치울역 = new Station(5L, "까치울역");

        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(1L)).thenReturn(Optional.of(부천시청역));
        when(stationDao.findById(5L)).thenReturn(Optional.of(까치울역));
        when(lineDao.findById(1L)).thenReturn(Optional.of(칠호선));

        sectionService.registerSection(new SectionRegisterRequest(5L, 1L, 3), 1L);

        verify(sectionDao).insert(new Section(까치울역, 부천시청역, 칠호선, 3));
        verify(sectionDao, never()).update(new Section(1L, 부천시청역, 까치울역, 칠호선, 7));
        verify(sectionDao, never()).update(new Section(1L, 까치울역, 신중동역, 칠호선, 7));
    }

    @Test
    @DisplayName("구간 추가 정상 테스트: 역 끝점에 추가되는 경우")
    void add4() {
        Station 까치울역 = new Station(5L, "까치울역");

        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(3L)).thenReturn(Optional.of(춘의역));
        when(stationDao.findById(5L)).thenReturn(Optional.of(까치울역));
        when(lineDao.findById(1L)).thenReturn(Optional.of(칠호선));

        sectionService.registerSection(new SectionRegisterRequest(3L, 5L, 3), 1L);

        verify(sectionDao).insert(new Section(춘의역, 까치울역, 칠호선, 3));
        verify(sectionDao, never()).update(new Section(1L, 신중동역, 까치울역, 칠호선, 7));
        verify(sectionDao, never()).update(new Section(1L, 까치울역, 신중동역, 칠호선, 7));
    }

    @Test
    @DisplayName("구간 추가 예외 테스트: 추가하려는 역의 Distance가 겹치는 역보다 긴 경우")
    void addException1() {
        Station 까치울역 = new Station(5L, "까치울역");

        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(2L)).thenReturn(Optional.of(신중동역));
        when(stationDao.findById(5L)).thenReturn(Optional.of(까치울역));
        when(lineDao.findById(1L)).thenReturn(Optional.of(칠호선));

        Assertions.assertThatThrownBy(
                () -> sectionService.registerSection(
                    new SectionRegisterRequest(2L, 5L, 20), 1L)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("기존 구간에 비해 거리가 길어 추가가 불가능 합니다.");
    }

    @Test
    @DisplayName("구간 추가 예외 테스트: 기존 구간과 겹치는 경우")
    void addException2() {
        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(1L)).thenReturn(Optional.of(부천시청역));
        when(stationDao.findById(2L)).thenReturn(Optional.of(신중동역));
        when(lineDao.findById(1L)).thenReturn(Optional.of(칠호선));

        Assertions.assertThatThrownBy(
                () -> sectionService.registerSection(
                    new SectionRegisterRequest(1L, 2L, 10), 1L)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("기존 구간의 상행역과 하행역이 중복 됩니다.");
    }

    @Test
    @DisplayName("구간 추가 예외 테스트: 등록하고자 하는 구간의 2개의 역이 모두 노선에 포함되지 않아 추가할 수 없는 경우")
    void addException3() {
        Station 까치울역 = new Station(5L, "까치울역");
        Station 온수역 = new Station(6L, "온수역");

        when(sectionDao.findAllByLineId(1L)).thenReturn(칠호선_구간들);
        when(stationDao.findById(5L)).thenReturn(Optional.of(까치울역));
        when(stationDao.findById(6L)).thenReturn(Optional.of(온수역));
        when(lineDao.findById(1L)).thenReturn(Optional.of(칠호선));

        Assertions.assertThatThrownBy(
                () -> sectionService.registerSection(
                    new SectionRegisterRequest(5L, 6L, 10), 1L)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("등록하고자 하는 구간의 2개의 역이 모두 노선에 포함되지 않아 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("경로 조회 테스트")
    void findPath() {
        when(stationDao.findById(1L)).thenReturn(Optional.of(부천시청역));
        when(stationDao.findById(4L)).thenReturn(Optional.of(부천종합운동장역));
        when(sectionDao.findAll()).thenReturn(칠호선_구간들);

        PathResponse pathResponse
            = sectionService.findStationToStationDistance(new PathRequest(1L, 4L));

        Assertions.assertThat(pathResponse.getDistance()).isEqualTo(30);
        Assertions.assertThat(pathResponse.getStations())
            .containsAll(List.of(
                new StationResponse(1L, "부천시청역"),
                new StationResponse(2L, "신중동역"),
                new StationResponse(3L, "춘의역"),
                new StationResponse(4L, "부천종합운동장역")
            ));
    }

    @Test
    @DisplayName("경로 조회 예외 테스트: 두 역이 연결되어 있지 않은 경우")
    void findPathException1() {
        Station 까치울역 = new Station(5L, "까치울역");
        Station 온수역 = new Station(6L, "온수역");

        Section 온수_까치울_구간 = new Section(
            4L,
            까치울역,
            온수역,
            칠호선,
            20
        );

        칠호선_구간들 = new Sections(List.of(부천시청_신중동_구간, 춘의_부천종합운동장_구간, 신중동_춘의_구간, 온수_까치울_구간));

        when(stationDao.findById(1L)).thenReturn(Optional.of(부천시청역));
        when(stationDao.findById(6L)).thenReturn(Optional.of(온수역));
        when(sectionDao.findAll()).thenReturn(칠호선_구간들);

        Assertions.assertThatThrownBy(
                () -> sectionService.findStationToStationDistance(new PathRequest(1L, 6L))
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발역과 도착역이 연결되어있지 않습니다.");
    }

    @Test
    @DisplayName("경로 조회 예외 테스트: 출발역과 도착역이 동일할 경우")
    void findPathException2() {
        when(stationDao.findById(1L)).thenReturn(Optional.of(부천시청역));
        when(sectionDao.findAll()).thenReturn(칠호선_구간들);

        Assertions.assertThatThrownBy(
                () -> sectionService.findStationToStationDistance(new PathRequest(1L, 1L))
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("출발역과 도착역이 동일합니다.");
    }

    @Test
    @DisplayName("경로 조회 예외 테스트: 존재하지 않은 역을 입력한 경우")
    void findPathException3() {
        when(stationDao.findById(1L)).thenReturn(Optional.of(부천시청역));
        when(stationDao.findById(8L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> sectionService.findStationToStationDistance(new PathRequest(1L, 8L))
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("존재하지 않은 역을 입력했습니다.");
    }
}
