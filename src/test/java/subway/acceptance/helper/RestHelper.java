package subway.acceptance.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

public class RestHelper {

    public static ExtractableResponse<Response> get(final String path) {
        return defaultJsonRequest()
                .when().get(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(final String path, final List<Object> pathParam) {
        return defaultJsonRequest()
                .when().get(path, pathParam.toArray())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> get(final String path, final Map<String, Object> queryParams) {
        final RequestSpecification jsonRequest = defaultJsonRequest();
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            jsonRequest.queryParam(entry.getKey(), entry.getValue());
        }

        return jsonRequest
                .when().get(path)
                .then().log().all()
                .extract();
    }
    
    public static ExtractableResponse<Response> post(final Object request, final String path) {
        return defaultJsonRequest()
                .body(request)
                .when().post(path)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> post(final Object request, final String path, final List<Object> pathParam) {
        return defaultJsonRequest()
                .body(request)
                .when().post(path, pathParam.toArray())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> put(final Object request, final String uri) {
        return defaultJsonRequest()
                .body(request)
                .when().put(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> put(final Object request, final String uri, final List<Object> pathParam) {
        return defaultJsonRequest()
                .body(request)
                .when().put(uri, pathParam.toArray())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(final String uri) {
        return defaultJsonRequest()
                .when().delete(uri)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(final String uri, final List<Object> pathParam) {
        return defaultJsonRequest()
                .when().delete(uri, pathParam.toArray())
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> delete(
            final String uri,
            final List<Object> pathParam,
            final Map<String, Object> queryParams
    ) {
        final RequestSpecification jsonRequest = defaultJsonRequest();
        for (Map.Entry<String, Object> params : queryParams.entrySet()) {
            jsonRequest.queryParam(params.getKey(), params.getValue());
        }

        return jsonRequest
                .when().delete(uri, pathParam.toArray())
                .then().log().all()
                .extract();
    }

    private static RequestSpecification defaultJsonRequest() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
}
