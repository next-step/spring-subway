package subway.integration;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.dto.request.CreateLineRequest;
import subway.dto.request.CreateSectionRequest;
import subway.dto.request.CreateStationRequest;
import subway.dto.response.CreateLineResponse;
import subway.dto.response.FindPathResponse;
import subway.dto.response.FindStationResponse;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.integration.LineIntegrationSupporter.registerSectionToLine;
import static subway.integration.PathIntegrationSupporter.findPath;
import static subway.integration.StationIntegrationSupporter.createStation;
import static subway.integration.LineIntegrationSupporter.createLineByLineRequest;

@DisplayName("지하철 경로 찾기 관련 기능")
public class PathIntegrationTest extends IntegrationTest {

    @DisplayName("시작역과 끝역이 하나의 노선에 존재할 경우, 최적의 경로를 찾아준다.")
    @Test
    void findPathWhenSameLine() {

        /* 역1 -- 10 -- > 역2 -- 5 -- > 역3 */

        // given
        Long stationRequest1 = createStation(new CreateStationRequest("역1")).jsonPath().getLong("id");
        Long stationRequest2 = createStation(new CreateStationRequest("역2")).jsonPath().getLong("id");
        Long stationRequest3 = createStation(new CreateStationRequest("역2")).jsonPath().getLong("id");

        CreateLineRequest lineRequest1 = new CreateLineRequest("노선1", "bg-red-600", stationRequest1, stationRequest2, 10);
        Long lineId = createLineByLineRequest(lineRequest1).body().as(CreateLineResponse.class).getId();

        CreateSectionRequest sectionRequest = new CreateSectionRequest(stationRequest2, stationRequest3, 5);
        registerSectionToLine(lineId, sectionRequest);

        // when
        ExtractableResponse<Response> response = findPath(stationRequest1, stationRequest3);
        FindPathResponse findPathResponse = response.body().as(FindPathResponse.class);

        List<FindStationResponse> stations = findPathResponse.getStations();
        Integer distance = findPathResponse.getDistance();

        // then
        assertThat(stations.size()).isEqualTo(3);
        assertThat(distance).isEqualTo(15);

        List<Integer> expected = Arrays.asList(1, 2, 3);

        for (int i = 0; i < stations.size(); i++) {
            assertThat(stations.get(i).getId()).isEqualTo(expected.get(i));
        }

    }
}
