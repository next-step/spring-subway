package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import subway.DomainFixture;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.domain.Section;
import subway.domain.Station;
import subway.dto.response.FindPathResponse;
import subway.dto.response.FindStationResponse;
import subway.exception.PathException;
import subway.exception.StationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PathService.class, PathFinderService.class})
@DisplayName("PathService 클래스")
public class PathServiceTest {

    @Autowired
    private PathService pathService;

    @MockBean
    private SectionDao sectionDao;

    @MockBean
    private StationDao stationDao;

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void beforeEach() {
        station1 = new Station(1L, "station1");
        station2 = new Station(2L, "station2");
        station3 = new Station(3L, "station3");
        station4 = new Station(4L, "station4");
    }

    @Nested
    @DisplayName("findPath 메소드는")
    class FindPath_Method {

        @Test
        @DisplayName("startStation과 endStation의 경로와 거리를 담은 FindPathResponse를 반환한다.")
        void Return_FindPathResponse_When_Input_StartStation_And_EndStation() {
            // given
            Station startStation = station1;
            Station endStation = station4;

            Section section1 = DomainFixture.Section.buildWithStations(startStation, station2, 1);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3, 2);
            Section section3 = DomainFixture.Section.buildWithStations(station2, endStation, 4);


            when(stationDao.findById(startStation.getId())).thenReturn(Optional.of(startStation));
            when(stationDao.findById(endStation.getId())).thenReturn(Optional.of(endStation));

            when(sectionDao.findAll()).thenReturn(List.of(section1, section2, section3));

            // when
            FindPathResponse findPathResponse = pathService.findPath(startStation.getId(), endStation.getId());
            Integer distance = findPathResponse.getDistance();
            List<FindStationResponse> stations = findPathResponse.getStations();

            // then
            assertThat(distance).isEqualTo(5);
            List<Station> expected = Arrays.asList(startStation, station2, endStation);

            for (int i = 0; i < stations.size(); i++) {
                assertThat(stations.get(i).getId()).isEqualTo(expected.get(i).getId());
            }
        }

        @Test
        @DisplayName("stationId에 해당하는 startStation또는 endStation이 존재하지 않는다면, StationException을 던진다")
        void Throw_StationException_When_Stations_Not_Exist() {
            // given
            Station startStation = station1;
            Station endStation = station2;

            // when
            Exception exception = catchException(() -> pathService.findPath(startStation.getId(), endStation.getId()));

            // then
            assertThat(exception).isInstanceOf(StationException.class);
        }

        @Test
        @DisplayName("startStation과 endStation가 동일하다면 PathException을 던진다")
        void Throw_PathException_When_Same_Stations() {
            // given
            Station startStation = station1;
            Station endStation = station1;

            Section section1 = DomainFixture.Section.buildWithStations(startStation, station2, 1);
            Section section2 = DomainFixture.Section.buildWithStations(station2, station3, 2);
            Section section3 = DomainFixture.Section.buildWithStations(station2, endStation, 4);


            when(stationDao.findById(startStation.getId())).thenReturn(Optional.of(startStation));
            when(stationDao.findById(endStation.getId())).thenReturn(Optional.of(endStation));

            when(sectionDao.findAll()).thenReturn(List.of(section1, section2, section3));

            // when
            Exception exception = catchException(() -> pathService.findPath(startStation.getId(), endStation.getId()));

            // then
            assertThat(exception).isInstanceOf(PathException.class);
        }

        @Test
        @DisplayName("startStation과 endStation가 연결되어있지 않다면 PathException을 던진다")
        void Throw_PathException_When_Unlinked_Stations() {
            // given
            Station startStation = station1;
            Station endStation = station4;

            Section section1 = DomainFixture.Section.buildWithStations(startStation, station2, 1);
            Section section2 = DomainFixture.Section.buildWithStations(station3, endStation, 2);

            when(stationDao.findById(startStation.getId())).thenReturn(Optional.of(startStation));
            when(stationDao.findById(endStation.getId())).thenReturn(Optional.of(endStation));

            when(sectionDao.findAll()).thenReturn(List.of(section1, section2));

            // when
            Exception exception = catchException(() -> pathService.findPath(startStation.getId(), endStation.getId()));

            // then
            assertThat(exception).isInstanceOf(PathException.class);
        }
    }
}
