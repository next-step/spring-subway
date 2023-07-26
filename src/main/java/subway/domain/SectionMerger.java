package subway.domain;

import java.util.List;
import java.util.function.BiFunction;

public enum SectionMerger {
    FIRST((relatedSection, addableSection) -> List.of(addableSection, relatedSection)),
    MID_UP((relatedSection, addableSection) -> List.of(addableSection, relatedSection.mergeUpWith(addableSection))),
    MID_DOWN((relatedSection, addableSection) -> List.of(relatedSection.mergeDownWith(addableSection), addableSection)),
    LAST((relatedSection, addableSection) -> List.of(relatedSection, addableSection)),
    ;
    public final BiFunction<Section, Section, List<Section>> function;

    SectionMerger(BiFunction<Section, Section, List<Section>> function) {
        this.function = function;
    }

    public static SectionMerger of(Section relatedSection, Section addableSection) {

        if (relatedSection.getUpStation().equals(addableSection.getUpStation())) {
            return MID_UP;
        }

        if (relatedSection.getDownStation().equals(addableSection.getDownStation())) {
            return MID_DOWN;
        }

        if (relatedSection.getDownStation().equals(addableSection.getUpStation())) {
            return LAST;
        }

        if (relatedSection.getUpStation().equals(addableSection.getDownStation())) {
            return FIRST;
        }

        throw new IllegalArgumentException("추가할 구간의 상행역 하행역이 모두 같거나 모두 다를 수 없습니다. 기존 구간: " + relatedSection + " 추가할 구간: " + addableSection);
    }

    public static List<Section> merge(Section mySection, Section relatedSection) {
        return of(mySection,relatedSection).function.apply(mySection, relatedSection);
    }
}
