package subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.springframework.util.Assert.isTrue;

public class Sections {
    protected static final int MINIMUM_SIZE = 1;
    protected List<Section> sections;

    public Sections(final List<Section> sections) {
        isTrue(sections.size() >= MINIMUM_SIZE, "노선에 등록된 구간은 반드시 한개 이상이어야합니다.");
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
            final Section originSection = findOriginSectionByDownStation(newSection);
            updateSection(newSection, originSection, originSection.getUpStation(), newSection.getUpStation());
        }
    }

    private void updateIfMatchUpStation(final Section newSection, final List<Station> stations) {
        if (stations.contains(newSection.getUpStation())) {
            final Section originSection = findOriginSectionByUpStation(newSection);
            updateSection(newSection, originSection, newSection.getDownStation(), originSection.getDownStation());
        }
    }

    private Section findOriginSectionByDownStation(final Section newSection) {
        return sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("새로운 하행 구간과 매치되는 기존 하행 구간을 찾지 못했습니다."));
    }

    private Section findOriginSectionByUpStation(final Section newSection) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
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
        final long distance = originSection.getDistance() - newSection.getDistance();
        sections.remove(originSection);
        sections.add(new Section(originSection.getId(), line, upStation, downStation, new Distance(distance)));
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
