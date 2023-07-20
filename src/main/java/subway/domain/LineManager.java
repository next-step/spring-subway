package subway.domain;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.util.Assert;

public class LineManager {

    private static final int MIN_DELETABLE_SIZE = 1;

    private final Line line;
    private final List<Section> sections;

    public LineManager(Line line, List<Section> sections) {
        this.line = line;
        this.sections = sections;
        initSectionConnection(this.sections);
    }


    private void initSectionConnection(List<Section> sections) {
        for (Section currentSection : sections) {
            Optional<Section> downSection = sections.stream()
                    .filter(section -> currentSection.getDownStation().equals(section.getUpStation())).findFirst();

            downSection.ifPresent(section -> currentSection.connectDownSection(downSection.get()));
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

        boolean isAllExist = isDownStationExists && isUpStationExists;
        Assert.isTrue(!isAllExist,
                () -> MessageFormat.format("upStation \"{0}\" 과 downStation \"{1}\"이 line\"{2}\"에 모두 존재합니다.", upStation,
                        downStation, sections));
    }

    private boolean isStationExists(Station upStation, boolean isUpStationExists, Section section) {
        isUpStationExists = isUpStationExists
                || section.getUpStation().equals(upStation)
                || section.getDownStation().equals(upStation);
        return isUpStationExists;
    }

    public void disconnectDownSection(Station downStation) {
        Assert.isTrue(sections.size() > MIN_DELETABLE_SIZE, () -> "line에 구간이 하나만 있으면, 구간을 삭제할 수 없습니다.");
        Section downSection = sections.get(0).findDownSection();

        Assert.isTrue(downSection.getDownStation().equals(downStation),
                () -> MessageFormat.format("삭제할 station \"{0}\" 은 하행의 downStation \"{1}\" 과 일치해야 합니다.",
                        downStation, downSection.getDownStation()));

        Section upSection = downSection.getUpSection();

        upSection.disconnectDownSection();
        sections.remove(downSection);
    }

}
