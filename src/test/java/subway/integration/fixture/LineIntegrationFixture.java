package subway.integration.fixture;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.dto.request.CreateLineRequest;
import subway.dto.response.LineResponse;

public class LineIntegrationFixture {

    public static Line createLine(final CreateLineRequest createLineRequest) {
        final ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createLineRequest)
                .when().post("/lines")
                .then().log().all().
                extract();
        final LineResponse lineResponse = response.body().as(LineResponse.class);
        return new Line(lineResponse.getId(), lineResponse.getName(), lineResponse.getColor());
    }

}
