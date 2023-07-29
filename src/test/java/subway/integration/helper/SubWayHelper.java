package subway.integration.helper;

import static subway.integration.helper.CommonRestAssuredUtils.post;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.LineCreationRequest;
import subway.dto.request.SectionAdditionRequest;
import subway.dto.request.StationRequest;

public class SubWayHelper {

    public static ExtractableResponse<Response> createLine(LineCreationRequest lineCreationRequest) {
        return post("/lines", lineCreationRequest);
    }

    public static ExtractableResponse<Response> findAllLines() {
        return CommonRestAssuredUtils.get("lines");
    }

    public static ExtractableResponse<Response> findLine(Long lineId) {
        return CommonRestAssuredUtils.get("/lines/{lineId}", lineId);
    }

    public static ExtractableResponse<Response> deleteLine(Long lineId) {
        return CommonRestAssuredUtils.delete("/lines/{lineId}", lineId);
    }

    public static ExtractableResponse<Response> updateLine(Long lineId,
        LineCreationRequest lineCreationRequest) {
        return CommonRestAssuredUtils.put("/lines/{lineId}", lineId, lineCreationRequest);
    }

    public static ExtractableResponse<Response> addSectionToLine(Line line,
        SectionAdditionRequest sectionAdditionRequest) {
        return CommonRestAssuredUtils.post("/lines/{id}/sections", line.getId(),
            sectionAdditionRequest);
    }

    public static ExtractableResponse<Response> addSectionToLine(Long lineId,
        SectionAdditionRequest sectionAdditionRequest) {
        return CommonRestAssuredUtils.post("/lines/{id}/sections", lineId,
            sectionAdditionRequest);
    }

    public static ExtractableResponse<Response> calculateShortestSubwayPath(Long sourceStationId, Long targetStationId) {
        Map<String, Long> params = new HashMap<>();
        params.put("source", sourceStationId);
        params.put("target", targetStationId);
        return CommonRestAssuredUtils.get("/path", params);
    }

    public static ExtractableResponse<Response> removeStationOfLine(Line line, Station station) {
        return CommonRestAssuredUtils.delete("/lines/{id}/sections", line.getId(), "stationId", station.getId());
    }

    public static ExtractableResponse<Response> createStation(StationRequest stationRequest) {
        return post("/stations", stationRequest);
    }

    public static ExtractableResponse<Response> createStation(String stationName) {
        return post("/stations", new StationRequest(stationName));
    }

    public static ExtractableResponse<Response> findAllStations() {
        return CommonRestAssuredUtils.get("/stations");
    }

    public static ExtractableResponse<Response> findStationById(Long stationId) {
        return CommonRestAssuredUtils.get("/stations/{stationId}", stationId);
    }

    public static ExtractableResponse<Response> deleteStationById(long stationId) {
        return CommonRestAssuredUtils.delete("/stations/{stationId}", stationId);
    }
}
