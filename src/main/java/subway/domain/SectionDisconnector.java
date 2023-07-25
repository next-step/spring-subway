package subway.domain;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

enum SectionDisconnector {

    UP((baseSection, disconnectStation) ->
            baseSection.getUpStation().equals(disconnectStation) && baseSection.getUpSection() == null),
    MIDDLE((baseSection, disconnectStation) ->
            baseSection.getDownStation().equals(disconnectStation) && baseSection.getDownSection() != null),
    DOWN((baseSection, disconnectStation) ->
            baseSection.getDownStation().equals(disconnectStation) && baseSection.getDownSection() == null),
    ;

    private final BiPredicate<Section, Station> sectionDisconnectorMatcher;

    SectionDisconnector(BiPredicate<Section, Station> sectionDisconnectorMatcher) {
        this.sectionDisconnectorMatcher = sectionDisconnectorMatcher;
    }

    static Optional<SectionDisconnector> findSectionDisConnector(Section baseSection, Station disconnectStation) {
        return Arrays.stream(SectionDisconnector.values())
                .filter(sectionDisconnector -> sectionDisconnector.sectionDisconnectorMatcher.test(baseSection,
                        disconnectStation))
                .findFirst();
    }
}
