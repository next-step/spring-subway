package subway.fixture;

import subway.dto.SectionRequest;

public final class SectionRequestFixture {

    public static SectionRequest createSection(long upStationId, long downStationId) {
        return new SectionRequest(String.valueOf(upStationId), String.valueOf(downStationId), SectionFixture.DEFAULT_DISTANCE);
    }
}
