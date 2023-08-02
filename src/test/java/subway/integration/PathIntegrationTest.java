package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.PathResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

@DisplayName("경로 조회 기능 인수 테스트")
class PathIntegrationTest extends IntegrationTest {

    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long stationId5;
    private Long stationId6;
    private Long lineId1;
    private Long lineId2;

    @BeforeEach
    public void setUp() {
        super.setUp();
        setUpStationsAndLine();
    }

    @DisplayName("하나의 노선에서 경로 반환")
    @Test
    void findOneLinePath() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId1)
                .queryParam("target", stationId2)
                .when().get("/paths")
                .then().log().all()
                .extract();
        List<StationResponse> stations = response.as(PathResponse.class).getStations();
        Long distance = response.as(PathResponse.class).getDistance();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(2),
                () -> assertThat(stations.get(0).getId()).isEqualTo(stationId1),
                () -> assertThat(stations.get(1).getId()).isEqualTo(stationId2),
                () -> assertThat(distance).isEqualTo(10L)
        );
    }

    @DisplayName("환승 한번하는 경로 반환")
    @Test
    void findOneTransferPath() {
        // given
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId2, stationId3, 13L))
                .when().post("/lines/{lineId}/sections", lineId1)
                .then().extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId1)
                .queryParam("target", stationId4)
                .when().get("/paths")
                .then().log().all()
                .extract();
        List<StationResponse> stations = response.as(PathResponse.class).getStations();
        Long distance = response.as(PathResponse.class).getDistance();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(4),
                () -> assertThat(stations.get(0).getId()).isEqualTo(stationId1),
                () -> assertThat(stations.get(1).getId()).isEqualTo(stationId2),
                () -> assertThat(stations.get(2).getId()).isEqualTo(stationId3),
                () -> assertThat(stations.get(3).getId()).isEqualTo(stationId4),
                () -> assertThat(distance).isEqualTo(33L)
        );
    }

    @DisplayName("환승 여러번하는 경로 반환")
    @Test
    void findMultiTransferPath() {
        // given
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId2, stationId5, 9999L))
                .when().post("/lines/{lineId}/sections", lineId1)
                .then().extract();
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId5, stationId6, 5L))
                .when().post("/lines/{lineId}/sections", lineId1)
                .then().extract();
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId2, stationId3, 5L))
                .when().post("/lines/{lineId}/sections", lineId2)
                .then().extract();
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId4, stationId5, 5L))
                .when().post("/lines/{lineId}/sections", lineId2)
                .then().extract();
        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new SectionRequest(stationId5, stationId6, 10L))
                .when().post("/lines/{lineId}/sections", lineId2)
                .then().extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId1)
                .queryParam("target", stationId6)
                .when().get("/paths")
                .then().log().all()
                .extract();
        List<StationResponse> stations = response.as(PathResponse.class).getStations();
        Long distance = response.as(PathResponse.class).getDistance();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(stations).hasSize(6),
                () -> assertThat(stations.get(0).getId()).isEqualTo(stationId1),
                () -> assertThat(stations.get(1).getId()).isEqualTo(stationId2),
                () -> assertThat(stations.get(2).getId()).isEqualTo(stationId3),
                () -> assertThat(stations.get(3).getId()).isEqualTo(stationId4),
                () -> assertThat(stations.get(4).getId()).isEqualTo(stationId5),
                () -> assertThat(stations.get(5).getId()).isEqualTo(stationId6),
                () -> assertThat(distance).isEqualTo(35L)
        );
    }

    @DisplayName("데이터 베이스에 출발역 없으면 예외발생")
    @Test
    void noSourceIdInDB() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", 9999L)
                .queryParam("target", stationId2)
                .when().get("/paths")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("데이터베이스에 도착역 없으면 예외발생")
    @Test
    void noTargetIdInDB() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId1)
                .queryParam("target", 9999L)
                .when().get("/paths")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("출발역이 소속된 노선 없으면 예외발생")
    @Test
    void noSourceInLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId6)
                .queryParam("target", stationId2)
                .when().get("/paths")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("도착역이 소속된 노선 없으면 예외발생")
    @Test
    void noTargetInLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId1)
                .queryParam("target", stationId6)
                .when().get("/paths")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역과 도착역이 같으면 예외발생")
    @Test
    void sameSourceAndTarget() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId1)
                .queryParam("target", stationId1)
                .when().get("/paths")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("출발역과 도착역 사이 연결된 구간 없으면 예외발생")
    @Test
    void noPathExist() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("source", stationId1)
                .queryParam("target", stationId4)
                .when().get("/paths")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void setUpStationsAndLine() {
        stationId1 = postStation("st1");
        stationId2 = postStation("st2");
        stationId3 = postStation("st3");
        stationId4 = postStation("st4");
        stationId5 = postStation("st5");
        stationId6 = postStation("st6");

        lineId1 = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LineRequest("3호선", "#555555", stationId1, stationId2, 10L))
                .when().post("/lines")
                .then()
                .extract()
                .as(LineResponse.class)
                .getId();
        lineId2 = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new LineRequest("4호선", "#665555", stationId3, stationId4, 10L))
                .when().post("/lines")
                .then()
                .extract()
                .as(LineResponse.class)
                .getId();
    }

    private static Long postStation(String name) {
        return RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(new StationRequest(name))
                .when().post("/stations")
                .then()
                .extract()
                .as(StationResponse.class)
                .getId();
    }
}
