package subway.domain;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.springframework.util.Assert.isTrue;

public class LineSections {
    protected static final int MINIMUM_SIZE = 1;
    protected List<Section> sections;

    public LineSections(final List<Section> sections) {
        isTrue(sections.size() >= MINIMUM_SIZE, "노선에 등록된 구간은 반드시 한 개 이상이어야합니다.");
        this.sections = new ArrayList<>(sections);
    }

    public void addSection(final Section newSection) {
        List<Station> upStations = getUpStations();
        List<Station> downStations = getDownStations();
        throwIfAllOrNothingMatchInLine(newSection);
        updateIfMatchUpStation(newSection, upStations);
        updateIfMatchDownStation(newSection, downStations);
        addNewSection(newSection);
    }

    private void throwIfAllOrNothingMatchInLine(final Section newSection) {
        final List<Station> stations = toStations();
        if (stations.contains(newSection.getUpStation()) == stations.contains(newSection.getDownStation())) {
            throw new IllegalArgumentException("라인에 포함되어 있는 세션 중 삽입하고자 하는 세션의 상행 , 하행 정보가 반드시 하나만 포함해야합니다.");
        }
    }

    private void updateIfMatchDownStation(final Section newSection, final List<Station> stations) {
        if (stations.contains(newSection.getDownStation())) {
            final Section originSection = findOriginDownStationSectionByStation(newSection.getDownStation());
            updateSection(newSection, originSection, originSection.getUpStation(), newSection.getUpStation());
        }
    }

    private void updateIfMatchUpStation(final Section newSection, final List<Station> stations) {
        if (stations.contains(newSection.getUpStation())) {
            final Section originSection = findOriginUpStationSectionByStation(newSection.getUpStation());
            updateSection(newSection, originSection, newSection.getDownStation(), originSection.getDownStation());
        }
    }

    private Section findOriginDownStationSectionByStation(final Station station) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("새로운 하행 구간과 매치되는 기존 하행 구간을 찾지 못했습니다."));
    }

    private Section findOriginUpStationSectionByStation(final Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(station))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("새로운 상행 구간과 매치되는 기존 상행 구간을 찾지 못했습니다."));
    }

    private void addNewSection(final Section newSection) {
        sections.add(newSection);
    }

    private void updateSection(final Section newSection,
                               final Section originSection,
                               final Station upStation,
                               final Station downStation) {
        final Line line = newSection.getLine();
        sections.remove(originSection);
        sections.add(new Section(line, upStation, downStation, originSection.subtractDistance(newSection)));
    }

    public void deleteSection(final Station station) {
        validateDeleteConstraint();
        throwIfNotContain(station);
        removeIfMatchFirstSection(station);
        removeIfMatchLastSection(station);
        removeIfMatchMiddleSection(station);
    }

    private void throwIfNotContain(final Station station) {
        if (!toStations().contains(station)) {
            throw new IllegalArgumentException("구간에서 역을 찾을 수 없습니다.");
        }
    }

    private void removeIfMatchFirstSection(final Station station) {
        if (getUpStations().contains(station)
                && !getDownStations().contains(station)) {
            sections.remove(findOriginUpStationSectionByStation(station));
        }
    }

    private void removeIfMatchLastSection(final Station station) {
        if (getDownStations().contains(station)
                && !getUpStations().contains(station)) {
            sections.remove(findOriginDownStationSectionByStation(station));
        }
    }

    private void removeIfMatchMiddleSection(final Station station) {
        if (getUpStations().contains(station)
                && getDownStations().contains(station)) {
            final Section upSection = findOriginDownStationSectionByStation(station);
            final Section downSection = findOriginUpStationSectionByStation(station);
            sections.remove(upSection);
            sections.remove(downSection);
            sections.add(new Section(
                    upSection.getLine(),
                    upSection.getUpStation(),
                    downSection.getDownStation(),
                    upSection.addDistance(downSection)));
        }
    }

    private void validateDeleteConstraint() {
        if (sectionLength() <= MINIMUM_SIZE) {
            throw new IllegalArgumentException("노선에 등록된 구간이 한 개 이하이면 제거할 수 없습니다.");
        }
    }

    public List<Station> toStations() {
        final List<Station> stations = getUpStations();
        stations.addAll(getDownStations());
        return stations;
    }

    public List<Section> getSections() {
        return sections.stream()
                .collect(toUnmodifiableList());
    }

    private List<Station> getUpStations() {
        return sections.stream()
                .map(Section::getUpStation)
                .collect(toList());
    }

    private List<Station> getDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(toList());
    }

    public int sectionLength() {
        return sections.size();
    }

    @Override
    public String toString() {
        return "LineSections{" +
                "sections=" + sections +
                '}';
    }
}
