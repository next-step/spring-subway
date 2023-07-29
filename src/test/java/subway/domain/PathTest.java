package subway.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import subway.domain.exception.StatusCodeException;
import subway.domain.response.PathResponse;
import subway.domain.status.PathExceptionStatus;

@DisplayName("Path 클래스")
class PathTest {

    @Nested
    @DisplayName("minimumPath 메소드는")
    class minimumPathOf_Method {

        Station upStation = new Station(1L, "station1");
        Station middleStation = new Station(2L, "station2");
        Station downStation = new Station(3L, "station3");

        Section upSection = DomainFixture.Section.buildWithStations(upStation, middleStation, 10);
        Section downSection = DomainFixture.Section.buildWithStations(middleStation, downStation, 10);

        @Test
        @DisplayName("시작 station과 종점 station이 주어지면, 거리와 연결된 station을 리턴한다.")
        void Return_Distance_And_PathStation_When_Input_Start_And_End_Station() {
            // given
            Path path = new Path(new ArrayList<>(List.of(upSection, downSection)));

            // when
            PathResponse result = path.minimumPath(upStation, downStation);

            // then
            assertPathResponse(result, 20, upStation, middleStation, downStation);
        }

        @Test
        @DisplayName("시작 station에서 station로 가는 경로를 찾을 수 없으면, StatusCodeException을 던진다")
        void Throw_StatusCodeException_When_Cannot_Found_Station() {
            // given
            Path path = new Path(new ArrayList<>(List.of(upSection, downSection)));

            Station illegalSourceStation = new Station(4L, "station4");
            Station illegalTargetStation = new Station(5L, "station5");

            // when
            Exception exception = catchException(() -> path.minimumPath(illegalSourceStation, illegalTargetStation));

            // then
            assertThat(exception).isInstanceOf(StatusCodeException.class);
            assertThat(((StatusCodeException) exception).getStatus()).isEqualTo(
                    PathExceptionStatus.CANNOT_FIND_PATH.getStatus());
        }

        @Test
        @DisplayName("사이클이 있디면, 더 빠른 경로로 돌아온다")
        void When_Cycled() {
            // given
            Section cycledSection = DomainFixture.Section.buildWithStations(downStation, upStation);
            Path path = new Path(new ArrayList<>(List.of(upSection, downSection, cycledSection)));

            // when
            PathResponse result = path.minimumPath(upStation, downStation);

            // then
            assertPathResponse(result, 10, upStation, downStation);
        }

        private void assertPathResponse(PathResponse pathResponse, int expectedDistance,
                Station... exactlyExpectedStations) {
            assertThat(pathResponse.getDistance()).isEqualTo(expectedDistance);
            assertThat(pathResponse.getStations()).hasSize(exactlyExpectedStations.length);
            List<PathResponse.StationResponse> stationResponses = pathResponse.getStations();
            for (int i = 0; i < stationResponses.size(); i++) {
                PathResponse.StationResponse stationResponse = stationResponses.get(i);
                Station exactlyExpectedStation = exactlyExpectedStations[i];
                assertThat(stationResponse.getId()).isEqualTo(exactlyExpectedStation.getId());
                assertThat(stationResponse.getName()).isEqualTo(exactlyExpectedStation.getName());
            }
        }
    }

}
