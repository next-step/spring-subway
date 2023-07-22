package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.CreateLineRequest;
import subway.dto.response.LineResponse;
import subway.dto.response.LineWithStationsResponse;
import subway.integration.fixture.LineIntegrationFixture;
import subway.integration.fixture.StationIntegrationFixture;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineIntegrationTest extends IntegrationTest {

    private CreateLineRequest createLineRequestA;
    private CreateLineRequest createLineRequestB;

    @BeforeEach
    public void setUp() {
        super.setUp();

        Station upStation = StationIntegrationFixture.createStation(Map.of("name", "낙성대"));
        Station downStation = StationIntegrationFixture.createStation(Map.of("name", "사당"));
        Station station = StationIntegrationFixture.createStation(Map.of("name", "방배"));

        createLineRequestA = new CreateLineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10L);
        createLineRequestB = new CreateLineRequest("2호선", "bg-red-600", upStation.getId(), station.getId(), 10L);

    }

    @DisplayName("지하철 노선을 생성하면서 첫 구간도 함께 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createLineRequestA)
                .when().post("/lines")
                .then().log().all().
                extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        LineIntegrationFixture.createLine(createLineRequestA);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createLineRequestA)
                .when().post("/lines")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        final Line lineA = LineIntegrationFixture.createLine(createLineRequestA);
        final Line lineB = LineIntegrationFixture.createLine(createLineRequestB);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).contains(lineA.getId(), lineB.getId());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        final Line line = LineIntegrationFixture.createLine(createLineRequestA);

        // when
        Long lineId = line.getId();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineWithStationsResponse resultResponse = response.as(LineWithStationsResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final Line line = LineIntegrationFixture.createLine(createLineRequestA);

        // when
        Long lineId = line.getId();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(createLineRequestB)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        final Line line = LineIntegrationFixture.createLine(createLineRequestB);

        // when
        Long lineId = line.getId();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
