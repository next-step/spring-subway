package subway.fixture;

import subway.domain.Station;

public final class StationFixture {

    public static Station 첫번째역() {
        return new Station(1L, "첫번째역");
    }

    public static Station 두번째역() {
        return new Station(2L, "두번째역");
    }

    public static Station 세번째역() {
        return new Station(3L, "세번째역");
    }

    public static Station 네번째역() {
        return new Station(4L, "네번째역");
    }
}
