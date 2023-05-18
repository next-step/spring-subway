package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.LineResponse;
import subway.dto.StationResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.LineStep.BG_RED_600;
import static subway.integration.LineStep.노선_생성_api_응답변환;
import static subway.integration.LineStep.신분당선;
import static subway.integration.SectionStep.구간_삭제_api;
import static subway.integration.SectionStep.구간_생성_api;
import static subway.integration.SectionStep.잘못된_요청_검증;
import static subway.integration.StationStep.강남역;
import static subway.integration.StationStep.역_생성_api_응답변환;
import static subway.integration.StationStep.정자역;
import static subway.integration.StationStep.판교역;

@DisplayName("지하철 구간 관련 기능")
public class SectionIntegrationTest extends IntegrationTest {

    private long 신분당선_id;
    private long 강남역_id;
    private long 판교역_id;
    private long 정자역_id;

    @BeforeEach
    public void setUp() {
        super.setUp();
        LineResponse lineResponse = 노선_생성_api_응답변환(신분당선, BG_RED_600);
        신분당선_id = lineResponse.getId();

        StationResponse stationResponse1 = 역_생성_api_응답변환(강남역);
        강남역_id = stationResponse1.getId();
        StationResponse stationResponse2 = 역_생성_api_응답변환(판교역);
        판교역_id = stationResponse2.getId();
        StationResponse stationResponse3 = 역_생성_api_응답변환(정자역);
        정자역_id = stationResponse3.getId();
    }

    @DisplayName("지하철 구간을 추가한다.")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> response = 구간_생성_api(신분당선_id, 강남역_id, 판교역_id, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("잘못된 구간 정보로 구간 추가를 요청하면 실패 응답이 온다.")
    @Test
    void createSection_fail() {
        // given
        int failDistance = 0;

        // when
        ExtractableResponse<Response> response = 구간_생성_api(신분당선_id, 강남역_id, 판교역_id, failDistance);

        // then
        잘못된_요청_검증(response);
    }

    @DisplayName("연결된 지하철 구간을 추가 가능하다.")
    @Test
    void createSection_add() {
        // given
        구간_생성_api(신분당선_id, 판교역_id, 강남역_id, 10);

        // when
        ExtractableResponse<Response> response = 구간_생성_api(신분당선_id, 정자역_id, 판교역_id, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        구간_생성_api(신분당선_id, 판교역_id, 강남역_id, 10);
        구간_생성_api(신분당선_id, 정자역_id, 판교역_id, 10);

        // when
        ExtractableResponse<Response> response = 구간_삭제_api(신분당선_id, 정자역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("마지막이 아닌 구간을 삭제할 수 없다.")
    @Test
    void deleteSection_fail() {
        // given
        구간_생성_api(신분당선_id, 판교역_id, 강남역_id, 10);
        구간_생성_api(신분당선_id, 정자역_id, 판교역_id, 10);

        // when
        ExtractableResponse<Response> response = 구간_삭제_api(신분당선_id, 강남역_id);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("없는 역을 구간으로 추가한다.")
    @Test
    void createSection_validate() {
        // given
        long failStationId = 999L;

        // when
        ExtractableResponse<Response> response = 구간_생성_api(신분당선_id, failStationId, 판교역_id, 10);

        // then
        잘못된_요청_검증(response);
    }

}
