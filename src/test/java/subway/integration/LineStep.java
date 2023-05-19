package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

public final class LineStep {

    public static final String 신분당선 = "신분당선";
    public static final String BG_RED_600 = "bg-red-600";
    public static final String 구신분당선 = "구신분당선";


    public static ExtractableResponse<Response> 노생_생성_api(String name, String color) {
        LineRequest request = new LineRequest(name, color);

        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/lines")
                .then().log().all().
                extract();
    }

    public static LineResponse 노선_생성_api_응답변환(String name, String color) {
        ExtractableResponse<Response> response = 노생_생성_api(name, color);
        return response.as(LineResponse.class);
    }

    public static ExtractableResponse<Response> 노생_조회_api(long lineId) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();
    }


}
