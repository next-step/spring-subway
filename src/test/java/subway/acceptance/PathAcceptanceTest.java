package subway.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import subway.acceptance.helper.LineHelper;
import subway.acceptance.helper.RestHelper;
import subway.acceptance.helper.SectionHelper;
import subway.acceptance.helper.StationHelper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("경로 관련 기능 인수 테스트")
class PathAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        setUpStations();
        setUpLines();
        setUpSections();
    }

    @Test
    @DisplayName("최단 경로를 조회한다.")
    void paths() {
        /* given */
        final Long source = 1L;
        final Long target = 7L;

        /* when */
        final ExtractableResponse<Response> response
                = RestHelper.get("/paths", Map.of("source", source, "target", target));

        /* then */
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final int anInt = response.jsonPath().getInt("distance");
        System.out.println("anInt = " + anInt);
    }

    private void setUpStations() {
        StationHelper.createStation("잠실");
        StationHelper.createStation("구로디지털단지");
        StationHelper.createStation("강남");
        StationHelper.createStation("역삼");
        StationHelper.createStation("선릉");
        StationHelper.createStation("교대");
        StationHelper.createStation("신대방");
        StationHelper.createStation("봉천");
        StationHelper.createStation("서초");
    }

    private void setUpLines() {
        LineHelper.createLine("1호선", "남색", 1L, 2L, 16);
        LineHelper.createLine("2호선", "초록색", 2L, 4L, 12);
        LineHelper.createLine("3호선", "주황색", 3L, 4L, 15);
    }

    private void setUpSections() {
        SectionHelper.createSection(1L, 2L, 1L, 1);
        SectionHelper.createSection(1L, 1L, 3L, 9);
        SectionHelper.createSection(1L, 1L, 4L, 35);

        SectionHelper.createSection(2L, 2L, 5L, 25);

        SectionHelper.createSection(3L, 3L, 6L, 22);
        SectionHelper.createSection(3L, 4L, 5L, 14);
        SectionHelper.createSection(3L, 4L, 6L, 17);
        SectionHelper.createSection(3L, 4L, 7L, 19);
        SectionHelper.createSection(3L, 5L, 7L, 8);
        SectionHelper.createSection(3L, 6L, 7L, 14);
        SectionHelper.createSection(3L, 8L, 9L, 777);
    }
}
