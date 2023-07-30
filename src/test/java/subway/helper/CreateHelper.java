package subway.helper;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineStationsResponse;
import subway.dto.SectionRequest;
import subway.dto.StationResponse;

public class CreateHelper {

    public static int getStationCountInLine(final Long lineId) {
        ExtractableResponse<Response> response = RestAssuredHelper.get("/lines/" + lineId);

        return response.jsonPath().getObject(".", LineStationsResponse.class)
                .getStations().size();
    }

    public static void createSection(final Long upStationId, final Long downStationId, final Long lineId,
            final Integer distance) {
        SectionRequest params = new SectionRequest(upStationId, downStationId, distance);

        RestAssuredHelper.post("/lines/" + lineId + "/sections", params);
    }

    public static Long createLine(String name, String color, long upStationId, long downStationId,
            final Integer distance) {
        LineRequest lineRequest = new LineRequest(name, color, upStationId, downStationId, distance);

        ExtractableResponse<Response> lineResponse = RestAssuredHelper.post("/lines", lineRequest);

        return lineResponse.as(LineResponse.class).getId();
    }

    public static Long createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssuredHelper.post("/stations", params);

        return response.as(StationResponse.class).getId();
    }
}
