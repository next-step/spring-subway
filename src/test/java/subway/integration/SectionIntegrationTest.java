package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import subway.api.dto.LineRequest;
import subway.api.dto.LineResponse;
import subway.api.dto.SectionRequest;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.service.LineService;
import subway.service.StationService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 구간 관련 기능")
public class SectionIntegrationTest extends IntegrationTest {
    @Autowired
    private LineService lineService;
    @Autowired
    private StationService stationService;
    private Line line;
    private Station station1;
    private Station station2;
    private Station station3;


    @BeforeEach
    public void setUp() {
        super.setUp();

        line = new Line("신분당선", "bg-red-600");
        lineService.saveLine(line);
        station1 = new Station("강남");
        station2 = new Station("신논현");
        station3 = new Station("논현");
        stationService.saveStation(station1);
        stationService.saveStation(station2);
        stationService.saveStation(station3);
    }

    @DisplayName("지하철 노선에 구간을 등록한다.")
    @Test
    void createSection() {
        // given
        SectionRequest sectionRequest1 = new SectionRequest(2L, 1L, 10);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/1/sections")
                .then().log().all().
                extract();
        // section 저장
        SectionRequest sectionRequest2 = new SectionRequest(3L, 2L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/1/sections")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("새로운 구간의 상행역이 해당 노선에 등록되어있는 하행 종점역이 아니면 에러를 낸다.")
    @Test
    void createSectionInvalidUpStation() {
        // given
        SectionRequest sectionRequest1 = new SectionRequest(2L, 1L, 10);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/1/sections")
                .then().log().all().
                extract();
        SectionRequest sectionRequest2 = new SectionRequest(3L, 1L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/1/sections")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.body().asString()).isEqualTo("노선의 하행 종점역이 새 구간의 상행역과 같지 않습니다.");
    }

    @DisplayName("노선에 이미 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다.")
    @Test
    void createSectionAlreadyRegistered() {
        // given
        SectionRequest sectionRequest1 = new SectionRequest(2L, 1L, 10);
        SectionRequest sectionRequest2 = new SectionRequest(3L, 2L, 1);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/1/sections")
                .then().log().all().extract();
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/1/sections")
                .then().log().all().extract();
        SectionRequest sectionRequest3 = new SectionRequest(3L, 1L, 1);

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest3)
                .when().post("/lines/1/sections")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        SectionRequest sectionRequest1 = new SectionRequest(2L, 1L, 10);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/1/sections")
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("stationId", 2)
                .when().delete("/lines/1/sections")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("하행 종점역이 아닌 구간을 제거하면 에러를 낸다.")
    @Test
    void deleteInvalidSection() {
        // given
        SectionRequest sectionRequest1 = new SectionRequest(2L, 1L, 10);
        SectionRequest sectionRequest2 = new SectionRequest(3L, 2L, 1);
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest1)
                .when().post("/lines/1/sections")
                .then().log().all().
                extract();
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest2)
                .when().post("/lines/1/sections")
                .then().log().all().
                extract();

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("stationId", 1)
                .when().delete("/lines/1/sections")
                .then().log().all().
                extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
