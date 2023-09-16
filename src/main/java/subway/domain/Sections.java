package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Sections {

    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addValidate(Station upStation, Station downStation) {
        if (!sections.isEmpty() && !sections.get(sections.size()-1).getDownStation().equals(upStation)) {
            throw new IllegalArgumentException("추가하려는 상행역이 기존의 하행역과 불일치합니다.");
        }
        if (!sections.isEmpty() && findAllStation().contains(downStation)) {
            throw new IllegalArgumentException("이미 노선에 추가되어 있는 역입니다.");
        }
    }

    public List<Station> findAllStation() {
        List<Station> stations = new ArrayList<>();
        for (Section section : sections) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }
        return stations.stream().distinct().collect(Collectors.toList());
    }

    public int getSize() {
        return sections.size();
    }

    public List<Section> getSectionList() {
        return sections;
    }
}
