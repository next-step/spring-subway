package subway.domain;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

enum SectionDisconnector {
    UP((baseSection, requesStation) -> baseSection.getUpSection() == null &&
            baseSection.getUpStation().equals(requesStation)) {
        @Override
        Section disconnectSection(Section baseSection, Station requestStation) {
            baseSection.getDownSection().disconnectUpSection();
            return null;
        }
    },
    MIDDLE_DOWN((baseSection, requesStation) -> baseSection.getUpSection() != null &&
            baseSection.getUpStation().equals(requesStation)) {
        @Override
        Section disconnectSection(Section baseSection, Station requestStation) {
            baseSection.getUpSection().disconnectMiddleSection();
            return baseSection.getUpSection();
        }
    },
    DOWN((baseSection, requesStation) -> baseSection.getDownSection() == null &&
            baseSection.getDownStation().equals(requesStation)) {
        @Override
        Section disconnectSection(Section baseSection, Station requestStation) {
            baseSection.getUpSection().disconnectDownSection();
            return null;
        }
    },
    ;

    private final BiPredicate<Section, Station> biPredicate;

    SectionDisconnector(BiPredicate<Section, Station> biPredicate) {
        this.biPredicate = biPredicate;
    }

    static Optional<SectionDisconnector> findSectionDisconnector(final Section baseSection, final Station requestStation) {
        return Arrays.stream(SectionDisconnector.values())
                .filter(sectionDisconnector -> sectionDisconnector.biPredicate.test(baseSection, requestStation))
                .findFirst();
    }

    abstract Section disconnectSection(final Section baseSection, final Station requestStation);
}
