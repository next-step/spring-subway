package subway.integration;

import static io.restassured.RestAssured.given;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.LineCreateRequest;
import subway.dto.LineUpdateRequest;
import subway.dto.SectionCreateRequest;

class LineIntegrationSupporter {

    static ExtractableResponse<Response> createLineByLineRequest(LineCreateRequest lineCreateRequest) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineCreateRequest)
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

    static ExtractableResponse<Response> getLineByLineId(long lineId) {
        return given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> updateLineByLineId(long lineId, LineUpdateRequest lineUpdateRequest) {
        return given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineUpdateRequest)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> deleteLineByLineId(long lineId) {
        return given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> registerSectionToLine(long lineId, SectionCreateRequest sectionCreateRequest) {
        return given().log().all()
                .body(sectionCreateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> deleteSectionByLineIdAndStationId(long lineId, long stationId) {
        return given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all()
                .extract();
    }
}
