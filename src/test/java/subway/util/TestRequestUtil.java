package subway.util;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;

public class TestRequestUtil {
    public static ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> createStation(StationRequest stationRequest) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }

    public static long extractId(ExtractableResponse<Response> createStation1Response) {
        return Long.parseLong(createStation1Response.header("Location").split("/")[2]);
    }

    public static ExtractableResponse<Response> createSection(long line2Id, SectionRequest sectionRequest2) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/{line2Id}/sections", line2Id)
                .then().log().all()
                .extract();
    }
}
