package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.PathRequest;
import subway.dto.StationRequest;
import subway.fixture.TestFixture;

public class PathIntegrationTest extends IntegrationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        StationRequest stationRequest = new StationRequest("남부터미널");
        StationRequest stationRequest2 = new StationRequest("교대역");
        StationRequest stationRequest3 = new StationRequest("고속터미널");

        ExtractableResponse<Response> station1 = TestFixture.createStation(stationRequest);
        ExtractableResponse<Response> station2 = TestFixture.createStation(stationRequest2);
        ExtractableResponse<Response> station3 = TestFixture.createStation(stationRequest3);
    }

    @Test
    @DisplayName("시작점과 도착점이 주어지면, 역과 최단 거리를 반환한다.")
    void test() {
        // given
        long targetId = 1L;
        long sourceId = 3L;
        PathRequest pathRequest = new PathRequest(sourceId, targetId);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(APPLICATION_JSON_VALUE)
            .body(pathRequest)
            .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
            .then().log().all()
            .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


}
