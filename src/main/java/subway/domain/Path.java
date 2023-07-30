package subway.domain;

import subway.dto.response.FindPathResponse;

import java.util.List;

public class Path {

    private final List<Section> sections;

    public Path(List<Section> sections) {
        this.sections = sections;
    }

    public FindPathResponse find(Station startStation, Station endStation) {
        return null;
    }
}
