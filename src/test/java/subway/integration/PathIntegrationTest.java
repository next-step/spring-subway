package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Station;
import subway.dto.PathResponse;
import subway.helper.CreateHelper;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 경로 관련 기능")
class PathIntegrationTest extends IntegrationTest {
    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @DisplayName("경로를 탐색하여 최단 거리를 반환한다.")
    @Test
    void pathFind() {
        // given
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yangjaeId = CreateHelper.createStation("양재역");
        Long southTerminalId = CreateHelper.createStation("남부터미널역");

        Long line2Id = CreateHelper.createLine("2호선", "green", gyodaeId, gangnamId, 10);
        Long shinBundangLineId = CreateHelper.createLine("신분당선", "red", gangnamId, yangjaeId, 10);
        Long line3Id = CreateHelper.createLine("3호선", "orange", gyodaeId, southTerminalId, 2);

        CreateHelper.createSection(southTerminalId, yangjaeId, 3, line3Id);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={startStationId}&target={endStationId}", gyodaeId, yangjaeId)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getStations()
                .stream()
                .map(Station::getId)
        ).containsExactly(gyodaeId, southTerminalId, yangjaeId);
        assertThat(pathResponse.getDistance()).isEqualTo(5);

    }

    @DisplayName("같은 출발역과 도착역으로 탐색을 요청한다.")
    @Test
    void sameStation() {
        // given
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yangjaeId = CreateHelper.createStation("양재역");
        Long southTerminalId = CreateHelper.createStation("남부터미널역");

        Long line2Id = CreateHelper.createLine("2호선", "green", gyodaeId, gangnamId, 10);
        Long shinBundangLineId = CreateHelper.createLine("신분당선", "red", gangnamId, yangjaeId, 10);
        Long line3Id = CreateHelper.createLine("3호선", "orange", gyodaeId, southTerminalId, 2);

        CreateHelper.createSection(southTerminalId, yangjaeId, 3, line3Id);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={startStationId}&target={endStationId}", gyodaeId, gyodaeId)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("연결되어 있지 않은 출발역과 도착역으로 탐색을 요청한다.")
    @Test
    void notConnected() {
        // given
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yangjaeId = CreateHelper.createStation("양재역");
        Long southTerminalId = CreateHelper.createStation("남부터미널역");

        Long line2Id = CreateHelper.createLine("2호선", "green", gyodaeId, gangnamId, 10);
        Long line3Id = CreateHelper.createLine("3호선", "orange", gyodaeId, southTerminalId, 2);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={startStationId}&target={endStationId}", gyodaeId, yangjaeId)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 역으로 탐색을 요청한다.")
    @Test
    void notExist() {
        // given
        Long gyodaeId = CreateHelper.createStation("교대역");
        Long gangnamId = CreateHelper.createStation("강남역");
        Long yangjaeId = CreateHelper.createStation("양재역");
        Long southTerminalId = CreateHelper.createStation("남부터미널역");

        Long line2Id = CreateHelper.createLine("2호선", "green", gyodaeId, gangnamId, 10);
        Long shinBundangLineId = CreateHelper.createLine("신분당선", "red", gangnamId, yangjaeId, 10);
        Long line3Id = CreateHelper.createLine("3호선", "orange", gyodaeId, southTerminalId, 2);

        CreateHelper.createSection(southTerminalId, yangjaeId, 3, line3Id);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={startStationId}&target={endStationId}", gyodaeId, Integer.MAX_VALUE)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
