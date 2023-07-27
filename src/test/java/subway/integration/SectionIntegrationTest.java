package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.SectionRequest;
import subway.helper.CreateHelper;
import subway.helper.RestAssuredHelper;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        final Long firstStationId = CreateHelper.createStation("강남역");
        final Long secondStationId = CreateHelper.createStation("역삼역");
        final Long thirdStationId = CreateHelper.createStation("교대역");
        final Long lineId = CreateHelper.createLine("2호선", "bg-123", firstStationId, secondStationId);

        final SectionRequest params = new SectionRequest(secondStationId, thirdStationId, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines/" + lineId + "/sections", params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 구간을 생성할 때 중복된 역이 있으면 예외를 던진다.")
    @Test
    void createAlreadyExist() {
        // given
        final Long firstStationId = CreateHelper.createStation("강남역");
        final Long secondStationId = CreateHelper.createStation("역삼역");
        final Long thirdStationId = CreateHelper.createStation("교대역");
        final Long lineId = CreateHelper.createLine("2호선", "bg-123", firstStationId, secondStationId);
        CreateHelper.createSection(secondStationId, thirdStationId,  lineId);

        final SectionRequest params = new SectionRequest(thirdStationId, secondStationId, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines/" + lineId + "/sections", params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 구간을 생성할 때 상행역과 하행역이 같으면 예외를 던진다.")
    @Test
    void createSame() {
        // given
        final Long firstStationId = CreateHelper.createStation("강남역");
        final Long secondStationId = CreateHelper.createStation("역삼역");
        final Long lineId = CreateHelper.createLine("2호선", "bg-123", firstStationId, secondStationId);

        final SectionRequest params = new SectionRequest(secondStationId, secondStationId, 10);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.post("/lines/" + lineId + "/sections", params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행 종점역을 제거한다.")
    @Test
    void deleteUpEndStation() {
        // given
        final Long firstStationId = CreateHelper.createStation("강남역");
        final Long secondStationId = CreateHelper.createStation("역삼역");
        final Long thirdStationId = CreateHelper.createStation("교대역");
        final Long lineId = CreateHelper.createLine("2호선", "bg-123", firstStationId, secondStationId);
        CreateHelper.createSection(secondStationId, thirdStationId, lineId);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.delete(
                "/lines/" + lineId + "/sections?stationId=" + firstStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(CreateHelper.getStationCountInLine(lineId)).isEqualTo(2);
    }

    @DisplayName("중간역을 제거한다.")
    @Test
    void deleteMidStation() {
        // given
        final Long firstStationId = CreateHelper.createStation("강남역");
        final Long secondStationId = CreateHelper.createStation("역삼역");
        final Long thirdStationId = CreateHelper.createStation("교대역");
        final Long lineId = CreateHelper.createLine("2호선", "bg-123", firstStationId, secondStationId);
        CreateHelper.createSection(secondStationId, thirdStationId, lineId);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.delete(
                "/lines/" + lineId + "/sections?stationId=" + secondStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(CreateHelper.getStationCountInLine(lineId)).isEqualTo(2);
    }

    @DisplayName("하행 종점역을 제거한다.")
    @Test
    void deleteDownEndStation() {
        // given
        final Long firstStationId = CreateHelper.createStation("강남역");
        final Long secondStationId = CreateHelper.createStation("역삼역");
        final Long thirdStationId = CreateHelper.createStation("교대역");
        final Long lineId = CreateHelper.createLine("2호선", "bg-123", firstStationId, secondStationId);
        CreateHelper.createSection(secondStationId, thirdStationId, lineId);

        // when
        ExtractableResponse<Response> response = RestAssuredHelper.delete(
                "/lines/" + lineId + "/sections?stationId=" + thirdStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(CreateHelper.getStationCountInLine(lineId)).isEqualTo(2);
    }
}
