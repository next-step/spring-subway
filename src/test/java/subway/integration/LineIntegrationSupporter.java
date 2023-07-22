package subway.integration;

import static io.restassured.RestAssured.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;

class LineIntegrationSupporter {

    static ExtractableResponse<Response> createLineByLineRequest(LineRequest lineRequest) {
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

    static ExtractableResponse<Response> getLineByLineId(Long lineId) {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> updateLineByLineId(Long lineId, LineRequest lineRequest) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> deleteLineByLineId(Long lineId) {
        return given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> registerSectionToLine(Long lineId, SectionRequest sectionRequest) {
        return given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> deleteSectionByLineIdAndStationId(Long lineId, Long stationId) {
        return given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all()
                .extract();
    }
}
