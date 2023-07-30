package subway;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class RestApiUtils {

    public static ExtractableResponse<Response> get(String path, Object... pathParams) {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when().get(path, pathParams)
            .then().log().all()
            .extract();
    }

    public static <T> ExtractableResponse<Response> post(T body, String path, Object... pathParams) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(body)
            .when().post(path, pathParams)
            .then().log().all().
            extract();
    }

    public static <T> ExtractableResponse<Response> put(T body, String path,  Object... pathParams) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(body)
            .when().put(path, pathParams)
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> delete(String path, Object... pathParams) {
        return RestAssured
            .given().log().all()
            .when().delete(path, pathParams)
            .then().log().all().
            extract();
    }

    public static long extractIdFromApiResult(ExtractableResponse<Response> apiResponse) {
        RestAssured.defaultParser = Parser.JSON;
        return apiResponse.jsonPath().getLong("id");
    }
}
