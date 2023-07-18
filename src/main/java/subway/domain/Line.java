package subway.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Line {
    private Long id;
    private String name;
    private String color;
    private List<Section> sectionList;

    public Line(Long id, String name, String color, List<Section> sectionList) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sectionList = sectionList;
    }

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sectionList = new LinkedList<>();
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sectionList = new LinkedList<>();
    }

    public void addSection(Section section) {
        
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

    public List<Section> getSectionList() {
        return sectionList;
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
                && Objects.equals(color, line.color) && Objects.equals(sectionList, line.sectionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, sectionList);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", sectionList=" + sectionList +
                '}';
    }
}
