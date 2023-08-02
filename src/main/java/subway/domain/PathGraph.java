package subway.domain;

import java.util.List;

public interface PathGraph {

    List<Station> findRoute();

    Long findDistance();
}
