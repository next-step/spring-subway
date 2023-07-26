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
import subway.exception.SubwayException;

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

        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 15);
        ExtractableResponse<Response> lineResponse = createLine(lineRequest);
        lineId = Long.parseLong(lineResponse.header("Location").split("/")[2]);
    }

    @Test
    @DisplayName("노선 하행 종점이 상행역인 구간을 추가한다.")
    void createSectionTest1() {
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

    // 31
    @Test
    @DisplayName("노선 상행 종점이 하행역인 구간을 추가한다.")
    void createSectionTest2() {
        // when
        SectionRequest sectionRequest = new SectionRequest(station3Id, station1Id, 15);
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

    //13
    @Test
    @DisplayName("상행역이 일치하는 구간에, 새로운 구간을 추가한다.")
    void createSectionTest3() {
        // when
        SectionRequest sectionRequest = new SectionRequest(station1Id, station3Id, 14);
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

    //32
    @Test
    @DisplayName("하행역이 일치하는 구간에, 새로운 구간을 추가한다.")
    void createSectionTest4() {
        // when
        SectionRequest sectionRequest = new SectionRequest(station3Id, station2Id, 14);
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

    // 12
    @Test
    @DisplayName("두 역이 모두 기존 노선에 존재하면, 추가할 수 없다.")
    void createSectionTest5() {
        // when
        SectionRequest sectionRequest = new SectionRequest(station1Id, station2Id, 14);
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

    //34
    @Test
    @DisplayName("노선에 구간이 하나도 포함 되지 않으면, 구간을 추가할 수 없다.")
    void createSectionTest6() {
        // when
        StationRequest stationRequest = new StationRequest("공릉역");
        ExtractableResponse<Response> createStationResponse = createStation(stationRequest);
        Long station4Id = Long.parseLong(createStationResponse.header("Location").split("/")[2]);

        SectionRequest sectionRequest = new SectionRequest(station3Id, station4Id, 14);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(SubwayException.class)).hasMessage("두 역 중 하나는 기존 노선에 포함되어야 합니다");
    }

    // 32
    @Test
    @DisplayName("새로운 구간의 길이가 기존 구간의 길이보다 크거나 같으면, 추가할 수 없다.")
    void createSectionTest7() {
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
        assertThat(response.body().as(SubwayException.class)).hasMessage("새로운 구간의 거리는 기존 노선의 거리보다 작아야 합니다. 새 구간 거리 : 15");
    }

    @Test
    @DisplayName("구간의 두 역이 같은 역일 경우, 구간을 추가할 수 없다.")
    void createSectionTest8() {
        // when
        SectionRequest sectionRequest = new SectionRequest(station2Id, station2Id, 14);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(SubwayException.class)).hasMessage("구간의 상행역과 하행역이 같을 수 없습니다");
    }

    @Test
    @DisplayName("노선에서 하행 종점역을 삭제할 수 있다.")
    void removeDownTerminalStation() {
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
    @DisplayName("노선에서 상행 종점역을 삭제할 수 있다.")
    void removeUpTerminalStationBadRequest() {
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
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, station1Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("종점역이 아닌 경우에도 삭제할 수 있다.")
    void middleStationRemoveTest() {
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
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
        assertThat(response.body().as(SubwayException.class)).hasMessage("노선에 구간이 하나일 때는 삭제할 수 없습니다.");
    }

    @Test
    @DisplayName("노선에서 역이 포함되지 않은 경우 역을 삭제할 수 없다.")
    void noStationRemoveBadRequest() {
        // given
        ExtractableResponse<Response> station4Response = createStation(new StationRequest("몽촌토성역"));
        Long station4Id = Long.parseLong(station4Response.header("Location").split("/")[2]);

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
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, station4Id)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().as(SubwayException.class)).hasMessage("노선에 역이 포함되지 않을 때는 삭제할 수 없습니다.");
    }
}
