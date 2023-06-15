package subway.application.path;

import org.junit.jupiter.params.provider.Arguments;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FindShortestPathParam {

    public static Stream<Arguments> findShortestPathSource() {
        return Stream.of(
                Arguments.of(1, 3,
                        new PathResponse(getStationResponses(1, 2, 3), 5, 1_250),
                        "id=1에서 id=3까지의 최단 경로"),
                Arguments.of(1, 5,
                        new PathResponse(getStationResponses(1, 2, 3, 4, 5), 18, 1_450),
                        "id=1에서 id=5까지의 최단 경로"),
                Arguments.of(1, 7,
                        new PathResponse(getStationResponses(1, 2, 3, 4, 5, 6, 7), 78, 2_450),
                        "id=1에서 id=7까지의 최단 경로"),
                Arguments.of(1, 10,
                        new PathResponse(getStationResponses(1, 2, 3, 9, 10), 15, 1_350),
                        "id=1에서 id=10까지의 최단 경로"),
                Arguments.of(1, 11,
                        new PathResponse(getStationResponses(1, 2, 3, 9, 10, 11), 19, 1_450),
                        "id=1에서 id=11까지의 최단 경로"),
                Arguments.of(10, 14,
                        new PathResponse(getStationResponses(10, 9, 3, 4, 5, 14), 29, 1_650),
                        "id=10에서 id=14까지의 최단 경로"),
                Arguments.of(14, 10,
                        new PathResponse(getStationResponses(14, 5, 4, 3, 9, 10), 29, 1_650),
                        "id=14에서 id=10까지의 최단 경로"),
                Arguments.of(4, 16,
                        new PathResponse(getStationResponses(4, 5, 14, 15, 16), 18, 1_450),
                        "id=4에서 id=16까지의 최단 경로")
        );
    }

    private static List<StationResponse> getStationResponses(long... ids) {
        List<StationResponse> list = new ArrayList<>();
        for (Long id : ids) {
            Station station = new Station(id, null);
            list.add(StationResponse.of(station));
        }
        return list;
    }


}
