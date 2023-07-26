package subway.integration.fixture;

import static subway.integration.fixture.CommonIntegrationFixture.post;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.LineCreationRequest;
import subway.dto.request.SectionAdditionRequest;
import subway.dto.request.StationRequest;

public class SubWayFixture {

    public static ExtractableResponse<Response> createLine(LineCreationRequest lineCreationRequest) {
        return post("/lines", lineCreationRequest);
    }

    public static ExtractableResponse<Response> findAllLines() {
        return CommonIntegrationFixture.get("lines");
    }

    public static ExtractableResponse<Response> findLine(Long lineId) {
        return CommonIntegrationFixture.get("/lines/{lineId}", lineId);
    }

    public static ExtractableResponse<Response> deleteLine(Long lineId) {
        return CommonIntegrationFixture.delete("/lines/{lineId}", lineId);
    }

    public static ExtractableResponse<Response> updateLine(Long lineId,
        LineCreationRequest lineCreationRequest) {
        return CommonIntegrationFixture.put("/lines/{lineId}", lineId, lineCreationRequest);
    }

    public static ExtractableResponse<Response> addSectionToLine(Line line,
        SectionAdditionRequest sectionAdditionRequest) {
        return CommonIntegrationFixture.post("/lines/{id}/sections", line.getId(),
            sectionAdditionRequest);
    }

    public static ExtractableResponse<Response> removeStationOfLine(Line line, Station station) {
        return CommonIntegrationFixture.delete("/lines/{id}/sections", line.getId(), "stationId", station.getId());
    }

    public static ExtractableResponse<Response> createStation(StationRequest stationRequest) {
        return post("/stations", stationRequest);
    }

    public static ExtractableResponse<Response> findAllStations() {
        return CommonIntegrationFixture.get("/stations");
    }

    public static ExtractableResponse<Response> findStationById(Long stationId) {
        return CommonIntegrationFixture.get("/stations/{stationId}", stationId);
    }

    public static ExtractableResponse<Response> deleteStationById(long stationId) {
        return CommonIntegrationFixture.delete("/stations/{stationId}", stationId);
    }
}
