package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.helper.TestHelper.createLine;
import static subway.helper.TestHelper.createSection;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineRequest;
import subway.dto.PathResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.helper.TestHelper;

public class PathIntegrationTest extends IntegrationTest {

    private Long station1Id;
    private Long station2Id;
    private Long station3Id;

    @BeforeEach
    public void setUp() {
        super.setUp();
        StationRequest stationRequest = new StationRequest("남부터미널");
        StationRequest stationRequest2 = new StationRequest("교대역");
        StationRequest stationRequest3 = new StationRequest("고속터미널");

        ExtractableResponse<Response> station1 = TestHelper.createStation(stationRequest);
        station1Id = Long.parseLong(station1.header("Location").split("/")[2]);
        ExtractableResponse<Response> station2 = TestHelper.createStation(stationRequest2);
        station2Id = Long.parseLong(station2.header("Location").split("/")[2]);
        ExtractableResponse<Response> station3 = TestHelper.createStation(stationRequest3);
        station3Id = Long.parseLong(station3.header("Location").split("/")[2]);

        LineRequest lineRequest = new LineRequest("2호선", "green", station1Id, station2Id, 15);
        ExtractableResponse<Response> lineResponse = createLine(lineRequest);
        Long lineId = Long.parseLong(lineResponse.header("Location").split("/")[2]);
        SectionRequest sectionRequest = new SectionRequest(station2Id, station3Id, 15);
        createSection(sectionRequest, lineId);
    }

    @Test
    @DisplayName("시작점과 도착점이 주어지면, 역과 최단 거리를 반환한다.")
    void 시작점_도착점_주어지면_역의_최단거리_반환() {
        // given
        long sourceId = station1Id;
        long targetId = station3Id;

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .when().get("/paths?source={sourceId}&target={targetId}", sourceId, targetId)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        PathResponse pathResponse = response.body().as(PathResponse.class);
        assertThat(pathResponse.getStations())
            .extracting("id").containsExactly(station1Id, station2Id, station3Id);
        assertThat(pathResponse).extracting("distance").isEqualTo(30L);
    }


}
