package subway.integration.helper;

import static subway.integration.helper.CommonRestAssuredUtils.post;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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

    public static ExtractableResponse<Response> removeStationOfLine(Line line, Station station) {
        return CommonRestAssuredUtils.delete("/lines/{id}/sections", line.getId(), "stationId", station.getId());
    }

    public static ExtractableResponse<Response> createStation(StationRequest stationRequest) {
        return post("/stations", stationRequest);
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
