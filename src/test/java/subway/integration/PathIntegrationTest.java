package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.dto.PathResponse;
import subway.dto.StationResponse;
import subway.integration.config.IntegrationTest;
import subway.integration.supporter.PathIntegrationSupporter;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.fixture.LineRequestFixture.*;
import static subway.fixture.SectionFixture.DEFAULT_DISTANCE;
import static subway.fixture.SectionRequestFixture.createSection;
import static subway.fixture.StationRequestFixture.*;
import static subway.integration.supporter.LineIntegrationSupporter.createLine;
import static subway.integration.supporter.SectionIntegrationSupporter.createSectionInLine;
import static subway.integration.supporter.StationIntegrationSupporter.createStation;

@DisplayName("지하철 경로 관련 기능")
@IntegrationTest
class PathIntegrationTest {

    private long 범계역_ID;
    private long 경마공원역_ID;
    private long 사당역_ID;
    private long 신용산역_ID;
    private long 강남역_ID;
    private long 잠실역_ID;
    private long 여의도역_ID;
    private long 노량진역_ID;

    /*
          <지하철 노선도>
          범계 -10- 경마공원 -10- 사당 -10- 신용산
                     |         |
                     50       10
                     ㄴ ------ 강남 -10- 잠실
          여의도 -10- 노량진

          - 4호선: 범계 ~ 신용산
          - 2호선: 사당 ~ 잠실
          - 신분당선: 경마공원 ~ 강남
          - 9호선: 여의도 ~ 노량진
     */

    @BeforeEach
    public void init_지하철_노선도() {
        // 역(station) 생성
        this.범계역_ID = createStation(범계역_요청()).jsonPath().getLong("id");
        this.경마공원역_ID = createStation(경마공원역_요청()).jsonPath().getLong("id");
        this.사당역_ID = createStation(사당역_요청()).jsonPath().getLong("id");
        this.신용산역_ID = createStation(신용산역_요청()).jsonPath().getLong("id");
        this.강남역_ID = createStation(강남역_요청()).jsonPath().getLong("id");
        this.잠실역_ID = createStation(잠실역_요청()).jsonPath().getLong("id");
        this.여의도역_ID = createStation(여의도역_요청()).jsonPath().getLong("id");
        this.노량진역_ID = createStation(노량진역_요청()).jsonPath().getLong("id");

        // 노선(line) 생성
        final long 사호선_ID = createLine(사호선_요청(this.범계역_ID, this.경마공원역_ID)).jsonPath().getLong("id");
        final long 이호선_ID = createLine(이호선_요청(this.사당역_ID, this.강남역_ID)).jsonPath().getLong("id");
        final long 구호선_ID = createLine(구호선_요청(this.여의도역_ID, this.노량진역_ID)).jsonPath().getLong("id");
        final long 신분당선_ID = createLine(신분당선_요청(this.경마공원역_ID, this.강남역_ID, DEFAULT_DISTANCE * 5)).jsonPath().getLong("id");

        // 구간(section) 생성
        createSectionInLine(사호선_ID, createSection(this.경마공원역_ID, this.사당역_ID));
        createSectionInLine(사호선_ID, createSection(this.사당역_ID, this.신용산역_ID));
        createSectionInLine(이호선_ID, createSection(this.강남역_ID, this.잠실역_ID));
        createSectionInLine(신분당선_ID, createSection(this.경마공원역_ID, this.강남역_ID));
        createSectionInLine(구호선_ID, createSection(this.여의도역_ID, this.노량진역_ID));
    }

    @DisplayName("지하철 경로를 조회하는 데 성공한다.")
    @Test
    void getPath() {
        // when
        final ExtractableResponse<Response> response = PathIntegrationSupporter.findPath(범계역_ID, 잠실역_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final PathResponse 범계역_잠실역_경로 = response.as(PathResponse.class);
        assertThat(범계역_잠실역_경로.getDistance()).isEqualTo(DEFAULT_DISTANCE * 4);

        final List<Long> stationsId = 범계역_잠실역_경로.getStations()
                .stream()
                .map(StationResponse::getId)
                .collect(Collectors.toUnmodifiableList());
        assertThat(stationsId).containsExactly(범계역_ID, 경마공원역_ID, 사당역_ID, 강남역_ID, 잠실역_ID);
    }

    @DisplayName("출발역과 도착역이 같아 지하철 경로를 조회하는 데 실패한다.")
    @Test
    void getPathWithSameStations() {
        // when
        final ExtractableResponse<Response> response = PathIntegrationSupporter.findPath(범계역_ID, 범계역_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("출발역과 도착역은 같을 수 없습니다.");
    }

    @DisplayName("출발역 또는 도착역이 존재하지 않아 지하철 경로를 조회하는 데 실패한다.")
    @Test
    void getPathWithStationNotExist() {
        // when
        final int notExistId = -1;
        final ExtractableResponse<Response> response1 = PathIntegrationSupporter.findPath(notExistId, 범계역_ID);
        final ExtractableResponse<Response> response2 = PathIntegrationSupporter.findPath(범계역_ID, notExistId);

        // then
        assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response1.body().jsonPath().getString("message"))
                .isEqualTo("해당 id(" + notExistId + ")를 가지는 역이 존재하지 않습니다.");

        assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.body().jsonPath().getString("message"))
                .isEqualTo("해당 id(" + notExistId + ")를 가지는 역이 존재하지 않습니다.");
    }

    @DisplayName("출발역과 도착역을 연결하는 경로가 없어 지하철 경로를 조회하는 데 실패한다.")
    @Test
    void getPathWithNotConnectedStations() {
        // when
        final ExtractableResponse<Response> response = PathIntegrationSupporter.findPath(범계역_ID, 노량진역_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().jsonPath().getString("message"))
                .isEqualTo("출발역과 도착역을 연결하는 경로가 존재하지 않습니다.");
    }
}
