package subway.helper;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;

public class TestHelper {

    public static ExtractableResponse<Response> createLine(final LineRequest lineRequest) {
        return RestAssured
            .given().log().all()
            .contentType(APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> createStation(final StationRequest stationRequest) {
        return RestAssured
            .given().log().all()
            .contentType(APPLICATION_JSON_VALUE)
            .body(stationRequest)
            .when().post("/stations")
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> createSection(final SectionRequest sectionRequest,
        final Long lineId) {
        return RestAssured
            .given().log().all()
            .contentType(APPLICATION_JSON_VALUE)
            .body(sectionRequest)
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();
    }

}
