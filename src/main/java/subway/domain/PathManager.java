package subway.domain;

import java.util.List;

public final class PathManager {

    private PathManager() {
    }

    public static PathManager create(final List<Station> stations, final List<Section> sections) {
        return new PathManager();
    }
}
