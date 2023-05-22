package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.PathResponse;
import subway.dto.SectionAddRequest;
import subway.dto.SectionResponse;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

@DisplayName("지하철 노선 경로 조회 기능")
public class PathIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 노선 경로를 조회할 때 출발역과 도착역을 입력하면 최단거리 및 총 거리를 반환한다.")
    @Test
    void getPaths() {
        // given
        LineResponse 신분당선 = 신분당선_생성();
        StationResponse 신사역 = 역_생성("신사");
        StationResponse 논현역 = 역_생성("논현");
        StationResponse 신논현역 = 역_생성("신논현");
        구간_생성(신분당선.getId(), 신사역.getId(), 논현역.getId());
        구간_생성(신분당선.getId(), 논현역.getId(), 신논현역.getId());

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .queryParam("source", 신사역.getId())
            .queryParam("target", 신논현역.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().log().all()
            .extract();

        // then
        PathResponse pathResponse = response.body().as(PathResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(pathResponse.getDistance()).isEqualTo(2);
        assertThat(pathResponse.getStations().size()).isEqualTo(3);
    }

    @DisplayName("지하철 노선 경로를 조회할 때 출발역과 도착역이 같으면 에러를 반환한다.")
    @Test
    void getPathsFalse() {
        // given
        LineResponse 신분당선 = 신분당선_생성();
        StationResponse 신사역 = 역_생성("신사");
        StationResponse 논현역 = 역_생성("논현");
        StationResponse 신논현역 = 역_생성("신논현");
        구간_생성(신분당선.getId(), 신사역.getId(), 논현역.getId());
        구간_생성(신분당선.getId(), 논현역.getId(), 신논현역.getId());

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .queryParam("source", 신사역.getId())
            .queryParam("target", 신사역.getId())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get("/paths")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private LineResponse 신분당선_생성() {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new LineRequest("신분당선", "Red"))
            .when().post("/lines")
            .then().log().all()
            .extract().body().as(LineResponse.class);
    }

    private StationResponse 역_생성(String name) {
        return RestAssured
            .given().log().all()
            .body(new StationRequest(name))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/stations")
            .then().log().all()
            .extract().body().as(StationResponse.class);
    }

    private SectionResponse 구간_생성(Long lineId, Long upStationId, Long downStationId) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(upStationId, downStationId, 1))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract().body().as(SectionResponse.class);
    }
}
