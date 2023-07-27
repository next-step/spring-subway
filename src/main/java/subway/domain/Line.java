package subway.domain;

import subway.exception.LineException;
import subway.exception.StationException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Line {

    private static final int MIN_DELETABLE_SIZE = 2;

    private final Long id;
    private final String name;
    private final String color;
    private final List<Section> sections;

    public Line(final Long id, final String name, final String color, final List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
        initSectionConnection(this.sections);
    }

    public Line(final Long id, final String name, final String color) {
        this(id, name, color, new ArrayList<>());
    }

    public Line(final String name, final String color) {
        this(null, name, color, new ArrayList<>());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    private void initSectionConnection(final List<Section> sections) {
        for (Section currentSection : sections) {
            Optional<Section> downSection = sections.stream()
                    .filter(section -> currentSection.getDownStation().equals(section.getUpStation())).findFirst();

            downSection.ifPresent(section -> currentSection.connectDownSection(downSection.get()));
        }
    }

    public List<Station> getSortedStations() {
        final Section section = sections.get(0).findUpSection();
        return getSortedStations(section);
    }

    private List<Station> getSortedStations(Section section) {
        final List<Station> sortedStations = new ArrayList<>();
        while (section.getDownSection() != null) {
            sortedStations.add(section.getUpStation());
            section = section.getDownSection();
        }
        sortedStations.add(section.getUpStation());
        sortedStations.add(section.getDownStation());
        return sortedStations;
    }

    public Optional<Section> findUpdateSection(final Section newSection) {
        validateStations(newSection.getUpStation(), newSection.getDownStation());
        final Section upSection = sections.get(0).findUpSection();
        return upSection.findUpdateSection(newSection);
    }

    private void validateStations(final Station upStation, final Station downStation) {
        final boolean isUpStationExists = isStationExists(upStation);
        final boolean isDownStationExists = isStationExists(downStation);

        validateExistStations(upStation, downStation, isUpStationExists, isDownStationExists);
    }

    private boolean isStationExists(Station station) {
        return sections.stream()
                .anyMatch(section -> isStationExists(station, section));
    }

    private boolean isStationExists(final Station station, final Section section) {
        return section.getUpStation().equals(station) || section.getDownStation().equals(station);
    }

    private void validateExistStations(
            final Station upStation,
            final Station downStation,
            final boolean isUpStationExists,
            final boolean isDownStationExists
    ) {
        validateAllExistStations(upStation, downStation, isUpStationExists, isDownStationExists);
        validateAllNotExistStations(upStation, downStation, isUpStationExists, isDownStationExists);
    }

    private void validateAllNotExistStations(
            final Station upStation,
            final Station downStation,
            final boolean isUpStationExists,
            final boolean isDownStationExists
    ) {
        if (!isDownStationExists && !isUpStationExists) {
            throw new StationException(
                    MessageFormat.format("upStation \"{0}\" 과 downStation \"{1}\"이 line\"{2}\"에 모두 존재하지 않습니다.",
                            upStation, downStation, sections)
            );
        }
    }

    private void validateAllExistStations(
            final Station upStation,
            final Station downStation,
            final boolean isUpStationExists,
            final boolean isDownStationExists
    ) {
        if (isDownStationExists && isUpStationExists) {
            throw new StationException(
                    MessageFormat.format("upStation \"{0}\" 과 downStation \"{1}\"이 line\"{2}\"에 모두 존재합니다.",
                            upStation, downStation, sections)
            );
        }
    }

    public Section disconnectSection(final Station requestStation) {
        validateLineSize();
        validateExistStation(requestStation);

        final Section upSection = sections.get(0).findUpSection();
        return upSection.disconnectSection(requestStation);
    }

    private void validateExistStation(Station station) {
        if (!isStationExists(station)) {
            throw new StationException(
                    MessageFormat.format("line \"{0}\"에 station \"{1}\"이 존재하지 않습니다.", id, station.getId())
            );
        }
    }

    private void validateLineSize() {
        if (sections.size() < MIN_DELETABLE_SIZE) {
            throw new LineException("line에 구간이 하나만 있으면, 구간을 삭제할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
