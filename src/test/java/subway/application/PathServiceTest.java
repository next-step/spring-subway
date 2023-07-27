package subway.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.anyLong;
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
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.DomainFixture;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.exception.StatusCodeException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PathService.class)
class PathServiceTest {

    @Autowired
    private PathService pathService;

    @MockBean
    private StationDao stationDao;

    @MockBean
    private SectionDao sectionDao;

    @Nested
    @DisplayName("getMinimumPath 메소드는")
    class GetMinimumPath_Method {

        Station upStation = new Station(1L, "upStation");
        Station middleStation = new Station(2L, "middleStation");
        Station downStation = new Station(3L, "downStation");

        Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation);
        Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation);

        @Test
        @DisplayName("stationId에 해당하는 Station을 찾을 수 없으면, StatusCodeException을 던진다.")
        void Throw_StatusCodeException_CannotFind_Matched_Station() {
            // given
            when(stationDao.findById(anyLong())).thenReturn(Optional.empty());

            // when
            Exception exception = catchException(
                    () -> pathService.getMinimumPath(upStation.getId(), downStation.getId()));

            // then
            assertThat(exception).isInstanceOf(StatusCodeException.class);
            assertThat(((StatusCodeException) exception).getStatus()).isEqualTo("PATH-SERVICE-401");
        }

    }

}
