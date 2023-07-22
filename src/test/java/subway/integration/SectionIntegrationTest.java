package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.domain.Line;
import subway.domain.Station;
import subway.dto.request.CreateLineRequest;
import subway.dto.request.SectionRequest;
import subway.integration.fixture.LineIntegrationFixture;
import subway.integration.fixture.SectionIntegrationFixture;
import subway.integration.fixture.StationIntegrationFixture;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
class SectionIntegrationTest extends IntegrationTest {

    private Long lineId;
    private SectionRequest sectionRequestA;
    private SectionRequest sectionRequestB;
    private SectionRequest distanceSectionBadRequest;
    private SectionRequest existBothBadRequest;
    private SectionRequest bothNotingBadRequest;

    @BeforeEach
    public void setUp() {
        super.setUp();

        Station upStation = StationIntegrationFixture.createStation(Map.of("name", "낙성대"));
        Station downStation = StationIntegrationFixture.createStation(Map.of("name", "사당"));
        Station newStationA = StationIntegrationFixture.createStation(Map.of("name", "방배"));
        Station newStationB = StationIntegrationFixture.createStation(Map.of("name", "서초"));
        Station newStationC = StationIntegrationFixture.createStation(Map.of("name", "교대"));

        final Line line = LineIntegrationFixture.createLine(new CreateLineRequest("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10L));

        lineId = line.getId();
        sectionRequestA = new SectionRequest(downStation.getId(), newStationA.getId(), 10L);
        sectionRequestB = new SectionRequest(newStationA.getId(), newStationB.getId(), 10L);
        distanceSectionBadRequest = new SectionRequest(newStationA.getId(), newStationC.getId(), 100L);
        existBothBadRequest = new SectionRequest(downStation.getId(), upStation.getId(), 5L);
        bothNotingBadRequest = new SectionRequest(newStationB.getId(), newStationC.getId(), 4L);
    }

    @DisplayName("지하철 구간을 생성한다.")
    @Test
    void createSection() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequestA)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void createSectionFailDistance() {
        // given
        SectionIntegrationFixture.createSection(lineId, sectionRequestA);
        SectionIntegrationFixture.createSection(lineId, sectionRequestB);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(distanceSectionBadRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // then
        final String errorMessage = response.body().asString();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorMessage).isEqualTo("거리는 1이상이어야 합니다.");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void createSectionFailBothExist() {
        // given
        SectionIntegrationFixture.createSection(lineId, sectionRequestA);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(existBothBadRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // then
        final String errorMessage = response.body().asString();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorMessage).isEqualTo("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void createSectionFailBothNothing() {
        // given
        SectionIntegrationFixture.createSection(lineId, sectionRequestA);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(bothNotingBadRequest)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all().
                extract();

        // then
        final String errorMessage = response.body().asString();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorMessage).isEqualTo("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
    }

    @DisplayName("지하철 노선 중 마지막 구간을 제거한다.")
    @Test
    void deleteLastSection() {
        // given
        SectionIntegrationFixture.createSection(lineId, sectionRequestA);
        SectionIntegrationFixture.createSection(lineId, sectionRequestB);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", sectionRequestB.getDownStationId())
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선 중 마지막 구간이 아닌 구간을 제거하면 실패한다.")
    @Test
    void deleteNotLastSectionThenBadRequest() {
        // given
        SectionIntegrationFixture.createSection(lineId, sectionRequestA);
        SectionIntegrationFixture.createSection(lineId, sectionRequestB);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", sectionRequestA.getDownStationId())
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        final String errorMessage = response.body().asString();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorMessage).isEqualTo("노선에 등록된 하행 종점역만 제거할 수 있습니다.");
    }

    @DisplayName("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다.")
    @Test
    void deleteConstraint() {
        // given  when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .queryParam("stationId", sectionRequestA.getUpStationId())
                .when().delete("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();

        // then
        final String errorMessage = response.body().asString();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorMessage).isEqualTo("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
    }


}
