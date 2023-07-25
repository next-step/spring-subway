package subway.domain;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

enum SectionConnector {
    UP((baseSection, requestSection) -> baseSection.getUpSection() == null && baseSection.getUpStation()
            .equals(requestSection.getDownStation())),
    MIDDLE_UP((baseSection, requestSection) -> baseSection.getUpStation().equals(requestSection.getUpStation())),
    MIDDLE_DOWN((baseSection, requestSection) -> baseSection.getDownStation().equals(requestSection.getDownStation())),
    DOWN((baseSection, requestSection) -> baseSection.getDownSection() == null
            && baseSection.getDownStation().equals(requestSection.getUpStation())),
    ;

    private final BiPredicate<Section, Section> sectionConnectorMatcher;

    SectionConnector(BiPredicate<Section, Section> sectionConnectorMatcher) {
        this.sectionConnectorMatcher = sectionConnectorMatcher;
    }

    static Optional<SectionConnector> findSectionConnector(Section baseSection, Section requestSection) {
        return Arrays.stream(SectionConnector.values())
                .filter(sectionConnector -> sectionConnector.sectionConnectorMatcher.test(baseSection, requestSection))
                .findFirst();
    }
}
