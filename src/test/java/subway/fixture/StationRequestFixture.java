package subway.fixture;

import subway.dto.StationRequest;

public final class StationRequestFixture {

    public static StationRequest 첫번째역_요청() {
        return new StationRequest("첫번째역");
    }

    public static StationRequest 두번째역_요청() {
        return new StationRequest("두번째역");
    }

    public static StationRequest 세번째역_요청() {
        return new StationRequest("세번째역");
    }

    public static StationRequest 네번째역_요청() {
        return new StationRequest("네번째역");
    }
}
