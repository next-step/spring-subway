package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;

import static io.restassured.RestAssured.given;

public class LineIntegrationSupporter {

    static ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().post("/lines")
                .then().log().all().
                extract();
    }

    static ExtractableResponse<Response> findAllLines() {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> findLine(Long lineId) {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> updateLine(Long lineId, LineRequest lineRequest) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> deleteLine(Long lineId) {
        return given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> createSectionInLine(Long lineId, SectionRequest sectionRequest) {
        return given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
