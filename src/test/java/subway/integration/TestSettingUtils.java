package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionAdditionRequest;
import subway.dto.StationRequest;

public class TestSettingUtils {

    static ExtractableResponse<Response> createLineWith(LineRequest lineRequest) {
        return RestAssured
            .given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest)
            .when().post("/lines")
            .then()
            .extract();
    }

    static ExtractableResponse<Response> createStationWith(StationRequest stationRequest) {
        return RestAssured
            .given()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then()
            .extract();
    }

    static List<ExtractableResponse<Response>> createStationsWithNames(String... names) {
        return Arrays.stream(names)
            .map(StationRequest::new)
            .map(TestSettingUtils::createStationWith)
            .collect(Collectors.toUnmodifiableList());
    }


    static ExtractableResponse<Response> createSectionWith(SectionAdditionRequest sectionAdditionRequest, Long lineId) {
        return RestAssured
            .given()
            .body(sectionAdditionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineId)
            .when()
            .post("/lines/{id}/sections")
            .then()
            .extract();
    }

    static List<Long> extractCreatedIds(List<ExtractableResponse<Response>> responses) {
        return responses.stream()
            .map(response -> Long.parseLong(response.header("Location").split("/")[2]))
            .collect(Collectors.toUnmodifiableList());
    }

    static Long extractCreatedId(ExtractableResponse<Response> response) {
        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
