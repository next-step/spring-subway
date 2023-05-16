package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

public final class StationStep {

    public static final String 강남역 = "강남역";
    public static final String 판교역 = "판교역";
    public static final String 정자역 = "정자역";

    public static ExtractableResponse<Response> 역_생성_api(String name) {
        StationRequest request = new StationRequest(name);

        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    public static StationResponse 역_생성_api_응답변환(String name) {
        ExtractableResponse<Response> response = 역_생성_api(name);
        return response.as(StationResponse.class);
    }

}
