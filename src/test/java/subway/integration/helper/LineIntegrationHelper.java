package subway.integration.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.dto.request.LineCreateRequest;
import subway.dto.response.LineResponse;

public class LineIntegrationHelper {

    public static Line createLine(final LineCreateRequest lineCreateRequest) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineCreateRequest)
                .when().post("/lines")
                .then().log().all().
                extract();
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        return new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

    public static Line createLine(final String name,
                                  final String color,
                                  final Long upStationId,
                                  final Long downStationId,
                                  final long distance) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LineCreateRequest(name, color, upStationId, downStationId, distance))
                .when().post("/lines")
                .then().log().all().
                extract();
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        return new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

}
