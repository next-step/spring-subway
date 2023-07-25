package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.RestApi;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // given
        createInitialLine();

        final String seoulStationId = "3";
        final StationRequest seoul = new StationRequest("서울");
        final SectionRequest extendToSeoul = new SectionRequest("2", seoulStationId, 10);
        RestApi.post(seoul, "stations");

        // when
        ExtractableResponse<Response> response = RestApi.post(extendToSeoul, "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        ExtractableResponse<Response> responseForCheck = RestApi.get("/lines/1");
        assertThat(responseForCheck.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("노선에 존재하지 않는 역으로 인한 구간 생성 실패")
    @Test
    void createSectionWithUnmatchedLineId() {
        // given
        final String notExistStationId = "3";
        SectionRequest extend = new SectionRequest("2", notExistStationId, 10);

        // when
        ExtractableResponse<Response> response = RestApi.post(extend, "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있어서 구간 생성 실패")
    @Test
    void createSectionWithBothExistingStation() {
        // given
        createInitialLine();

        final String duplicateUpStationId = "1";
        final String duplicateDownStationId = "2";
        final SectionRequest badSectionRequest = new SectionRequest(duplicateUpStationId,
            duplicateDownStationId, 5);

        // when
        ExtractableResponse<Response> response = RestApi.post(badSectionRequest,
            "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 모두 노선에 등록되어 있지 않아서 구간 생성 실패")
    @Test
    void createSectionWithBothNotExistingStation() {
        // given
        createInitialLine();

        final String notExistUpStationId = "5";
        final String notExistDownStationId = "6";
        final SectionRequest badSectionRequest = new SectionRequest(notExistUpStationId,
            notExistDownStationId, 5);

        // when
        ExtractableResponse<Response> response = RestApi.post(badSectionRequest,
            "/lines/1/sections");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 중간 구간을 삭제한다.")
    @Test
    void deleteMiddleSection() {
        // given
        final long middleStationId = 3;
        createInitialLine();
        extendSectionsInLine();

        // when
        ExtractableResponse<Response> response = RestApi.delete(
            "/lines/1/sections?stationId=" + middleStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 종점 구간을 삭제한다.")
    @Test
    void deleteLastSection() {
        // given
        final long lastStationId = 4;
        createInitialLine();
        extendSectionsInLine();

        // when
        ExtractableResponse<Response> response = RestApi.delete(
            "/lines/1/sections?stationId=" + lastStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선에 구간이 1개일 때 구간 제거 실패")
    @Test
    void deleteSectionAtLineHasOneSection() {
        // given
        createInitialLine();

        // when
        ExtractableResponse<Response> response = RestApi.delete("/lines/1/sections?stationId=2");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void createInitialLine() {
        final LineRequest line1 = new LineRequest("1호선", 1L, 2L, 10, "blue");
        final StationRequest incheon = new StationRequest("인천");
        final StationRequest bupyeon = new StationRequest("부평");

        RestApi.post(incheon, "/stations");
        RestApi.post(bupyeon, "/stations");
        RestApi.post(line1, "/lines");
    }

    private void extendSectionsInLine() {
        final StationRequest seoul = new StationRequest("서울");
        final StationRequest dongmyo = new StationRequest("동묘앞");
        final SectionRequest extendToSeoul = new SectionRequest("2", "3", 10);
        final SectionRequest extendToDongmyo = new SectionRequest("3", "4", 10);

        RestApi.post(seoul, "/stations");
        RestApi.post(dongmyo, "/stations");
        RestApi.post(extendToSeoul, "/lines/1/sections");
        RestApi.post(extendToDongmyo, "/lines/1/sections");
    }
}
