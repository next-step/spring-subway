package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.exception.SubwayException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static subway.fixture.SectionFixture.*;
import static subway.fixture.StationFixture.*;

@DisplayName("경로 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @InjectMocks
    private PathService pathService;

    @Mock
    private SectionDao sectionDao;

    @Mock
    private StationDao stationDao;

    @DisplayName("출발역 id와 도착역 id로 경로를 조회하는 데 성공한다.")
    @Test
    void findPath() {
        // given
        final Station 범계역 = 범계역();
        final Station 잠실역 = 잠실역();

        final long 범계역_잠실역_거리 = DEFAULT_DISTANCE * 4;

        given(stationDao.findAll()).willReturn(List.of(
                범계역(), 경마공원역(), 사당역(), 신용산역(), 강남역(), 잠실역(), 여의도역(), 노량진역()
        ));

        given(sectionDao.findAll()).willReturn(List.of(
                범계역_경마공원역_구간(DEFAULT_DISTANCE),
                경마공원역_사당역_구간(DEFAULT_DISTANCE),
                사당역_신용산역_구간(DEFAULT_DISTANCE),
                경마공원역_강남역_구간(DEFAULT_DISTANCE * 5),
                사당역_강남역_구간(DEFAULT_DISTANCE),
                강남역_잠실역_구간(DEFAULT_DISTANCE),
                여의도역_노량진역_구간(DEFAULT_DISTANCE)
        ));

        given(stationDao.findById(범계역.getId())).willReturn(Optional.of(범계역));
        given(stationDao.findById(잠실역.getId())).willReturn(Optional.of(잠실역));

        // when
        final PathResponse 범계역_강남역_경로_응답 = pathService.findPath(범계역.getId(), 잠실역.getId());

        // then
        assertThat(범계역_강남역_경로_응답.getDistance()).isEqualTo(범계역_잠실역_거리);

        final List<Station> stations = 범계역_강남역_경로_응답.getStations()
                .stream()
                .map(stationResponse -> new Station(stationResponse.getId(), stationResponse.getName()))
                .collect(Collectors.toUnmodifiableList());
        assertThat(stations).containsExactly(범계역, 경마공원역(), 사당역(), 강남역(), 잠실역);
    }

    @DisplayName("출발역 id 또는 도착역 id가 존재하지 않아 경로를 조회하는 데 실패한다.")
    @Test
    void findPathWithStationNoExist() {
        // given
        final Station 범계역 = 범계역();
        final Station 첫번째역 = 첫번째역();

        given(stationDao.findAll()).willReturn(List.of(
                범계역(), 경마공원역(), 사당역(), 신용산역(), 강남역(), 잠실역(), 여의도역(), 노량진역()
        ));

        given(sectionDao.findAll()).willReturn(List.of(
                범계역_경마공원역_구간(DEFAULT_DISTANCE),
                경마공원역_사당역_구간(DEFAULT_DISTANCE),
                사당역_신용산역_구간(DEFAULT_DISTANCE),
                경마공원역_강남역_구간(DEFAULT_DISTANCE * 5),
                사당역_강남역_구간(DEFAULT_DISTANCE),
                강남역_잠실역_구간(DEFAULT_DISTANCE),
                여의도역_노량진역_구간(DEFAULT_DISTANCE)
        ));

        given(stationDao.findById(범계역.getId())).willReturn(Optional.of(범계역));
        given(stationDao.findById(첫번째역.getId())).willReturn(Optional.of(첫번째역));

        // when & then
        assertThatThrownBy(() -> pathService.findPath(범계역.getId(), 첫번째역.getId()))
                .hasMessage(첫번째역().getName() + "은(는) 존재하지 않는 역입니다.")
                .isInstanceOf(SubwayException.class);
        assertThatThrownBy(() -> pathService.findPath(첫번째역.getId(), 범계역.getId()))
                .hasMessage(첫번째역().getName() + "은(는) 존재하지 않는 역입니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("출발역 id와 도착역 id가 일치해 경로를 조회하는 데 실패한다.")
    @Test
    void findPathWithSameStationId() {
        // given
        final Station 범계역 = 범계역();

        given(stationDao.findAll()).willReturn(List.of(
                범계역(), 경마공원역(), 사당역(), 신용산역(), 강남역(), 잠실역(), 여의도역(), 노량진역()
        ));

        given(sectionDao.findAll()).willReturn(List.of(
                범계역_경마공원역_구간(DEFAULT_DISTANCE),
                경마공원역_사당역_구간(DEFAULT_DISTANCE),
                사당역_신용산역_구간(DEFAULT_DISTANCE),
                경마공원역_강남역_구간(DEFAULT_DISTANCE * 5),
                사당역_강남역_구간(DEFAULT_DISTANCE),
                강남역_잠실역_구간(DEFAULT_DISTANCE),
                여의도역_노량진역_구간(DEFAULT_DISTANCE)
        ));

        given(stationDao.findById(범계역.getId())).willReturn(Optional.of(범계역));

        // when & then
        assertThatThrownBy(() -> pathService.findPath(범계역.getId(), 범계역.getId()))
                .hasMessage("출발역과 도착역은 같을 수 없습니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("출발역과 도착역을 잇는 경로가 존재하지 않아 경로를 조회하는 데 실패한다.")
    @Test
    void findPathWithNotConnectedStations() {
        // given
        final Station 범계역 = 범계역();
        final Station 노량진역 = 노량진역();

        given(stationDao.findAll()).willReturn(List.of(
                범계역(), 경마공원역(), 사당역(), 신용산역(), 강남역(), 잠실역(), 여의도역(), 노량진역()
        ));

        given(sectionDao.findAll()).willReturn(List.of(
                범계역_경마공원역_구간(DEFAULT_DISTANCE),
                경마공원역_사당역_구간(DEFAULT_DISTANCE),
                사당역_신용산역_구간(DEFAULT_DISTANCE),
                경마공원역_강남역_구간(DEFAULT_DISTANCE * 5),
                사당역_강남역_구간(DEFAULT_DISTANCE),
                강남역_잠실역_구간(DEFAULT_DISTANCE),
                여의도역_노량진역_구간(DEFAULT_DISTANCE)
        ));

        given(stationDao.findById(범계역.getId())).willReturn(Optional.of(범계역));
        given(stationDao.findById(노량진역.getId())).willReturn(Optional.of(노량진역));

        // when & then
        assertThatThrownBy(() -> pathService.findPath(범계역.getId(), 노량진역.getId()))
                .hasMessage("출발역과 도착역을 연결하는 경로가 존재하지 않습니다.")
                .isInstanceOf(SubwayException.class);
    }
}
