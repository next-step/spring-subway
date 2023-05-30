package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import subway.dao.StationDao;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Station 서비스 로직")
@SpringBootTest
class StationServiceTest {

    private static final StationRequest REQUEST_1 = new StationRequest("정자역");
    private static final StationRequest REQUEST_2 = new StationRequest("판교역");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private StationService stationService;
    @Autowired
    private StationDao stationDao;

    @BeforeEach
    void setUp() {
        // reset station table
        jdbcTemplate.update("delete from station");
    }

    @DisplayName("Station을 저장한다")
    @Test
    void saveStation() {
        // when
        StationResponse response = stationService.saveStation(REQUEST_1);

        // then
        Station foundStation = assertDoesNotThrow(() -> stationDao.findById(response.getId()).get());
        assertThat(foundStation.getName()).isEqualTo(REQUEST_1.getName());
    }

    @DisplayName("id를 통해 Station을 찾는다")
    @Test
    void findStationResponseByValidId() {
        // given
        StationResponse savedResponse = stationService.saveStation(REQUEST_1);

        // when
        StationResponse foundResponse = stationService.findStationResponseById(savedResponse.getId());

        // then
        assertThat(foundResponse.getId()).isNotNull();
        assertThat(foundResponse.getName()).isEqualTo(REQUEST_1.getName());
    }

    @DisplayName("존재하지 않는 id를 통해 Station을 찾는다")
    @Test
    void findStationResponseByNonExistenceId() {
        // given
        StationResponse savedResponse = stationService.saveStation(REQUEST_1);

        // when, then
        assertThatThrownBy(() -> stationService.findStationResponseById(savedResponse.getId() + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("모든 Station을 찾는다")
    @Test
    void findAllStationResponses() {
        // given
        stationService.saveStation(REQUEST_1);
        stationService.saveStation(REQUEST_2);

        // when
        List<StationResponse> responses = stationService.findAllStationResponses();

        // then
        assertThat(responses)
                .flatExtracting(StationResponse::getId).doesNotContainNull();
        assertThat(responses)
                .flatExtracting(StationResponse::getName).containsExactly(REQUEST_1.getName(), REQUEST_2.getName());
    }

    @DisplayName("Station을 수정한다")
    @Test
    void updateStation() {
        // given
        StationResponse response = stationService.saveStation(REQUEST_1);

        // when
        stationService.updateStation(response.getId(), REQUEST_2);

        // then
        Station foundStation = stationDao.findById(response.getId()).get();
        assertThat(foundStation.getName()).isEqualTo(REQUEST_2.getName());
    }

    @DisplayName("Station을 삭제한다")
    @Test
    void deleteStationById() {
        // given
        StationResponse response = stationService.saveStation(REQUEST_1);

        // when
        stationService.deleteStationById(response.getId());

        // then
        assertThat(stationDao.findById(response.getId())).isEmpty();
    }

}
