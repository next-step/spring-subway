package subway.helper;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;
import subway.domain.Station;
import subway.dto.LineStationsResponse;

import java.util.List;
import java.util.stream.Collectors;

public class GetHelper {

    public static List<String> getStationNamesInLine(Long lineId) {
        ExtractableResponse<Response> lineResponse = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then()
                .extract();

        LineStationsResponse resultResponse = lineResponse.as(LineStationsResponse.class);
        return resultResponse.getStations()
                .stream()
                .map(Station::getName)
                .collect(Collectors.toList());
    }
}
