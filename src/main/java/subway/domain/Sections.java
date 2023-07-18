package subway.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void validNewSection(Section section) {
        if (sections.size() == 0) {
            return;
        }
        validNewSectionDownStation(section);
        validNewSectionUpStation(section);
    }

    private void validNewSectionDownStation(Section section) {
        sections.stream()
            .filter(s -> !s.getUpStation().equals(section.getDownStation()))
            .filter(s -> !s.getDownStation().equals(section.getDownStation()))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("입력하고자 하는 하행역이 해당 노선에 등록되어 있습니다.")
            );
    }

    private void validNewSectionUpStation(Section section) {
        Station endStation = findEndStation();
        if (!endStation.equals(section.getUpStation())) {
            throw new IllegalArgumentException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니다.");
        }
    }

    public void canDeleteStation(Long stationId) {
        if (sections.size() <= 1) {
            throw new IllegalStateException("구간이 1개 이하이므로 해당역을 삭제할 수 없습니다.");
        }
        Station endStation = findEndStation();
        if (endStation.getId() != stationId) {
            throw new IllegalArgumentException("하행 종점역이 아니면 삭제할 수 없습니다.");
        }
    }

    private Station findEndStation() {
        Set<Station> upStations = sections.stream()
            .map(Section::getUpStation)
            .collect(Collectors.toSet());

        Station station = sections.stream()
            .map(Section::getDownStation)
            .filter(downStation -> !upStations.contains(downStation))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("노선이 잘못되었습니다."));
        return station;
    }


}
