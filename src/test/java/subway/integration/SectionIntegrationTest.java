package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.TestSettingUtils.createLineWith;
import static subway.integration.TestSettingUtils.createSectionWith;
import static subway.integration.TestSettingUtils.createStationsWithNames;
import static subway.integration.TestSettingUtils.extractCreatedId;
import static subway.integration.TestSettingUtils.extractCreatedIds;

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
import subway.dto.SectionAdditionRequest;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void addSection() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역", "서초역"));
        final Long 사당역_ID = stationIds.get(0);
        final Long 방배역_ID = stationIds.get(1);
        final Long 서초역_ID = stationIds.get(2);

        final LineRequest lineRequest = new LineRequest("2호선", 사당역_ID, 방배역_ID, 3,
            "#ff0000");
        final Long lineId = extractCreatedId(createLineWith(lineRequest));
        final SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(방배역_ID,
            서초역_ID, 3);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(sectionAdditionRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineId)
            .when()
            .post("/lines/{id}/sections")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("지하철 노선의 구간을 제거한다.")
    @Test
    void removeSection() {
        //given
        final List<Long> stationIds = extractCreatedIds(createStationsWithNames("사당역", "방배역", "서초역"));
        final Long 사당역_ID = stationIds.get(0);
        final Long 방배역_ID = stationIds.get(1);
        final Long 서초역_ID = stationIds.get(2);

        final LineRequest lineRequest = new LineRequest("2호선", 사당역_ID, 방배역_ID, 3,
            "#ff0000");
        final Long lineId = extractCreatedId(createLineWith(lineRequest));

        final SectionAdditionRequest sectionAdditionRequest = new SectionAdditionRequest(방배역_ID,
            서초역_ID, 3);
        createSectionWith(sectionAdditionRequest, lineId);

        // when
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", lineId)
            .param("stationId", 방배역_ID)
            .when().delete("/lines/{id}/sections")
            .then().log().all().
            extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
