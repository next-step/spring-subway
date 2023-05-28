package subway.domain.searchGraph;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SearchGraphErrorMessage {
    public static final String SEARCH_GRAPH_FAILED_TO_ADD
            = "탐색 그래프에 값 추가를 실패했습니다.";
    public static final String SEARCH_GRAPH_NOT_CONTAINS_STATION
            = "탐색 그래프가 해당 역을 찾을 수 없습니다.";
    public static final String SEARCH_GRAPH_CANNOT_FIND_PATH
            = "탐색 그래프가 구간에 해당하는 경로를 찾을 수 없습니다.";
}
