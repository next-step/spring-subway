package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import subway.dto.SectionAddRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineIntegrationTest extends IntegrationTest {
    private LineRequest lineRequest1;
    private LineRequest lineRequest2;
    private LineRequest lineRequest3;
    private Station 반석역;
    private Station 지족역;
    private Station 노은역;
    private Station 월드컵경기장역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        lineRequest2 = new LineRequest("구신분당선", "bg-red-600");
        lineRequest3 = new LineRequest("대전1호선", "Gray");
        반석역 = new Station(1L, "반석");
        지족역 = new Station(2L, "지족");
        노은역 = new Station(3L, "노은");
        월드컵경기장역 = new Station(4L, "월드컵경기장");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
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
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
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
        ExtractableResponse<Response> createResponse1 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        ExtractableResponse<Response> createResponse2 = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest2)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse resultResponse = response.as(LineResponse.class);
        assertThat(resultResponse.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest2)
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
        ExtractableResponse<Response> createResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest3)
            .when().post("/lines")
            .then().log().all().
            extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 지족역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선에 구간을 추가할 때 등록된 역이 있으면 에러를 반환한다.")
    @Test
    void addSectionFalse() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest3)
            .when().post("/lines")
            .then().log().all().
            extract();

        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 지족역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(지족역.getId(), 반석역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간을 추가할 때 갈림길이 있으면 에러를 반환한다.")
    @Test
    void addSectionFalse2() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest3)
            .when().post("/lines")
            .then().log().all().
            extract();

        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 지족역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 노은역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간을 추가할 때 등록할 상행역이 하행종점역이 아니면 에러를 반환한다.")
    @Test
    void addSectionFalse3() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest3)
            .when().post("/lines")
            .then().log().all().
            extract();

        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 지족역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 월드컵경기장역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선에 구간을 추가할 때 상행역, 하행역, 거리가 null 이거나 거리가 1미만이면 에러를 반환한다.")
    @ParameterizedTest
    @MethodSource("provideValueForValidatorSectionAddRequest")
    void addSectionFalse4(Long upStationId, Long downStationId, Integer distance) {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest3)
            .when().post("/lines")
            .then().log().all().
            extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(upStationId, downStationId, distance))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private static Stream<Arguments> provideValueForValidatorSectionAddRequest() {
        return Stream.of(Arguments.of(null, 1L, 1),
            Arguments.of(1L, null, 1),
            Arguments.of(1L, 1L, null),
            Arguments.of(1L, 1L, 0));
    }

    @DisplayName("지하철 노선에 구간을 제거한다.")
    @Test
    void removeSection() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest3)
            .when().post("/lines")
            .then().log().all().
            extract();

        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 지족역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .queryParam("stationId", 지족역.getId())
            .when().delete("/lines/{lienId}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선에 구간을 제거할 때 하행 종점역이 아니면 에러를 반환한다.")
    @Test
    void removeFalse() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(lineRequest3)
            .when().post("/lines")
            .then().log().all().
            extract();

        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(new SectionAddRequest(반석역.getId(), 지족역.getId(), 10))
            .when().post("/lines/{lineId}/sections", lineId)
            .then().log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .queryParam("stationId", 반석역.getId())
            .when().delete("/lines/{lienId}/sections", lineId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
