package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import subway.exception.IllegalStationsException;

@DisplayName("역 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @InjectMocks
    private StationService stationService;

    @Mock
    private StationDao stationDao;

    @Test
    @DisplayName("역 정보를 저장한다.")
    void saveStationTest() {
        // given
        Long stationId = 1L;
        StationRequest request = new StationRequest("잠실");
        Station insertedStation = new Station(stationId, request.getName());
        given(stationDao.insert(any(Station.class))).willReturn(insertedStation);

        // when
        StationResponse result = stationService.saveStation(request);

        // then
        assertThat(result.getName()).isEqualTo(request.getName());
    }

    @Test
    @DisplayName("식별자와 일치하는 역 정보를 반환한다.")
    void findStationByIdTest() {
        // given
        Long stationId = 1L;
        Station station = new Station(stationId, "잠실");
        given(stationDao.findById(stationId)).willReturn(Optional.of(station));

        // when
        StationResponse result = stationService.findStationById(stationId);

        // then
        assertThat(result.getId()).isEqualTo(station.getId());
        assertThat(result.getName()).isEqualTo(station.getName());
    }

    @Test
    @DisplayName("식별자와 일치하는 역 정보가 없으면 예외를 던진다.")
    void findStationByIdExceptionTest() {
        // given
        Long stationId = 1L;
        given(stationDao.findById(stationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> stationService.findStationById(stationId))
            .hasMessage("존재하지 않는 역 정보입니다.")
            .isInstanceOf(IllegalStationsException.class);
    }

    @Test
    @DisplayName("모든 역 정보를 반환한다.")
    void findAllStationsTest() {
        // given
        List<Station> stations = createStations();
        given(stationDao.findAll()).willReturn(createStations());

        // when
        List<StationResponse> result = stationService.findAllStations();

        // then
        List<Long> expectedIds = stations.stream()
            .map(Station::getId)
            .collect(Collectors.toList());
        List<Long> actualIds = result.stream()
            .map(StationResponse::getId)
            .collect(Collectors.toList());
        assertThat(actualIds).containsAll(expectedIds);
    }

    @Test
    @DisplayName("역 정보를 갱신한다.")
    void updateStationTest() {
        // given
        Long stationId = 1L;
        StationRequest request = new StationRequest("잠실");
        doNothing().when(stationDao).update(any(Station.class));

        // when
        stationService.updateStation(stationId, request);

        // then
        verify(stationDao, times(1)).update(any(Station.class));
    }

    @Test
    @DisplayName("역 정보를 갱신한다.")
    void deleteStationByIdTest() {
        // given
        Long stationId = 1L;
        doNothing().when(stationDao).deleteById(stationId);

        // when
        stationService.deleteStationById(stationId);

        // then
        verify(stationDao, times(1)).deleteById(stationId);
    }

    private List<Station> createStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(new Station(1L, "잠실"));
        stations.add(new Station(2L, "잠실나루"));
        stations.add(new Station(3L, "강변"));
        return stations;
    }
}