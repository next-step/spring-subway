package subway.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.SubwayException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@DisplayName("역 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @DisplayName("역을 추가하는 데 성공한다.")
    @Test
    void saveStation() {
        // given
        final StationRequest stationRequest = new StationRequest("오이도");

        given(stationDao.findByName(stationRequest.getName())).willReturn(Optional.empty());
        given(stationDao.insert(any(Station.class))).willReturn(new Station(1L, stationRequest.getName()));

        // when & then
        assertThatNoException().isThrownBy(() -> stationService.saveStation(stationRequest));
    }

    @DisplayName("중복된 이름으로 역을 추가하는 데 실패한다.")
    @Test
    void saveStationWithDuplicateName() {
        // given
        final StationRequest stationRequest = new StationRequest("오이도");

        given(stationDao.findByName(stationRequest.getName())).willReturn(Optional.of(new Station(stationRequest.getName())));

        // when & then
        assertThatThrownBy(() -> stationService.saveStation(stationRequest))
                .hasMessage("중복된 이름(" + stationRequest.getName() + ")의 역이 존재합니다.")
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("역을 조회하는 데 성공한다.")
    @Test
    void findStation() {
        // given
        final long stationId = 1;
        final Station station = new Station(stationId, "오이도");

        given(stationDao.findById(stationId)).willReturn(Optional.of(station));

        // when
        final StationResponse response = stationService.findStationById(stationId);

        // then
        assertThat(response.getId()).isEqualTo(stationId);
    }

    @DisplayName("없는 id로 역을 조회하는 데 실패한다.")
    @Test
    void findStationWithWrongId() {
        // given
        final long stationId = 1;

        given(stationDao.findById(stationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.findStationById(stationId))
                .hasMessage(String.format("해당 id(%d)를 가지는 역이 존재하지 않습니다.", stationId))
                .isInstanceOf(SubwayException.class);
    }

    @DisplayName("모든 역을 조회하는 데 성공한다.")
    @Test
    void findAllStations() {
        // given
        final List<Station> stations = List.of(new Station(1L, "오이도"));
        given(stationDao.findAll()).willReturn(stations);

        // when
        List<StationResponse> response = stationService.findAllStations();

        // then
        assertThat(response).hasSameSizeAs(stations);
        assertThat(response.get(0).getId()).isEqualTo(stations.get(0).getId());
    }

    @DisplayName("역의 이름을 변경하는 데 성공한다.")
    @Test
    void updateStationName() {
        // given
        final StationRequest stationRequest = new StationRequest("오이도");

        // when & then
        assertThatNoException().isThrownBy(() -> stationService.updateStation(1L, stationRequest));
    }

    @DisplayName("역을 삭제하는 데 성공한다.")
    @Test
    void deleteStationName() {
        assertThatNoException().isThrownBy(() -> stationService.deleteStationById(1L));
    }
}
