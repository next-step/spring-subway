package subway.integration.helper;

import io.restassured.RestAssured;
import org.springframework.http.MediaType;

public abstract class RestHelper {

    public static void post(final Object request, final String path) {
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(path)
                .then();
    }

    public static void post(final Object request, final String path, final Object... pathParam) {
        RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(path, pathParam)
                .then();
    }
}
