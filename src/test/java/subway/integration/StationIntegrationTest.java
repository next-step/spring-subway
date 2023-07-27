package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.integration.supporter.StationIntegrationSupporter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
class StationIntegrationTest extends IntegrationTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        final StationRequest 강남역_요청 = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> 강남역_응답 = StationIntegrationSupporter.createStation(강남역_요청);

        // then
        assertThat(강남역_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(강남역_응답.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        final StationRequest 강남역_요청 = new StationRequest("강남역");
        StationIntegrationSupporter.createStation(강남역_요청);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.createStation(강남역_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStations() {
        /// given
        final StationRequest 강남역_요청 = new StationRequest("강남역");
        final StationRequest 역삼역_요청 = new StationRequest("역삼역");

        ExtractableResponse<Response> 강남역_응답 = StationIntegrationSupporter.createStation(강남역_요청);
        ExtractableResponse<Response> 역삼역_응답 = StationIntegrationSupporter.createStation(역삼역_요청);

        // when
        ExtractableResponse<Response> response = StationIntegrationSupporter.findAllStations();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedStationIds = Stream.of(강남역_응답, 역삼역_응답)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultStationIds = response.jsonPath().getList(".", StationResponse.class).stream()
                .map(StationResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultStationIds).containsAll(expectedStationIds);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        /// given
        final StationRequest 강남역_요청 = new StationRequest("강남역");
        ExtractableResponse<Response> 강남역_생성_응답 = StationIntegrationSupporter.createStation(강남역_요청);

        // when
        final Long 강남역_ID = Long.parseLong(강남역_생성_응답.header("Location").split("/")[2]);
        ExtractableResponse<Response> 강남역_조회_응답 = StationIntegrationSupporter.findStation(강남역_ID);

        // then
        assertThat(강남역_조회_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
        StationResponse 강남역_응답 = 강남역_조회_응답.as(StationResponse.class);
        assertThat(강남역_응답.getId()).isEqualTo(강남역_ID);
    }

    @DisplayName("지하철역을 수정한다.")
    @Test
    void updateStation() {
        // given
        final StationRequest 강남역_요청 = new StationRequest("강남역");

        ExtractableResponse<Response> 강남역_응답 = StationIntegrationSupporter.createStation(강남역_요청);

        // when
        final String uri = 강남역_응답.header("Location");

        final StationRequest 강남역_수정_요청 = new StationRequest("삼성역");
        ExtractableResponse<Response> 삼성역_응답 = StationIntegrationSupporter.updateStation(uri, 강남역_수정_요청);

        // then
        assertThat(삼성역_응답.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        final StationRequest 강남역_요청 = new StationRequest("강남역");

        ExtractableResponse<Response> 강남역_생성_응답 = StationIntegrationSupporter.createStation(강남역_요청);

        // when
        final String uri = 강남역_생성_응답.header("Location");

        ExtractableResponse<Response> 강남역_삭제_응답 = StationIntegrationSupporter.deleteStation(uri);

        // then
        assertThat(강남역_삭제_응답.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
