package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.ErrorResponse;
import subway.dto.SectionRequest;

import static org.assertj.core.api.Assertions.assertThat;

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

    public static void 잘못된_요청_검증(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isNotEmpty();
        assertThat(errorResponse.getErrorMessage()).isNotEmpty();
    }

}
