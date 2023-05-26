package subway.integration;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import subway.domain.Line;
import subway.domain.Section;
import subway.domain.Station;
import subway.domain.application.SubwayGraphService;
import subway.domain.dto.StationDto;
import subway.domain.dto.SubwayPathDto;
import subway.domain.repository.LineRepository;
import subway.domain.repository.SectionRepository;
import subway.domain.repository.StationRepository;
import subway.domain.vo.Distance;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * 경로 찾기에 사용된 시나리오는 [README test 상황] 참고
 */
@DisplayName("경로 찾기 통합테스트")
@ActiveProfiles("test")
public class PathIntegrationTest extends IntegrationTest {

    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private SubwayGraphService subwayGraphService;

    private Station chungjeong = new Station(1L, "충정로");
    private Station sichung = new Station(2L, "시청");
    private Station uljiro3 = new Station(3L, "을지로 3가");
    private Station seoulyuk = new Station(4L, "서울역");
    private Station jongro3 = new Station(5L, "종로 3가");
    private Station uljiro4 = new Station(6L, "을지로 4가");

    @BeforeEach
    public void dataInit() {
        // line
        lineRepository.insert(new Line(1L, "1호선", "파랑"));
        lineRepository.insert(new Line(2L, "2호선", "초록"));

        // station
        stationRepository.insert(chungjeong);
        stationRepository.insert(sichung);
        stationRepository.insert(uljiro3);
        stationRepository.insert(seoulyuk);
        stationRepository.insert(jongro3);
        stationRepository.insert(uljiro4);

        // section
        sectionRepository.insert(new Section(1L ,new Line(2L, "1"), chungjeong, sichung, new Distance(1)));
        sectionRepository.insert(new Section(2L ,new Line(2L, "1"), sichung, uljiro3, new Distance(1)));
        sectionRepository.insert(new Section(3L ,new Line(2L, "1"), uljiro3, uljiro4, new Distance(10)));
        sectionRepository.insert(new Section(4L ,new Line(1L, "1"), seoulyuk, sichung, new Distance(2)));
        sectionRepository.insert(new Section(5L ,new Line(1L, "1"), sichung, jongro3, new Distance(2)));
        sectionRepository.insert(new Section(6L ,new Line(1L, "1"), jongro3, uljiro4, new Distance(2)));

        // graph init
        subwayGraphService.initGraph();
    }

    @Test
    @DisplayName("최단경로 찾기에 성공한다.")
    void findShortenPathSuccess() {

        // graph init
        RestAssured.given().log().all()
                .when()
                .get("/lines/")
                .then().log().all()
                .extract();

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("startStationId", 2L)
                .queryParam("endStationId", 6L)
                .when()
                .get("/paths")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);

        SubwayPathDto answer = SubwayPathDto.builder()
                .stations(Stream.of(sichung, jongro3, uljiro4).map(StationDto::from).collect(Collectors.toList()))
                .distance(4d)
                .build();
        SubwayPathDto result = response.body().as(SubwayPathDto.class);
        assertThat(result).usingRecursiveComparison().isEqualTo(answer);
    }
}
