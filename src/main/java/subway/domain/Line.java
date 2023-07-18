package subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.springframework.util.Assert;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Section> sections;

    public Line(Long id, String name, String color, List<Section> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }

    public Line(String name, String color, Section section) {
        Assert.notNull(section.getId(), () -> "Section.id가 null일때, Line에 추가 될 수 없습니다.");
        this.name = name;
        this.color = color;
        this.sections = new LinkedList<>(List.of(section));
    }

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new LinkedList<>();
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = new LinkedList<>();
    }

    public void connectSection(Section section) {
        Assert.notNull(section.getId(), () -> "Section.id가 null일때, Line에 추가 될 수 없습니다.");
        Section downSection = getDownSection();
        downSection.connectDownSection(section);
        sections.add(section);
    }

    private Section getDownSection() {
        return sections.stream()
                .filter(section -> section.getDownSection() == null)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Line에 포함된 하행 Section을 찾을 수 없습니다."));
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

    public List<Section> getSections() {
        return sections;
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
                && Objects.equals(color, line.color) && Objects.equals(sections, line.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sections);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sectionList=" + sections +
                '}';
    }
}
