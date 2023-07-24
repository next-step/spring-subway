package subway.dto;

import subway.domain.Line;
import subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public final class LineWithStationsResponse {

    private final LineResponse lineResponse;
    private final List<StationResponse> stationResponses;

    private LineWithStationsResponse(final LineResponse lineResponse,
                                     final List<StationResponse> stationResponses) {
        this.lineResponse = lineResponse;
        this.stationResponses = stationResponses;
    }

    public static LineWithStationsResponse of(final Line line, List<Station> stations) {
        final List<StationResponse> stationResponses = stations.stream()
                .map(StationResponse::of)
                .collect(Collectors.toUnmodifiableList());
        return new LineWithStationsResponse(LineResponse.of(line), stationResponses);
    }

    public LineResponse getLineResponse() {
        return lineResponse;
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
