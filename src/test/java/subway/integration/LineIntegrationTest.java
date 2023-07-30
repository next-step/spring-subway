package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.domain.Station;
import subway.dto.LineRequest;
import subway.dto.LineResponse;
import subway.dto.LineWithStationsResponse;
import subway.fixture.LineRequestFixture;
import subway.fixture.SectionRequestFixture;
import subway.fixture.StationFixture;
import subway.fixture.StationRequestFixture;
import subway.integration.config.IntegrationTest;
import subway.integration.supporter.LineIntegrationSupporter;
import subway.integration.supporter.SectionIntegrationSupporter;
import subway.integration.supporter.StationIntegrationSupporter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@IntegrationTest
class LineIntegrationTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        StationIntegrationSupporter.createStations(
                StationRequestFixture.첫번째역_요청(),
                StationRequestFixture.두번째역_요청()
        );

        final LineRequest 신분당선_요청 = LineRequestFixture.신분당선_요청();
        final ExtractableResponse<Response> 신분당선_생성_응답 = LineIntegrationSupporter.createLine(신분당선_요청);

        // then
        assertThat(신분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(신분당선_생성_응답.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        final LineRequest 신분당선_요청 = LineRequestFixture.신분당선_요청();
        LineIntegrationSupporter.createLine(신분당선_요청);

        // when
        final ExtractableResponse<Response> 신분당선_생성_응답 = LineIntegrationSupporter.createLine(신분당선_요청);

        // then
        assertThat(신분당선_생성_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationIntegrationSupporter.createStations(
                StationRequestFixture.첫번째역_요청(),
                StationRequestFixture.두번째역_요청(),
                StationRequestFixture.세번째역_요청(),
                StationRequestFixture.네번째역_요청()
        );

        final LineRequest 신분당선_요청 = LineRequestFixture.신분당선_요청();
        final LineRequest 구신분당선_요청 = LineRequestFixture.구신분당선_요청();
        final ExtractableResponse<Response> 신분당선_생성_응답 = LineIntegrationSupporter.createLine(신분당선_요청);
        final ExtractableResponse<Response> 구신분당선_생성_응답 = LineIntegrationSupporter.createLine(구신분당선_요청);

        // when
        final ExtractableResponse<Response> response = LineIntegrationSupporter.findAllLines();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<Long> expected_모든_역들_ID = Stream.of(신분당선_생성_응답, 구신분당선_생성_응답)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toUnmodifiableList());
        final List<Long> result_모든_역들_ID = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toUnmodifiableList());

        assertThat(result_모든_역들_ID).containsAll(expected_모든_역들_ID);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationIntegrationSupporter.createStations(
                StationRequestFixture.첫번째역_요청(),
                StationRequestFixture.두번째역_요청(),
                StationRequestFixture.세번째역_요청(),
                StationRequestFixture.네번째역_요청()
        );

        final LineRequest 신분당선_요청 = LineRequestFixture.신분당선_요청(1, 2);
        final ExtractableResponse<Response> 신분당선_생성_응답 = LineIntegrationSupporter.createLine(신분당선_요청);
        final Long 신분당선_ID = Long.parseLong(신분당선_생성_응답.header("Location").split("/")[2]);

        SectionIntegrationSupporter.createSectionInLine(신분당선_ID, SectionRequestFixture.create(2, 3));
        SectionIntegrationSupporter.createSectionInLine(신분당선_ID, SectionRequestFixture.create(3, 4));

        // when
        final ExtractableResponse<Response> response = LineIntegrationSupporter.findLine(신분당선_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineWithStationsResponse 신분당선_응답 = response.as(LineWithStationsResponse.class);
        assertThat(신분당선_응답.getId()).isEqualTo(신분당선_ID);

        final List<Station> 신분당선_역들 = 신분당선_응답.getStations()
                .stream()
                .map(stationResponse -> new Station(stationResponse.getId(), stationResponse.getName()))
                .collect(Collectors.toUnmodifiableList());

        assertThat(신분당선_역들).containsExactly(
                StationFixture.첫번째역(),
                StationFixture.두번째역(),
                StationFixture.세번째역(),
                StationFixture.네번째역()
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationIntegrationSupporter.createStations(
                StationRequestFixture.첫번째역_요청(),
                StationRequestFixture.두번째역_요청(),
                StationRequestFixture.세번째역_요청()
        );

        final LineRequest 신분당선_요청 = LineRequestFixture.신분당선_요청();
        final LineRequest 구신분당선_요청 = LineRequestFixture.구신분당선_요청();
        final ExtractableResponse<Response> 신분당선_생성_응답 = LineIntegrationSupporter.createLine(신분당선_요청);

        // when
        final Long 신분당선_ID = Long.parseLong(신분당선_생성_응답.header("Location").split("/")[2]);
        final ExtractableResponse<Response> response = LineIntegrationSupporter.updateLine(신분당선_ID, 구신분당선_요청);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationIntegrationSupporter.createStations(
                StationRequestFixture.첫번째역_요청(),
                StationRequestFixture.두번째역_요청()
        );

        final LineRequest 신분당선_요청 = LineRequestFixture.신분당선_요청();
        final ExtractableResponse<Response> 신분당선_생성_응답 = LineIntegrationSupporter.createLine(신분당선_요청);

        // when
        Long 신분당선_ID = Long.parseLong(신분당선_생성_응답.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = LineIntegrationSupporter.deleteLine(신분당선_ID);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
