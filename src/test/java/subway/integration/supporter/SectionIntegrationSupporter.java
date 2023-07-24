package subway.integration.supporter;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.SectionRequest;

import static io.restassured.RestAssured.given;

public class SectionIntegrationSupporter {

    public static ExtractableResponse<Response> createSectionInLine(Long lineId, SectionRequest sectionRequest) {
        return given().log().all()
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteSectionInLineByStationId(Long lineId, Long stationId) {
        return given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all()
                .extract();
    }
}
