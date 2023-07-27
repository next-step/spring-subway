package subway.fixture;

import subway.dto.SectionRequest;

public final class SectionRequestFixture {

    public static final int DEFAULT_DISTANCE = 10;

    public static SectionRequest create(long upStationId, long downStationId) {
        return new SectionRequest(String.valueOf(upStationId), String.valueOf(downStationId), DEFAULT_DISTANCE);
    }
}
