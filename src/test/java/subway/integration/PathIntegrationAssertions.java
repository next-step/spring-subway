package subway.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;

class PathIntegrationAssertions {

    private PathIntegrationAssertions() {
        throw new UnsupportedOperationException("Cannot invoke constructor \"PathIntegrationAssertions()\"");
    }

    static void assertStationPath(ExtractableResponse<Response> response, int stationSize) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.distance).isInstanceOf(Number.class);
        assertThat(pathResponse.getStations()).hasSize(stationSize);
        pathResponse.getStations().forEach(
                stationResponse -> {
                    assertThat(stationResponse.getId()).isInstanceOf(Number.class);
                    assertThat(stationResponse.getName()).isNotEmpty().isInstanceOf(String.class);
                }
        );
    }

    private static final class PathResponse {

        private final List<StationResponse> stations;
        private final int distance;

        public PathResponse(List<StationResponse> stations, int distance) {
            this.stations = stations;
            this.distance = distance;
        }

        public List<StationResponse> getStations() {
            return stations;
        }

        public int getDistance() {
            return distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PathResponse)) {
                return false;
            }
            PathResponse that = (PathResponse) o;
            return distance == that.distance && Objects.equals(stations, that.stations);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stations, distance);
        }

        @Override
        public String toString() {
            return "PathResponse{" +
                    "stations=" + stations +
                    ", distance=" + distance +
                    '}';
        }

        private static final class StationResponse {
            private final long id;
            private final String name;

            public StationResponse(long id, String name) {
                this.id = id;
                this.name = name;
            }

            public long getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof StationResponse)) {
                    return false;
                }
                StationResponse that = (StationResponse) o;
                return id == that.id && Objects.equals(name, that.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, name);
            }

            @Override
            public String toString() {
                return "StationResponse{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }

}
