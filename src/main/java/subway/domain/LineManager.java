package subway.domain;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.util.Assert;

public class LineManager {

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

    public void connectDownSection(Section downSection) {
        validNotDuplicatedDownStation(downSection.getDownStation());

        Section lineDownSection = sections.get(0).findDownSection();
        lineDownSection.connectDownSection(downSection);

        sections.add(downSection);
    }

    private void validNotDuplicatedDownStation(Station downStation) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(downStation)
                        || section.getDownStation().equals(downStation))
                .findAny()
                .ifPresent(section -> {
                    throw new IllegalArgumentException(
                            MessageFormat.format("line에 이미 존재하는 station 입니다. \"{0}\"", downStation));
                });
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
            isUpStationExists = section.getUpStation().equals(upStation) || section.getDownStation().equals(upStation);
            isDownStationExists =
                    section.getDownStation().equals(downStation) || section.getUpStation().equals(downStation);
        }
        boolean isAllExist = isDownStationExists && isUpStationExists;
        Assert.isTrue(!isAllExist,
                () -> MessageFormat.format("upStation \"{0}\" 과 downStation \"{1}\"이 line\"{2}\"에 모두 존재합니다.", upStation,
                        downStation, sections));
    }

    public void disconnectDownSection(Station downStation) {
        Assert.isTrue(sections.size() > 1, () -> "line에 구간이 하나만 있으면, 구간을 삭제할 수 없습니다.");
        Section downSection = sections.get(0).findDownSection();

        Assert.isTrue(downSection.getDownStation().equals(downStation),
                () -> MessageFormat.format("삭제할 station \"{0}\" 은 하행의 downStation \"{1}\" 과 일치해야 합니다.",
                        downStation, downSection.getDownStation()));

        Section upSection = downSection.getUpSection();

        upSection.disconnectDownSection();
        sections.remove(downSection);
    }

}
