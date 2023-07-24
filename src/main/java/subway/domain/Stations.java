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
import java.util.stream.Stream;
import subway.exception.IllegalStationsException;

public class Stations {

    private final List<Station> stations;

    public Stations(final List<StationPair> stationPairs) {
        this.stations = sort(stationPairs);
    }

    private List<Station> sort(final List<StationPair> stationPairs) {
        validate(stationPairs);
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
        return upStations.stream()
                .findAny()
                .orElseThrow(() -> new IllegalStationsException("역들이 제대로 연결되지 않았습니다."));
    }

    private Map<Station, Station> convert(final List<StationPair> stationPairs) {
        return stationPairs.stream()
                .collect(Collectors.toMap(
                        StationPair::getUpStation,
                        StationPair::getDownStation)
                );
    }

    private void validate(List<StationPair> stationPairs) {
        int totalStationSize = stationPairs.size() * 2;
        Set<Station> distinctStations = stationPairs.stream()
            .flatMap(stationPair -> Stream.of(stationPair.getUpStation(), stationPair.getDownStation()))
            .collect(Collectors.toSet());
        if (distinctStations.size() != totalStationSize) {
            throw new IllegalStationsException("중복된 역은 노선에 포함될 수 없습니다.");
        }
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
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
