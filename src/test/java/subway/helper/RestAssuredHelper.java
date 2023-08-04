package subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.springframework.http.MediaType;

public class RestAssuredHelper {

    public static ExtractableResponse<Response> get(final String path) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(final String path, final Map<String, Object> params) {
        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .params(params)
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> post(final String path, final Object body) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> put(final String path, final Object body) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().put(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(final String path) {
        return RestAssured
                .given().log().all()
                .when().delete(path)
                .then().log().all()
                .extract();
    }
}
