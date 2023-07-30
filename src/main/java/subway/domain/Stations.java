package subway.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stations {
    private final Set<Station> stations;

    public Stations(final Set<Station> stations) {
        this.stations = Collections.unmodifiableSet(stations);
    }

    public static Stations of(final List<Section> sections) {
        Set<Station> stations = sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toUnmodifiableSet());
        return new Stations(stations);
    }

    public boolean contains(Station station) {
        return stations.contains(station);
    }

    public Stations subtract(Stations other) {
        Set<Station> result = new HashSet<>(stations);
        result.removeAll(other.stations);

        return new Stations(result);
    }

    public Optional<Station> findAny() {
        return stations.stream().findAny();
    }

    public Set<Station> getStations() {
        return stations;
    }
}
