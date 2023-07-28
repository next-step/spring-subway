package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.ExceptionResponse;
import subway.dto.LineRequest;
import subway.dto.PathResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.exception.ErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.util.TestRequestUtil.createLine;
import static subway.util.TestRequestUtil.createSection;
import static subway.util.TestRequestUtil.createStation;
import static subway.util.TestRequestUtil.extractId;

@DisplayName("경로 조회 기능 통합 테스트")
public class PathIntegrationTest extends IntegrationTest {
    @Test
    @DisplayName("최단 경로를 탐색한다.")
    void findShortestPath() {
        // given
        long station1Id = extractId(createStation(new StationRequest("강남역")));
        long station2Id = extractId(createStation(new StationRequest("역삼역")));
        long station3Id = extractId(createStation(new StationRequest("성수역")));
        long station4Id = extractId(createStation(new StationRequest("건대입구역")));
        long line1Id = extractId(createLine(
                new LineRequest("1호선", "green", station1Id, station2Id, 10)
        ));
        long line2Id = extractId(createLine(
                new LineRequest("2호선", "orange", station1Id, station3Id, 11)
        ));

        SectionRequest sectionRequest1 = new SectionRequest(station2Id, station4Id, 10);
        createSection(line1Id, sectionRequest1);

        SectionRequest sectionRequest2 = new SectionRequest(station3Id, station4Id, 10);
        createSection(line2Id, sectionRequest2);

        // when
        PathResponse response = RestAssured
                .given().log().all()
                .when().get("/paths?source={station1Id}&target={station4Id}", station1Id, station4Id)
                .then().log().all()
                .extract()
                .body().as(PathResponse.class);

        // then
        assertThat(response.getStations()
                .stream()
                .map(StationResponse::getId))
                .containsExactly(station1Id, station2Id, station4Id);
        assertThat(response.getDistance()).isEqualTo(20);
    }

    @Test
    @DisplayName("출발역이 존재하지 않는 경우 오류가 발생한다.")
    void nonExistSourceThrowError() {
        // given
        long station1Id = extractId(createStation(new StationRequest("강남역")));
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/paths?source={station1Id}&target={station4Id}", 5L, station1Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.STATION_ID_NO_EXIST.getMessage() + 5L);
    }

    @Test
    @DisplayName("도착역이 존재하지 않는 경우 오류가 발생한다.")
    void nonExistTargetThrowError() {
        // given
        long station1Id = extractId(createStation(new StationRequest("강남역")));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/paths?source={station1Id}&target={station4Id}", station1Id, 5L)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.STATION_ID_NO_EXIST.getMessage() + 5L);
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경우 오류가 발생한다.")
    void sameStationsThrowError() {
        // given
        long station1Id = extractId(createStation(new StationRequest("강남역")));
        long station2Id = extractId(createStation(new StationRequest("역삼역")));
        createLine(new LineRequest("1호선", "green", station1Id, station2Id, 10));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/paths?source={station1Id}&target={station4Id}", station1Id, station1Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.PATH_SAME_STATIONS.getMessage());
    }

    @Test
    @DisplayName("등록된 구간이 하나도 없는 경우 오류가 발생한다.")
    void noSectionsThrowError() {
        // given
        long station1Id = extractId(createStation(new StationRequest("강남역")));
        long station2Id = extractId(createStation(new StationRequest("역삼역")));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/paths?source={station1Id}&target={station4Id}", station1Id, station2Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.PATH_NO_SECTIONS.getMessage());
    }

    @Test
    @DisplayName("출발역이 포함된 구간이 없는 경우 오류가 발생한다.")
    void sourceNotInSection() {
        // given
        long station1Id = extractId(createStation(new StationRequest("강남역")));
        long station2Id = extractId(createStation(new StationRequest("역삼역")));
        long station3Id = extractId(createStation(new StationRequest("성수역")));

        createLine(new LineRequest("1호선", "green", station1Id, station2Id, 10));

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/paths?source={station1Id}&target={station4Id}", station3Id, station1Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.STATION_NOT_CONTAINED.getMessage() + station3Id);
    }

    @Test
    @DisplayName("도착역이 포함된 구간이 없는 경우 오류가 발생한다.")
    void targetNotInSection() {
        // given
        long station1Id = extractId(createStation(new StationRequest("강남역")));
        long station2Id = extractId(createStation(new StationRequest("역삼역")));
        long station3Id = extractId(createStation(new StationRequest("성수역")));

        createLine(new LineRequest("1호선", "green", station1Id, station2Id, 10));


        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/paths?source={station1Id}&target={station4Id}", station1Id, station3Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(ExceptionResponse.class).getMessage())
                .isEqualTo(ErrorCode.STATION_NOT_CONTAINED.getMessage() + station3Id);
    }
}
