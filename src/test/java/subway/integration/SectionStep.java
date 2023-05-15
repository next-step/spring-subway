package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.SectionRequest;

public final class SectionStep {

    public static ExtractableResponse<Response> 구간_생성_api(long lineId, long downStationId, long upStationId,
                                                          int distance) {
        SectionRequest request = new SectionRequest(downStationId, upStationId, distance);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();
    }

    public static ExtractableResponse<Response> 구간_삭제_api(long lineId, long downStationId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("stationId", downStationId)
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();
    }

}
