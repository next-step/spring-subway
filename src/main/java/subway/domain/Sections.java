package subway.domain;

import java.util.LinkedList;
import java.util.List;

public class Sections {

    private final List<Section> values;

    public Sections(List<Section> values) {
        this.values = new LinkedList<>(values);
    }

    public void addLast(Section section) {
        validateAddable(section);
        this.values.add(section);
    }

    private void validateAddable(Section section) {
        if (getLast().cannotPrecede(section)) {
            throw new IllegalArgumentException(
                "구간 끝에 추가할 수 없습니다. 기존 하행 구간: " + getLast() + " 추가할 구간: " + section);
        }
    }

    Section getLast() {
        return this.values.get(this.values.size() - 1);
    }
}
