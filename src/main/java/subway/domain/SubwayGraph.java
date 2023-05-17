package subway.domain;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter(AccessLevel.PACKAGE)
public class SubwayGraph {
    /**
     * 해당 역과 연결되어 있는 구간들을 저장합니다.
     * 정방향 탐색에 쓰입니다.
     */
    private Map<Station, Set<Section>> connection = new HashMap<>();

    /**
     * 하행역과 연결되어 있는 구간들을 저장합니다.
     * 역방향 탐색과 값 제거에 쓰입니다.
     */
    private Map<Station, Set<Section>> backwardConnection = new HashMap<>();

    /**
     * 각 호선의 첫번째 역을 저장합니다.
     * 노선별 탐색에 사용됩니다.
     */
    private Map<Line, Station> firstStationInLines = new HashMap<>();

    public SubwayGraph() {}

    /**
     * 지하철 노선 그래프에 구간을 추가합니다.
     * 1. 새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.
     * 2. 노선에 이미 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.
     * @param section
     */
    public void add(Section section) {
        if (!isLastDownStationInLine(section)) {
            throw new IllegalStateException("기존의 하행 종점역에만 추가할 수 있습니다.");
        }

        if (isAlreadyExistsInLine(section.getLine(), section.getDownStation())) {
            throw new IllegalStateException("이미 해당 노선에 존재하는 역은 추가할 수 없습니다.");
        }
        connection.computeIfAbsent(section.getUpStation(), (unused) -> new HashSet<>()).add(section);
        firstStationInLines.putIfAbsent(section.getLine(), section.getUpStation());
        addToBackward(section);
    }

    private boolean isAlreadyExistsInLine(Line line, Station newStation) {
        if(!firstStationInLines.containsKey(line)) {
            return false;
        }
        Station stationInLine = firstStationInLines.get(line);
        if (stationInLine.equals(newStation)) {
            return true;
        }
        while (true) {
            if (!connection.containsKey(stationInLine)) {
                return false;
            }
            Optional<Section> nextSection = connection.get(stationInLine).stream()
                    .filter(section -> section.getLine().equals(line))
                    .findAny();
            if (nextSection.isEmpty()) {
                return false;
            }
            Station nextStation = nextSection.get().getDownStation();
            if (nextStation.equals(newStation)) {
                return true;
            }
            stationInLine = nextStation;
        }
    }

    /**
     * 추가할 구간의 상행역이 해당 라인의 하행 종점역인지 반환합니다.
     * @param section
     * @return
     */
    private boolean isLastDownStationInLine(Section section) {
        if (!connection.containsKey(section.getUpStation())) {
            return true;
        }
        Set<Section> sections = connection.get(section.getUpStation());
        Optional<Section> connectedSectionInSameLine = sections.stream()
                .filter(connectSection
                        -> connectSection.getLine().equals(section.getLine())).findAny();
        return connectedSectionInSameLine.isEmpty();
    }

    private void addToBackward(Section section) {
        backwardConnection.computeIfAbsent(section.getDownStation(),
                (unused) -> new HashSet<>()).add(section);
    }

    /**
     * 지하철 노선에 등록된 역 중 하행 종점역만 제거할 수 있다. 즉, 마지막 구간만 제거할 수 있습니다.
     * @param stationId
     */
    public void remove(Long stationId) {
        Station removeStation = new Station(stationId);
        if(!backwardConnection.containsKey(removeStation)) {
            throw new IllegalStateException("삭제할 역이 존재하지 않습니다.");
        }

        // 삭제할 역의 상행역 찾기
        List<Station> upStations = backwardConnection.get(removeStation).stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());

        if (!isLastDownStation(removeStation)) {
            throw new IllegalStateException("하행 종점역만 제거할 수 있습니다.");
        }

        for (Station upStation: upStations) {
            removeStationConnectedUpStation(upStation, removeStation);
        }
    }

    /**
     * upStation 과 연결돠어 있는 downStation 을 삭제합니다.
     * @param upStation
     * @param removeStation
     */
    private void removeStationConnectedUpStation(Station upStation, Station removeStation) {
        Set<Section> sections = connection.get(upStation);
        Optional<Section> removeSection = sections.stream()
                .filter(section -> section.getDownStation().equals(removeStation))
                .findAny();
        sections.remove(removeSection
                .orElseThrow(() -> new IllegalStateException("upStation과 연결된 downStation 삭제에 실패했습니다.")));
    }

    private boolean isLastDownStation(Station downStation) {
        if(!connection.containsKey(downStation)) {
            return true;
        }
        if (connection.get(downStation).isEmpty()) {
            return true;
        }
        return false;
    }

    public Set<Section> getSections(Station station) {
        return connection.get(station);
    }

}
