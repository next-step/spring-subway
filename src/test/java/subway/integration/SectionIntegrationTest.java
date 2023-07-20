package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.TestRequestUtil.createLine;
import static subway.integration.TestRequestUtil.createStation;

@DisplayName("구간 관련 기능")
public class SectionIntegrationTest extends IntegrationTest {
    private Long station1Id;
    private Long station2Id;
    private Long station3Id;

    private Long lineId;

    @BeforeEach
    public void setUp() {
        super.setUp();

        StationRequest stationRequest1 = new StationRequest("신대방역");
        StationRequest stationRequest2 = new StationRequest("서울대입구역");
        StationRequest stationRequest3 = new StationRequest("잠실역");

        ExtractableResponse<Response> createStation1Response = createStation(stationRequest1);
        station1Id = Long.parseLong(createStation1Response.header("Location").split("/")[2]);

        ExtractableResponse<Response> createStation2Response = createStation(stationRequest2);
        station2Id = Long.parseLong(createStation2Response.header("Location").split("/")[2]);

        ExtractableResponse<Response> createStation3Response = createStation(stationRequest3);
        station3Id = Long.parseLong(createStation3Response.header("Location").split("/")[2]);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 14);
        ExtractableResponse<Response> lineResponse = createLine(lineRequest);
        lineId = Long.parseLong(lineResponse.header("Location").split("/")[2]);
    }

    @Test
    @DisplayName("노선에 구간을 추가한다.")
    void createSectionTest() {
        // when
        SectionRequest sectionRequest = new SectionRequest(station2Id, station3Id, 15);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("기존 하행 종점역이 새로운 구간의 상행역이 아닌 경우 오류를 반환한다.")
    void downTerminalDoesNotMatchNewUpStationSection() {
        // when
        SectionRequest sectionRequest = new SectionRequest(station3Id, station2Id, 15);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선에서 하행 종점역을 삭제할 수 있다.")
    void removeDownStation() {
        // given
        SectionRequest sectionRequest = new SectionRequest(station2Id, station3Id, 15);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, station3Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("노선의 하행 종점역이 아니면 삭제할 수 없다.")
    void removeNotDownStationBadRequest() {
        // given
        SectionRequest sectionRequest = new SectionRequest(station2Id, station3Id, 15);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, station2Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("노선에서 구간이 하나인 경우 역을 삭제할 수 없다.")
    void removeOnlyOneSectionBadRequest() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, station2Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
