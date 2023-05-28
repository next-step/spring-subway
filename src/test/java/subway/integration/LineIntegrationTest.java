package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.application.SubwayGraphService;
import subway.domain.dto.LineDto;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.domain.vo.Distance;
import subway.web.dto.LineRequest;
import subway.web.dto.LineResponse;
import subway.web.dto.SectionRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 통합테스트")
@ActiveProfiles("test")
public class LineIntegrationTest extends IntegrationTest {
    private LineRequest lineRequest1;
    private LineRequest lineRequest2;
    private SectionRequest sectionRequest1;
    private SectionRequest sectionRequest2;

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private SubwayGraphService subwayGraphService;

    public LineIntegrationTest() {
        lineRequest1 = new LineRequest("신분당선", "bg-red-600");
        lineRequest2 = new LineRequest("구신분당선", "bg-red-600");

        sectionRequest1 = SectionRequest.builder()
                .downStationId(2L)
                .upStationId(1L)
                .distance(10)
                .build();
        sectionRequest2 = SectionRequest.builder()
                .downStationId(3L)
                .upStationId(2L)
                .distance(5)
                .build();
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
        // line
        Line line1 = new Line(1L, "1호선", "red");
        Line line2 = new Line(2L, "2호선", "blue");
        lineRepository.insert(line1);
        lineRepository.insert(line2);

        // station
        Station station1 = new Station(1L, "강남역");
        Station station2 = new Station(2L, "진해역");
        Station station3 = new Station(3L, "동해역");
        Station station4 = new Station(4L, "서해역");
        Station station5 = new Station(5L, "남해역");
        stationRepository.insert(station1);
        stationRepository.insert(station2);
        stationRepository.insert(station3);
        stationRepository.insert(station4);
        stationRepository.insert(station5);

        // section
        sectionRepository.insert(new Section(1L, line1, station1, station2, new Distance(10)));
        sectionRepository.insert(new Section(2L, line1, station2, station3, new Distance(10)));
        sectionRepository.insert(new Section(3L, line1, station3, station4, new Distance(10)));

        subwayGraphService.initGraph();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("전체 지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("특정 지하철 노선을 조회한다.")
    @Test
    void getLine() {
        Long lineId = 1L;

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineDto lineDto = response.as(LineDto.class);
        assertThat(lineDto.getId()).isEqualTo(lineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest2)
                .when().put("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(lineRequest1)
                .when().post("/lines")
                .then().log().all().
                extract();

        // when
        Long lineId = Long.parseLong(createResponse.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().delete("/lines/{lineId}", lineId)
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("노선에 구간(역)을 등록합니다")
    void addStationToLine() {
        // given
        Long lineId = 1L;
        Long downStationId = 5L;
        Long upStationId = 4L;
        Integer distance = 10;

        SectionRequest sectionRequest = SectionRequest.builder()
                .downStationId(downStationId)
                .upStationId(upStationId)
                .distance(distance)
                .build();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .pathParam("id", lineId)
                .body(sectionRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{id}/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("노선에 있는 구간(역)을 제거합니다")
    void removeStationToLine() {
        // given
        Long lineId = 1L;
        Long stationId = 4L;    // 해당 호선의 하행 종점역 이어야 합니다

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .pathParam("lineId", lineId)
                .queryParam("stationId", stationId)
                .when()
                .delete("/lines/{lineId}/sections")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(204);
    }
}
