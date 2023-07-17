package subway.domain;

import java.util.List;
import java.util.Objects;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 이상의 구간이 있어야 합니다.");
        }
        this.sections = sections;
    }


    public void insert(Section newSection) {
        if (!Objects.equals(newSection.getUpStationId(), sections.get(sections.size() - 1).getDownStationId())) {
            throw new IllegalArgumentException("새로운 구간의 상행역은 기존 하행 종점역과 같아야 합니다.");
        }

        Long newDownStationId = newSection.getDownStationId();
        for (Section section : sections) {
            if (section.getUpStationId() == newDownStationId) {
                throw new IllegalArgumentException("새로운 구간의 하행역은 기존 노선에 등록되어 있지 않은 역이어야 합니다.");
            }
        }

        sections.add(newSection);
    }
}
