package subway.integration.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.domain.Station;
import subway.dto.response.StationResponse;

import java.util.Map;

public class StationIntegrationHelper {
    public static Station createStation(final String name) {
        final Map<String, String> params = Map.of("name", name);
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
        final StationResponse stationResponse = response.body().as(StationResponse.class);
        return new Station(stationResponse.getId(), stationResponse.getName());
    }
}
