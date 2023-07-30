package subway;

import static subway.RestApiUtils.extractIdFromApiResult;

import subway.ui.dto.LineRequest;
import subway.ui.dto.SectionRequest;
import subway.ui.dto.StationRequest;

public class DomainFixtures {

    public static long createLine(String name, long upStationId, long downStationId) {
        final LineRequest line = new LineRequest(name, upStationId, downStationId, 10, "blue");
        return extractIdFromApiResult(RestApiUtils.post(line, "/lines"));
    }

    public static long createStation(String name) {
        final StationRequest stationRequest = new StationRequest(name);
        return extractIdFromApiResult(RestApiUtils.post(stationRequest, "/stations"));
    }

    public static LineWithStationId createInitialLine(String name, String upStationName,
        String downStationName) {
        long upStationId = createStation(upStationName);
        long downStationId = createStation(downStationName);
        long lineId = createLine(name, upStationId, downStationId);
        return new LineWithStationId(lineId, upStationId, downStationId);
    }

    public static long extendSectionToLine(long lineId, long upStationId, long downStationId) {
        final SectionRequest extendToDownStation = new SectionRequest(
            String.valueOf(upStationId),
            String.valueOf(downStationId),
            10
        );

        return extractIdFromApiResult(
            RestApiUtils.post(extendToDownStation, "/lines/" + lineId + "/sections"));
    }

    public static class LineWithStationId {
        private final long lineId;
        private final long upStationId;
        private final long downStationId;

        public LineWithStationId(long lineId, long upStationId, long downStationId) {
            this.lineId = lineId;
            this.upStationId = upStationId;
            this.downStationId = downStationId;
        }

        public long getLineId() {
            return lineId;
        }

        public long getUpStationId() {
            return upStationId;
        }

        public long getDownStationId() {
            return downStationId;
        }
    }
}
