package subway.domain;

import java.text.MessageFormat;
import java.util.List;
import org.springframework.util.Assert;

public class LineManager {

    private final Line line;
    private final List<Section> sections;

    public LineManager(Line line, List<Section> sections) {
        this.line = line;
        this.sections = sections;
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

    public void connectSection(Section requestSection) {
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
