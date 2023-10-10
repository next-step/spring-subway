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

    public List<Section> getSections() {
        return sections;
    }

    public int getTotalDistance() {
        int distance = 0;
        for (Section section : sections) {
            distance += section.getDistance();
        }
        return distance;
    }

    public int calculateCharge(int distance) {
        int fare = 1250;
        if (distance > 50) {
            int over50 = distance - 50;
            fare += ((over50 + 8 - 1) / 8) * 100;
            distance = 50;
        }

        if (distance > 10) {
            int over10 = distance -10;
            fare += ((over10 + 5 - 1) / 5) * 100;
        }
        return fare;
    }
}
