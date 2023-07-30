package subway.fixture;

import subway.dto.LineRequest;

import static subway.fixture.SectionFixture.DEFAULT_DISTANCE;

public final class LineRequestFixture {

    public static LineRequest 신분당선_요청() {
        return new LineRequest("신분당선", 1L, 2L, DEFAULT_DISTANCE, LineFixture.DEFAULT_COLOR);
    }

    public static LineRequest 신분당선_요청(long upStationId, long downStationId) {
        return new LineRequest("신분당선", upStationId, downStationId, DEFAULT_DISTANCE, LineFixture.DEFAULT_COLOR);
    }

    public static LineRequest 구신분당선_요청() {
        return new LineRequest("구신분당선", 3L, 4L, DEFAULT_DISTANCE, LineFixture.DEFAULT_COLOR);
    }
}
