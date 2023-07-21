package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.dao.StationDao;
import subway.domain.Station;
import subway.dto.StationRequest;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = StationService.class)
@DisplayName("StationService 클래스")
class StationServiceTest {

    @Autowired
    private StationService stationService;

    @MockBean
    private StationDao stationDao;

    @Nested
    @DisplayName("saveStation 메소드는")
    class SaveStation_Method {

        @Test
        @DisplayName("중복된 이름의 station이 요청되면, IllegalArgumentException을 던진다.")
        void Throw_IllegalArgumentException_If_Duplicated_Station_Name() {
            // given
            Station station = new Station("exists");
            StationRequest stationRequest = new StationRequest("exists");

            when(stationDao.findByName(station.getName())).thenReturn(Optional.of(station));

            // when
            Exception exception = catchException(() -> stationService.saveStation(stationRequest));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("findStationResponseById 메소드는")
    class FindStationResponseById_Method {

        @Test
        @DisplayName("stationId에 해당하는 Station이 없다면, IllegalArgumentException을 던진다.")
        void Throw_IllegalArgumentException_If_Cannot_Find_Station() {
            // given
            Long stationId = 1L;

            when(stationDao.findById(stationId)).thenReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> stationService.findStationResponseById(stationId));

            // then
            assertThat(exception).isInstanceOf(IllegalArgumentException.class);
        }
    }

}
