package subway.domain.searchGraph;

import subway.domain.Section;
import subway.domain.Station;
import subway.domain.vo.SubwayPath;

import java.util.List;

public interface SearchGraph {
    /**
     * 역을 추가합니다.
     * @param station
     */
    void addStation(Station station);

    /**
     * 구간을 추가합니다.
     * @param section
     */
    void addSection(Section section);

    /**
     * 구간을 제거합니다.
     * @Param section
     */
    void removeSection(Section section);

    /**
     * 최단경로를 구합니다.
     * @Param 탐색 시작 역
     * @Param 탐색 도착 역
     * @return GraphPath - 경로, 최단거리
     */
    SubwayPath findShortenPath(Station startStation, Station endStation);

    /**
     * 저장된 역을 반환합니다.
     * @return List of Station
     */
    List<Station> getStations();
}
