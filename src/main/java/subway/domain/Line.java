package subway.domain;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import subway.domain.response.SectionDisconnectResponse;
import subway.domain.status.LineExceptionStatus;
import subway.util.Assert;

public class Line {

    private static final int MIN_DELETABLE_SIZE = 1;

    private final Long id;
    private final Name name;
    private final Color color;
    private final List<Section> sections;

    public Line(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = new Name(name);
        this.color = new Color(color);
        this.sections = sections;
        initSectionConnection(this.sections);
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = new Name(name);
        this.color = new Color(color);
        this.sections = new ArrayList<>();
    }

    public Line(String name, String color, List<Section> sections) {
        this.id = null;
        this.name = new Name(name);
        this.color = new Color(color);
        this.sections = sections;
        initSectionConnection(this.sections);
    }

    private void initSectionConnection(List<Section> sections) {
        for (Section currentSection : sections) {
            Optional<Section> downSection = sections.stream()
                    .filter(section -> currentSection.getDownStation().equals(section.getUpStation())).findFirst();

            downSection.ifPresent(section -> currentSection.connectSection(downSection.get()));
        }
    }

    public List<Station> getSortedStations() {
        Section section = sections.get(0).findUpSection();
        return getSortedStations(section);
    }

    private List<Station> getSortedStations(Section section) {
        List<Station> sortedStations = new ArrayList<>();
        while (section.getDownSection() != null) {
            sortedStations.add(section.getUpStation());
            section = section.getDownSection();
        }
        sortedStations.add(section.getUpStation());
        sortedStations.add(section.getDownStation());
        return sortedStations;
    }

    public Section connectSection(Section requestSection) {
        validExistStations(requestSection.getUpStation(), requestSection.getDownStation());
        Section upSection = sections.get(0).findUpSection();

        Section newSection = upSection.connectSection(requestSection);

        sections.add(newSection);
        return newSection;
    }

    private void validExistStations(Station upStation, Station downStation) {
        boolean isUpStationExists = false;
        boolean isDownStationExists = false;
        for (Section section : sections) {
            isUpStationExists = isStationExists(upStation, isUpStationExists, section);
            isDownStationExists = isStationExists(downStation, isDownStationExists, section);
        }

        validIsDuplicatedSection(upStation, downStation, isUpStationExists, isDownStationExists);
    }

    private void validIsDuplicatedSection(Station upStation, Station downStation, boolean isUpStationExists,
            boolean isDownStationExists) {
        boolean isAllExist = isDownStationExists && isUpStationExists;

        Assert.isTrue(!isAllExist,
                () -> MessageFormat.format("upStation \"{0}\" 과 downStation \"{1}\"이 line\"{2}\"에 모두 존재합니다.", upStation,
                        downStation, sections), LineExceptionStatus.DUPLICATED_SECTIONS.getStatus());
    }

    private boolean isStationExists(Station upStation, boolean isUpStationExists, Section section) {
        isUpStationExists = isUpStationExists
                || section.getUpStation().equals(upStation)
                || section.getDownStation().equals(upStation);
        return isUpStationExists;
    }

    public SectionDisconnectResponse disconnectSection(Station station) {
        Assert.isTrue(sections.size() > MIN_DELETABLE_SIZE, () -> "line에 구간이 하나만 있으면, 구간을 삭제할 수 없습니다.",
                LineExceptionStatus.DISCONNECT_FAIL_DELETABLE_SIZE.getStatus());
        Section downSection = sections.get(0).findDownSection();

        Section upSection = downSection.findUpSection();

        SectionDisconnectResponse sectionDisconnectResponse = upSection.disconnectStation(station);
        sections.remove(sectionDisconnectResponse.getDeletedSection());

        return sectionDisconnectResponse;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.value;
    }

    public String getColor() {
        return color.value;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Line)) {
            return false;
        }
        Line line = (Line) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name)
                && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sections=" + sections +
                '}';
    }
}
