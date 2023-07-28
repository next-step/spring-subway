package subway.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.PathResponse;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.util.TestRequestUtil.createLine;
import static subway.util.TestRequestUtil.createStation;
import static subway.util.TestRequestUtil.extractId;

@DisplayName("경로 조회 기능 통합 테스트")
public class PathIntegrationTest extends IntegrationTest {
    @Test
    void name() {
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
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/{line1Id}/sections", line1Id)
                .then().log().all()
                .extract();

        SectionRequest sectionRequest2 = new SectionRequest(station3Id, station4Id, 10);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/{line2Id}/sections", line2Id)
                .then().log().all()
                .extract();

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
}
