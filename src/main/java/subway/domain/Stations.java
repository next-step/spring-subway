package subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import subway.exception.IllegalStationsException;
import subway.vo.StationPair;

public class Stations {

    private final List<Station> stations;

    public Stations(final List<StationPair> stationPairs) {
        this.stations = sort(stationPairs);
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    private List<Station> sort(final List<StationPair> stationPairs) {
        validateDuplicateUpStation(stationPairs);
        validateDuplicateDownStation(stationPairs);
        final Map<Station, Station> upToDownStations = convert(stationPairs);
        final Station startStation = findStartStation(upToDownStations);
        return connect(upToDownStations, startStation);
    }

    private List<Station> connect(final Map<Station, Station> upToDownStations, final Station startStation) {
        final List<Station> stations = new ArrayList<>();
        Station currentStation = startStation;
        while(currentStation != null) {
            stations.add(currentStation);
            currentStation = upToDownStations.get(currentStation);
        }
        return stations;
    }

    private Station findStartStation(final Map<Station, Station> upToDownStations) {
        final Set<Station> upStations = new HashSet<>(upToDownStations.keySet());
        final Collection<Station> downStations = upToDownStations.values();

        upStations.removeAll(downStations);
        validateFirstStations(upStations);

        return upStations.stream()
            .findFirst()
            .orElseThrow(() -> new IllegalStationsException("역들이 제대로 연결되지 않았습니다."));
    }

    private Map<Station, Station> convert(final List<StationPair> stationPairs) {
        return stationPairs.stream()
                .collect(Collectors.toMap(
                        StationPair::getUpStation,
                        StationPair::getDownStation)
                );
    }

    private void validateFirstStations(Set<Station> upStations) {
        if (upStations.size() != 1) {
            throw new IllegalStationsException("역들이 제대로 연결되지 않았습니다.");
        }
    }

    private void validateDuplicateUpStation(List<StationPair> stationPairs) {
        long distinctUpStationCount = stationPairs.stream()
            .map(StationPair::getUpStation)
            .distinct()
            .count();
        if (distinctUpStationCount != stationPairs.size()) {
            throw new IllegalStationsException("중복된 역은 노선에 포함될 수 없습니다.");
        }
    }

    private void validateDuplicateDownStation(List<StationPair> stationPairs) {
        long distinctUpStationCount = stationPairs.stream()
            .map(StationPair::getDownStation)
            .distinct()
            .count();
        if (distinctUpStationCount != stationPairs.size()) {
            throw new IllegalStationsException("중복된 역은 노선에 포함될 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Stations stations1 = (Stations) o;
        return Objects.equals(stations, stations1.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stations);
    }

    @Override
    public String toString() {
        return "Stations{" +
            "stations=" + stations +
            '}';
    }
}
